package cn.comradexy.middleware.sdk.config;

import cn.comradexy.middleware.sdk.annatation.EzScheduled;
import cn.comradexy.middleware.sdk.annatation.EzSchedules;
import cn.comradexy.middleware.sdk.task.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Bean处理器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-25
 * @Description: Bean处理器
 */
public class EasyCronSchedulerInitProcessor implements BeanPostProcessor, ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent>, Ordered {
    private ApplicationContext applicationContext;

    /**
     * 定时任务管理器
     */
    private Scheduler scheduledTaskMgr;

    /**
     * 存放没有@EzScheduled注解的类
     * <p>
     * 考虑到Bean的初始化可能会被设置为并发初始化，
     * 即postProcessAfterInitialization方法可能会被多线程调用，
     * 因此底层用ConcurrentHashMap包装的Set，保证线程安全
     * </p>
     */
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 存放未解析的cron任务
     */
    private final Set<CronTask> unresolvedCronTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Lazy
    public void setScheduledTaskMgr(Scheduler scheduledTaskMgr) {
        Assert.notNull(scheduledTaskMgr, "ScheduledTaskMgr must not be null");
        this.scheduledTaskMgr = scheduledTaskMgr;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext, "ApplicationContext must not be null");
        this.applicationContext = applicationContext;
    }

    /**
     * 改变执行顺序的优先级，数字越小，优先级越高
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    /**
     * Bean初始化之后调用，解析EzScheduled注解
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 防止循环依赖
        if (bean instanceof Scheduler) {
            return bean;
        }

        // 获取Bean的用户态类型，例如Bean有可能被CGLIB增强，这个时候要取其父类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        // 判断类是否有EzScheduled注解
        // nonAnnotatedClasses存放着不存在@EzScheduled注解的类型，缓存起来避免重复判断它是否携带@EzScheduled注解的方法
        if (nonAnnotatedClasses.contains(targetClass) || !AnnotationUtils.isCandidateClass(targetClass,
                Arrays.asList(EzScheduled.class, EzSchedules.class))) {
            return bean;
        }

        // 支持重复注解，获取具体类型中Method -> @EzScheduled的集合，最终会封装为多个Task
        Map<Method, Set<EzScheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<EzScheduled>>) method -> {
                    Set<EzScheduled> scheduledAnnotations =
                            AnnotatedElementUtils.getMergedRepeatableAnnotations(method,
                                    EzScheduled.class, EzSchedules.class);
                    return !scheduledAnnotations.isEmpty() ? scheduledAnnotations : null;
                });

        // 解析到类型中不存在@EzScheduled注解的方法添加到nonAnnotatedClasses缓存
        if (annotatedMethods.isEmpty()) {
            nonAnnotatedClasses.add(targetClass);
            if (logger.isTraceEnabled()) {
                logger.trace("No @EzScheduled annotations found on bean class: " + targetClass);
            }
        } else {
            // 处理@EzScheduled注解的方法，调用processEzScheduled方法登记任务
            annotatedMethods.forEach((method, scheduledAnnotations) -> {
                scheduledAnnotations.forEach((scheduled) -> {
                    processEzScheduled(scheduled, method, bean);
                });
            });
            if (logger.isTraceEnabled()) {
                logger.trace(annotatedMethods.size() + " @EzScheduled methods processed on bean '" + beanName +
                        "': " + annotatedMethods);
            }
        }

        return bean;
    }

    /**
     * 登记被@EzScheduled注解修饰的方法
     */
    public void processEzScheduled(EzScheduled ezScheduled, Method method, Object bean) {
        // TODO: 解析@EzScheduled注解的方法，转化为Job和ExecDetail，并存放到JobStore中
        //  如果开启持久化支持，还需要从数据库中读取任务及执行细节

        try {
            // 通过方法宿主Bean和目标方法封装Runnable适配器ScheduledMethodRunnable实例
            Runnable runnable = createRunnable(bean, method);

            // 解析cron，装载为CronTask
            String cron = ezScheduled.cron();
            if (CronExpression.isValidExpression(cron)) {
                // 由于ScheduledTaskMgr和TaskScheduler可能还未初始化，先把任务添加到缓存中
                unresolvedCronTasks.add(new CronTask(runnable, new CronTrigger(cron)));
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Encountered invalid @EzScheduled method '" + method.getName() +
                    "':" + " " + ex.getMessage());
        }
    }

    /**
     * 将@EzScheduled注解的方法包装成ScheduledMethodRunnable
     */
    private Runnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @EzScheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }

    /**
     * 当ApplicationContext初始化或刷新时调用
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == applicationContext) {
            finishRegistration();
        }
    }

    /**
     * 完成最终注册，通过ScheduledTaskMgr调度任务
     */
    private void finishRegistration() {
//        if (!scheduledTaskMgr.hasTaskScheduler()) {
//            // 通过ApplicationContext获取所有TaskScheduler实例
//            // 先根据类型获取，再根据名称获取
//            try {
//                scheduledTaskMgr.setTaskScheduler(applicationContext.getBean(TaskScheduler.class));
//            } catch (NoUniqueBeanDefinitionException ex) {
//                logger.trace("Could not find unique TaskScheduler bean", ex);
//                try {
//                    scheduledTaskMgr.setTaskScheduler(applicationContext.getBean(TaskScheduler.class, "taskScheduler"));
//                } catch (NoSuchBeanDefinitionException ex2) {
//                    logger.info("More than one TaskScheduler bean exists within the context, and " +
//                            "none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler'.");
//                }
//            } catch (NoSuchBeanDefinitionException ex) {
//                logger.trace("Could not find TaskScheduler bean", ex);
//                logger.info("No TaskScheduler bean found within the context. " +
//                        "Consider defining a bean of type TaskScheduler, or mark one of them as primary.");
//            }
//        }
//
//        // 如果scheduledTaskMgr还有装配TaskScheduler实例，说明没有配置TaskScheduler Bean
//        if (!scheduledTaskMgr.hasTaskScheduler()) {
//            // 默认使用单线程调度器
//            scheduledTaskMgr.setTaskScheduler(new ConcurrentTaskScheduler(Executors.newSingleThreadScheduledExecutor()));
//        }

        // 调度所有已登记的任务
        scheduleTasks();
    }

    private void scheduleTasks() {
        // 调度所有装载完毕的CronTask
        if (!unresolvedCronTasks.isEmpty()) {
            for (CronTask cronTask : unresolvedCronTasks) {
                // 通过scheduledTaskMgr托管
//                scheduledTaskMgr.scheduleTask(cronTask.getExpression(), cronTask.getRunnable());
            }
        }
    }
}
