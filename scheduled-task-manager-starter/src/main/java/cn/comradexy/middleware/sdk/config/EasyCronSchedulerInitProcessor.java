package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.annatation.EzScheduled;
import cn.comradexy.middleware.sdk.annatation.EzSchedules;
import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import cn.comradexy.middleware.sdk.task.JobStore;
import cn.comradexy.middleware.sdk.task.Scheduler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean处理器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-25
 * @Description: Bean处理器
 */
public class EasyCronSchedulerInitProcessor implements BeanPostProcessor, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, Ordered {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 没有@EzScheduled注解的类
     * <p>
     * 考虑到Bean的初始化可能会被设置为并发初始化，
     * 即postProcessAfterInitialization方法可能会被多线程调用，
     * 因此底层用ConcurrentHashMap包装的Set，保证线程安全
     * </p>
     */
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 待处理的任务
     */
    private final Set<Task> pendingTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext 不能为null");
        ScheduleContext.Global.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        // 改变执行顺序的优先级，数字越小，优先级越高
        return Integer.MAX_VALUE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Scheduler) return bean;

        // Bean有可能被CGLIB增强，这个时候要取其父类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        // 判断类是否有EzScheduled注解
        if (this.nonAnnotatedClasses.contains(targetClass) || !AnnotationUtils.isCandidateClass(targetClass,
                Arrays.asList(EzScheduled.class, EzSchedules.class))) {
            return bean;
        }

        // 支持重复注解，获取具体类型中Method -> @EzScheduled的集合
        Map<Method, Set<EzScheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<EzScheduled>>) metadataLookup -> {
                    Set<EzScheduled> annotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                            metadataLookup, EzScheduled.class, EzSchedules.class);
                    return annotations.isEmpty() ? null : annotations;
                });

        if (annotatedMethods.isEmpty()) {
            this.nonAnnotatedClasses.add(targetClass);
        } else {
            annotatedMethods.forEach((method, annotations) -> annotations.forEach((scheduled) -> {
                processEzScheduled(scheduled, method, bean, beanName);
            }));
        }

        return bean;
    }

    public void processEzScheduled(EzScheduled ezScheduled, Method method, Object bean, String beanName) {
        // 解析@EzScheduled修饰的方法
        // 封装成Task，加入待处理任务集合
        // 等配置初始化完成后，再为任务分配key，并调度任务
        Task task = new Task();
        task.setCron(ezScheduled.cron());
        task.setDesc(ezScheduled.desc());
        task.setBeanClassName(bean.getClass().toString());
        task.setBeanName(beanName);
        task.setMethodName(method.getName());
        task.setEndTime(new Date(ezScheduled.endTime()));
        pendingTasks.add(task);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == ScheduleContext.Global.applicationContext) {
            init_config();
            init_dcs();
            init_storage();
            init_tasks();
        }
    }

    /**
     * 初始化配置
     */
    private void init_config() {
        try {
            String configBeanName = "comradexy-middleware-easy-cron-scheduler-configuration";
            EasyCronSchedulerProperties properties = ScheduleContext.Global.applicationContext
                    .getBean(configBeanName, EasyCronSchedulerConfiguration.class)
                    .getEasyCronSchedulerProperties();
            ScheduleContext.Global.schedulerServerId = properties.getSchedulerServerId();
            ScheduleContext.Global.schedulerServerName = properties.getSchedulerServerName();
            ScheduleContext.Global.schedulerPoolSize = properties.getSchedulerPoolSize();
            ScheduleContext.Global.enableStorage = properties.getEnableStorage();
        } catch (Exception e) {
            logger.error("初始化配置异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化分布式服务
     */
    private void init_dcs() {

    }

    /**
     * 初始化存储服务
     */
    private void init_storage() {
        if (!ScheduleContext.Global.enableStorage) return;
        // TODO: 如果开启持久化支持，还需要从数据库中读取任务及执行细节

    }

    /**
     * 初始化任务
     */
    private void init_tasks() {
        pendingTasks.forEach(task -> {
            // 组装Job
            String jobKey = task.getJobKey(ScheduleContext.Global.schedulerServerId);
            if (null == JobStore.getJob(jobKey)) {
                String jobDesc = "beanClass: " + task.getBeanClassName() +
                        ", beanName: " + task.getBeanName() +
                        ", methodName: " + task.getMethodName();
                Job job = Job.builder()
                        .key(task.getJobKey(ScheduleContext.Global.schedulerServerId))
                        .desc(jobDesc)
                        .beanClassName(task.getBeanClassName())
                        .beanName(task.getBeanName())
                        .methodName(task.getMethodName())
                        .build();
                JobStore.addJob(job);
            }

            // 组装ExecDetail
            String execDetailKey = task.getExecDetailKey(ScheduleContext.Global.schedulerServerId);
            if (null == JobStore.getExecDetail(execDetailKey)) {
                ExecDetail execDetail = ExecDetail.builder()
                        .key(execDetailKey)
                        .desc(task.getDesc())
                        .cronExpr(task.getCron())
                        .jobKey(jobKey)
                        .endTime(task.getEndTime())
                        .build();
                JobStore.addExecDetail(execDetail);
            }

            // 调度任务
            String schedulerBeanName = "comradexy-middleware-easy-cron-scheduler";
            Scheduler scheduler = ScheduleContext.Global.applicationContext.getBean(schedulerBeanName, Scheduler.class);
            scheduler.scheduleTask(execDetailKey);
        });

    }

    @Data
    private static class Task {
        private String cron;
        private String desc;
        private String beanClassName;
        private String beanName;
        private String methodName;
        private Date endTime;

        String getJobKey(String appName) {
            return appName + "_" + beanClassName + "_" + beanName + "_" + methodName;
        }

        String getExecDetailKey(String appName) {
            String key = getJobKey(appName) + "_" + cron + "_" + endTime.toString();
            return UUID.fromString(key).toString();
        }
    }
}
