package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.domain.ScheduledTaskMgrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
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
public class ScheduledWithMgrAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware,
        SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent> {
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private ScheduledTaskMgrService scheduledTaskMgrService;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private final Logger logger = LoggerFactory.getLogger(ScheduledWithMgrAnnotationProcessor.class);

    public void setScheduledTaskMgrService(ScheduledTaskMgrService scheduledTaskMgrService) {
        this.scheduledTaskMgrService = scheduledTaskMgrService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // TODO: 不太懂，待研究
        logger.info("bean-{}: 执行postProcessAfterInitialization", beanName);
//        // 排除AopInfrastructureBean、TaskScheduler、ScheduledExecutorService，防止循环依赖
//        if (!(bean instanceof AopInfrastructureBean) && !(bean instanceof TaskScheduler) && !(bean instanceof
//        ScheduledExecutorService)) {
//            // 判断类是否有ScheduledWithMgr注解
//            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
//            if (!this.nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass(targetClass,
//                    ScheduledWithMgr.class)) {
//                // 获取类中所有方法
//                Map<Method, Set<ScheduledWithMgr>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
//                        (MethodIntrospector.MetadataLookup<Set<ScheduledWithMgr>>) method -> {
//                            // 获取方法上的ScheduledWithMgr注解
//                            Set<ScheduledWithMgr> scheduledWithMgrAnnotations =
//                                    AnnotatedElementUtils.getMergedRepeatableAnnotations(method,
//                                            ScheduledWithMgr.class);
//                            return !scheduledWithMgrAnnotations.isEmpty() ? scheduledWithMgrAnnotations : null;
//                        });
//                if (annotatedMethods.isEmpty()) {
//                    this.nonAnnotatedClasses.add(targetClass);
//                    if (this.logger.isTraceEnabled()) {
//                        this.logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
//                    }
//                } else {
//                    annotatedMethods.forEach((method, scheduledAnnotations) -> {
//                        scheduledAnnotations.forEach((scheduled) -> {
//                            this.processScheduledWithMgr(scheduled, method, bean);
//                        });
//                    });
//                    if (this.logger.isTraceEnabled()) {
//                        this.logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" +
//                        beanName + "': " + annotatedMethods);
//                    }
//                }
//            }
//
//            return bean;
//        } else {
//            return bean;
//        }
        return bean;
    }

    /**
     * 处理scheduledWithMgr注解
     */
    public void processScheduledWithMgr(ScheduledWithMgr scheduledWithMgr, Method method, Object bean) {
        // TODO: 调用ScheduledTaskMgrService的createTask方法创建定时任务
        // 需要现在用TaskScheduler创建定时任务，然后在afterSingletonsInstantiated方法中注册定时任务（分配任务ID等）
        try {
            Runnable runnable = this.createRunnable(bean, method);


        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Encountered invalid @ScheduledWithMgr method '" + method.getName() + "':" +
                    " " + e.getMessage());
        }
    }

    /**
     * 在所有单例实例化完成后执行
     */
    @Override
    public void afterSingletonsInstantiated() {
        // TODO: 注册定时任务
        // 清空非注解类集合
        this.nonAnnotatedClasses.clear();
        if (this.applicationContext == null) {
            // 与onApplicationEvent方法互斥，即两个方法中只有一个会执行
            // this.applicationContext为空时，执行afterSingletonsInstantiated
            // this.applicationContext不为空时，执行onApplicationEvent
            logger.info("执行afterSingletonsInstantiated");
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
     * 容器刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // TODO: 容器刷新事件
        if (event.getApplicationContext() == this.applicationContext) {
            logger.info("容器刷新事件");
        }

    }
}
