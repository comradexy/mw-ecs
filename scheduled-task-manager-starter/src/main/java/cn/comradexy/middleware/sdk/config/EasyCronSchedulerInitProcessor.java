package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.annatation.EzScheduled;
import cn.comradexy.middleware.sdk.annatation.EzSchedules;
import cn.comradexy.middleware.sdk.common.ScheduleContext;
import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.TaskHandler;
import cn.comradexy.middleware.sdk.domain.TaskKeyGenerator;
import cn.comradexy.middleware.sdk.support.storage.IStorageService;
import cn.comradexy.middleware.sdk.task.ITaskStore;
import cn.comradexy.middleware.sdk.task.IScheduler;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
     * 待处理任务
     */
    private final Set<PendingTask> pendingTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext 不能为null");
        ScheduleContext.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        // 改变执行顺序的优先级，数字越小，优先级越高
        return Integer.MAX_VALUE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
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
        PendingTask pendingTask = new PendingTask();
        pendingTask.setTaskHandler(
                TaskHandler.builder()
                        .beanClassName(bean.getClass().getName())
                        .beanName(beanName)
                        .methodName(method.getName())
                        .build()
        );
        pendingTask.setExecDetail(ExecDetail.builder()
                .cronExpr(ezScheduled.cron())
                .desc(ezScheduled.desc())
                .endTime(ezScheduled.endTime().equals(ScheduleContext.DEFAULT_END_TIME) ?
                        null : LocalDateTime.parse(ezScheduled.endTime()))
                .maxExecCount(ezScheduled.maxExecCount())
                .build());
        pendingTasks.add(pendingTask);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == ScheduleContext.applicationContext) {
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
            ScheduleContext.properties = ScheduleContext.applicationContext
                    .getBean("comradexy-middleware-easy-cron-scheduler-configuration",
                            EasyCronSchedulerConfiguration.class)
                    .getProperties();
            ScheduleContext.scheduler = ScheduleContext.applicationContext
                    .getBean("comradexy-middleware-easy-cron-scheduler", IScheduler.class);
            ScheduleContext.taskStore = ScheduleContext.applicationContext
                    .getBean("comradexy-middleware-job-store", ITaskStore.class);
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
        if (!ScheduleContext.properties.getEnableStorage()) return;

        ScheduleContext.storageService = ScheduleContext.applicationContext
                .getBean("comradexy-middleware-storage-service", IStorageService.class);

        if (ScheduleContext.properties.getStorageType().equals(EasyCronSchedulerProperties.StorageType.JDBC.getValue())) {
            logger.info("初始化存储服务: JDBC");

            // 初始化数据库，创建表（如果不存在）
            try (Statement statement = ScheduleContext.applicationContext
                    .getBean("comradexy-middleware-data-source", DataSource.class)
                    .getConnection()
                    .createStatement()) {
                // 获取 resources 目录下的 schema.sql 文件，并执行
                ClassPathResource resource = new ClassPathResource("data/schema.sql");
                String schemaSql = new String(resource.getInputStream().readAllBytes());
                // 按分号分割每条SQL语句
                String[] sqlStatements = schemaSql.split(";");
                for (String sql : sqlStatements) {
                    if (!sql.trim().isEmpty()) statement.addBatch(sql);
                }
                statement.executeBatch();
            } catch (Exception e) {
                throw new RuntimeException("初始化数据库失败", e);
            }

            // 数据恢复：从数据库中加载任务到缓存中
            ScheduleContext.storageService.recover();
        }

        // TODO: REDIS存储服务

    }

    /**
     * 初始化任务
     */
    private void init_tasks() {
        pendingTasks.forEach(pendingTask -> {
            String execDetailKey =
                    TaskKeyGenerator.getExecDetailKey(ScheduleContext.properties.getSchedulerServerId(),
                            pendingTask.getTaskHandler(), pendingTask.getExecDetail());
            String taskHandlerKey =
                    TaskKeyGenerator.getTaskHandlerKey(ScheduleContext.properties.getSchedulerServerId(),
                            pendingTask.getTaskHandler());

            // 组装ExecDetail
            if (null != ScheduleContext.taskStore.getExecDetail(execDetailKey)) return;
            ExecDetail execDetail = ExecDetail.builder()
                    .key(execDetailKey)
                    .desc(pendingTask.getExecDetail().getDesc())
                    .cronExpr(pendingTask.getExecDetail().getCronExpr())
                    .taskHandlerKey(taskHandlerKey)
                    .endTime(pendingTask.getExecDetail().getEndTime())
                    .maxExecCount(pendingTask.getExecDetail().getMaxExecCount())
                    .build();
            ScheduleContext.taskStore.addExecDetail(execDetail);

            // 组装Job
            if (null != ScheduleContext.taskStore.getTaskHandler(taskHandlerKey)) return;
            String jobDesc = "beanClass: " + pendingTask.getTaskHandler().getBeanClassName() +
                    ", beanName: " + pendingTask.getTaskHandler().getBeanName() +
                    ", methodName: " + pendingTask.getTaskHandler().getMethodName();
            TaskHandler job = TaskHandler.builder()
                    .key(taskHandlerKey)
                    .desc(jobDesc)
                    .beanClassName(pendingTask.getTaskHandler().getBeanClassName())
                    .beanName(pendingTask.getTaskHandler().getBeanName())
                    .methodName(pendingTask.getTaskHandler().getMethodName())
                    .build();
            ScheduleContext.taskStore.addTaskHandler(job);
        });

        // 调度任务
        ScheduleContext.taskStore.getAllExecDetails().forEach(execDetail -> {
            if (execDetail.getState().equals(ExecDetail.ExecState.INIT)) {
                ScheduleContext.scheduler.scheduleTask(execDetail.getKey());
            } else if (execDetail.getState().equals(ExecDetail.ExecState.PAUSED)
                    || execDetail.getState().equals(ExecDetail.ExecState.BLOCKED)
                    || execDetail.getState().equals(ExecDetail.ExecState.RUNNING)) {
                ScheduleContext.scheduler.resumeTask(execDetail.getKey());
            }
        });
    }

    /**
     * 待处理任务
     * <p>
     * ScheduleContext未初始化完成时，任务先存放在这里；
     * TaskHandler和ExecDetail的key在ScheduleContext初始化完成后才能确定。
     * </p>
     */
    @Data
    private static class PendingTask {
        private TaskHandler taskHandler;
        private ExecDetail execDetail;
    }
}
