package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.domain.ScheduledTaskMgr;
import cn.comradexy.middleware.sdk.domain.model.valobj.ScheduledTaskMgrEnumVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Bean后置处理器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-25
 * @Description: Bean后置处理器
 */
@Component
@ConditionalOnBean(annotation = EnableSchedulingWithMgr.class) // 没有使用@EnableSchedulingWithMgr注解，就不加载这个类
public class ScheduledWithMgrAnnotationProcessor implements BeanPostProcessor, BeanNameAware, BeanFactoryAware,
        ApplicationContextAware, SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, Ordered {
    @Nullable
    private String beanName;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private ApplicationContext applicationContext;

    /**
     * 定时任务管理器
     */
    private final ScheduledTaskMgr scheduledTaskMgr;

    /**
     * 存放没有@ScheduledWithMgr注解的类
     */
    private final Set<Class<?>> nonAnnotatedClasses;

    /**
     * 存放未解析的cron任务
     */
    private final Set<CronTask> unresolvedCronTasks;

    private final Logger logger;

    public ScheduledWithMgrAnnotationProcessor(ScheduledTaskMgr scheduledTaskMgr) {
        Assert.notNull(scheduledTaskMgr, "ScheduledTaskMgr must not be null");
        this.scheduledTaskMgr = scheduledTaskMgr;

        // 考虑到Bean的初始化可能会被设置为并发初始化，即postProcessAfterInitialization方法可能会被多线程调用
        // 因此底层用ConcurrentHashMap包装的Set，线程安全
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.unresolvedCronTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (beanFactory == null) {
            beanFactory = applicationContext;
        }
    }

    @Override
    public int getOrder() {
        // 改变执行顺序的优先级，数字越小，优先级越高
        return Integer.MAX_VALUE;
    }

    /**
     * Bean初始化之后调用，解析ScheduledWithMgr注解
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 排除AopInfrastructureBean、TaskScheduler、ScheduledExecutorService，防止循环依赖
        if ((bean instanceof AopInfrastructureBean) || (bean instanceof TaskScheduler) || (bean instanceof ScheduledExecutorService)) {
            return bean;
        }

        // 判断类是否有ScheduledWithMgr注解
        // 获取Bean的用户态类型，例如Bean有可能被CGLIB增强，这个时候要取其父类
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        // nonAnnotatedClasses存放着不存在@ScheduledWithMgr注解的类型，缓存起来避免重复判断它是否携带@ScheduledWithMgr注解的方法
        if (!nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass(targetClass,
                Arrays.asList(ScheduledWithMgr.class, SchedulesWithMgr.class))) {
            return bean;
        }

        // 因为JDK8之后支持重复注解，因此获取具体类型中Method -> @ScheduledWithMgr的集合，最终会封装为多个Task
        Map<Method, Set<ScheduledWithMgr>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<ScheduledWithMgr>>) method -> {
                    Set<ScheduledWithMgr> scheduledAnnotations =
                            AnnotatedElementUtils.getMergedRepeatableAnnotations(method,
                                    ScheduledWithMgr.class, SchedulesWithMgr.class);
                    return !scheduledAnnotations.isEmpty() ? scheduledAnnotations : null;
                });

        // 解析到类型中不存在@ScheduledWithMgr注解的方法添加到nonAnnotatedClasses缓存
        if (annotatedMethods.isEmpty()) {
            nonAnnotatedClasses.add(targetClass);
            if (logger.isTraceEnabled()) {
                logger.trace("No @ScheduledWithMgr annotations found on bean class: " + targetClass);
            }
        } else {
            // 处理@ScheduledWithMgr注解的方法，调用processScheduledWithMgr方法登记任务
            annotatedMethods.forEach((method, scheduledAnnotations) -> {
                scheduledAnnotations.forEach((scheduled) -> {
                    processScheduledWithMgr(scheduled, method, bean);
                });
            });
            if (logger.isTraceEnabled()) {
                logger.trace(annotatedMethods.size() + " @ScheduledWithMgr methods processed on bean '" + beanName +
                        "': " + annotatedMethods);
            }
        }

        return bean;
    }

    /**
     * 登记被@ScheduledWithMgr注解修饰的方法
     */
    public void processScheduledWithMgr(ScheduledWithMgr scheduledWithMgr, Method method, Object bean) {
        try {
            // 通过方法宿主Bean和目标方法封装Runnable适配器ScheduledMethodRunnable实例
            Runnable runnable = createRunnable(bean, method);

            // 解析cron和zone，装载为CronTask
            String cron = scheduledWithMgr.cron();
            if (StringUtils.hasText(cron)) {
                String zone = scheduledWithMgr.zone();
                // TODO: 解析占位符
                /*if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }*/
                if (StringUtils.hasLength(cron) && !ScheduledWithMgr.CRON_DISABLED.equals(cron)) {
                    TimeZone timeZone;
                    if (StringUtils.hasText(zone)) {
                        timeZone = StringUtils.parseTimeZoneString(zone);
                    } else {
                        timeZone = TimeZone.getDefault();
                    }
                    // 由于ScheduledTaskMgr和TaskScheduler可能还未初始化，先把任务添加到缓存中
                    unresolvedCronTasks.add(new CronTask(runnable, new CronTrigger(cron, timeZone)));
                }
            }
        } catch (
                IllegalArgumentException ex) {
            throw new IllegalStateException("Encountered invalid @ScheduledWithMgr method '" + method.getName() +
                    "':" + " " + ex.getMessage());
        }
    }

    /**
     * 将@ScheduledWithMgr注解的方法包装成ScheduledMethodRunnable
     */
    protected Runnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @ScheduledWithMgr");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }

    /**
     * 当ApplicationContext无法获取时调用
     */
    @Override
    public void afterSingletonsInstantiated() {
        nonAnnotatedClasses.clear();
        if (applicationContext == null) {
            finishRegistration();
        }
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
        // TODO: 通过ScheduledTaskMgr调度任务
    }

    /**
     * 从BeanFactory中解析TaskScheduler
     */
    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> schedulerType, boolean byName) {
        if (byName) {
            T scheduler = beanFactory.getBean(ScheduledTaskMgrEnumVO.DEFAULT_SCHEDULED_TASK_MGR_SERVICE_BEAN_NAME,
                    schedulerType);
            if (beanName != null && beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory) beanFactory).registerDependentBean("taskScheduler", beanName);
            }

            return scheduler;
        } else if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder<T> holder = ((AutowireCapableBeanFactory) beanFactory).resolveNamedBean(schedulerType);
            if (beanName != null && beanFactory instanceof ConfigurableBeanFactory) {
                ((ConfigurableBeanFactory) beanFactory).registerDependentBean(holder.getBeanName(), beanName);
            }

            return holder.getBeanInstance();
        } else {
            return beanFactory.getBean(schedulerType);
        }
    }

}
