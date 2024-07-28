package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.domain.ScheduledTaskMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
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
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
    private final ScheduledTaskMgr scheduledTaskMgr;

    @Nullable
    private String beanName;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private ApplicationContext applicationContext;

    private final Set<Class<?>> nonAnnotatedClasses;
    private final Logger logger;

    public ScheduledWithMgrAnnotationProcessor(TaskScheduler taskScheduler, ScheduledTaskMgr scheduledTaskMgr) {
        Assert.notNull(scheduledTaskMgr, "ScheduledTaskMgr must not be null");
        this.scheduledTaskMgr = scheduledTaskMgr;

        // 底层用ConcurrentHashMap包装，线程安全
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
        this.logger = LoggerFactory.getLogger(ScheduledWithMgrAnnotationProcessor.class);
    }

    @Override
    public int getOrder() {
        // 改变执行顺序的优先级，数字越小，优先级越高
        return Integer.MAX_VALUE;
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
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
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
        if (!this.nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass(targetClass,
                Arrays.asList(ScheduledWithMgr.class, SchedulesWithMgr.class))) {
            return bean;
        }

        // 因为JDK8之后支持重复注解，因此获取具体类型中Method -> @ScheduledWithMgr的集合，最终会封装为多个Task
        Map<Method, Set<ScheduledWithMgr>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MethodIntrospector.MetadataLookup<Set<ScheduledWithMgr>>) method -> {
            Set<ScheduledWithMgr> scheduledAnnotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(method,
                    ScheduledWithMgr.class, SchedulesWithMgr.class);
            return !scheduledAnnotations.isEmpty() ? scheduledAnnotations : null;
        });

        // 解析到类型中不存在@ScheduledWithMgr注解的方法添加到nonAnnotatedClasses缓存
        if (annotatedMethods.isEmpty()) {
            this.nonAnnotatedClasses.add(targetClass);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No @ScheduledWithMgr annotations found on bean class: " + targetClass);
            }
        } else {
            // 处理@ScheduledWithMgr注解的方法，调用processScheduledWithMgr方法登记任务
            annotatedMethods.forEach((method, scheduledAnnotations) -> {
                scheduledAnnotations.forEach((scheduled) -> {
                    this.processScheduledWithMgr(scheduled, method, bean);
                });
            });
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(annotatedMethods.size() + " @ScheduledWithMgr methods processed on bean '" + beanName +
                        "': " + annotatedMethods);
            }
        }

        return bean;
    }

    /**
     * 登记被@ScheduledWithMgr注解修饰的方法
     */
    public void processScheduledWithMgr(ScheduledWithMgr scheduledWithMgr, Method method, Object bean) {
        // TODO: 封装任务，并存放到ScheduledTaskMgr的unresolvedTasks中
        try {
            Runnable runnable = this.createRunnable(bean, method);


        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Encountered invalid @ScheduledWithMgr method '" + method.getName() +
                    "':" + " " + e.getMessage());
        }
    }

    /**
     * 当ApplicationContext无法获取时调用
     */
    @Override
    public void afterSingletonsInstantiated() {
        this.nonAnnotatedClasses.clear();
        if (this.applicationContext == null) {
            this.finishRegistration();
        }
    }

    /**
     * 当ApplicationContext初始化或刷新时调用
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            this.finishRegistration();
        }
    }

    /**
     * 完成最终注册，通过ScheduledTaskMgr调度任务
     */
    private void finishRegistration() {
        // TODO:
    }

    /**
     * 将@ScheduledWithMgr注解的方法包装成ScheduledMethodRunnable
     */
    protected Runnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @ScheduledWithMgr");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }
}
