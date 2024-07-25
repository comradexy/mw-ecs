package cn.comradexy.middleware.sdk.annatation;

import cn.comradexy.middleware.sdk.domain.ScheduledTaskMgrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;

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
public class ScheduledWithMgrAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware,
        SmartInitializingSingleton {
    @Nullable
    private ApplicationContext applicationContext;
    @Nullable
    private ScheduledTaskMgrService scheduledTaskMgrService;

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private final Logger logger = LoggerFactory.getLogger(ScheduledWithMgrAnnotationProcessor.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scheduledTaskMgrService = applicationContext.getBean(ScheduledTaskMgrService.class);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // TODO: 不太懂，待研究
        // 排除AopInfrastructureBean、TaskScheduler、ScheduledExecutorService，防止循环依赖
        if (!(bean instanceof AopInfrastructureBean) && !(bean instanceof TaskScheduler) && !(bean instanceof ScheduledExecutorService)) {
            // 判断类是否有ScheduledWithMgr注解
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
            if (!this.nonAnnotatedClasses.contains(targetClass) && AnnotationUtils.isCandidateClass(targetClass,
                    ScheduledWithMgr.class)) {
                // 获取类中所有方法
                Map<Method, Set<ScheduledWithMgr>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                        (MethodIntrospector.MetadataLookup<Set<ScheduledWithMgr>>) method -> {
                            // 获取方法上的ScheduledWithMgr注解
                            Set<ScheduledWithMgr> scheduledWithMgrAnnotations =
                                    AnnotatedElementUtils.getMergedRepeatableAnnotations(method,
                                            ScheduledWithMgr.class);
                            return !scheduledWithMgrAnnotations.isEmpty() ? scheduledWithMgrAnnotations : null;
                        });
                if (annotatedMethods.isEmpty()) {
                    this.nonAnnotatedClasses.add(targetClass);
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
                    }
                } else {
                    annotatedMethods.forEach((method, scheduledAnnotations) -> {
                        scheduledAnnotations.forEach((scheduled) -> {
                            this.processScheduledWithMgr(scheduled, method, bean);
                        });
                    });
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods);
                    }
                }
            }

            return bean;
        } else {
            return bean;
        }
    }

    /**
     * 处理scheduledWithMgr注解
     */
    public void processScheduledWithMgr(ScheduledWithMgr scheduledWithMgr, Method method, Object bean) {
        // TODO: 调用ScheduledTaskMgrService的createTask方法创建定时任务

    }

    /**
     * 在所有单例实例化完成后执行
     */
    @Override
    public void afterSingletonsInstantiated() {
        // TODO: 注册定时任务
        // 清空非注解类集合
        this.nonAnnotatedClasses.clear();
    }
}
