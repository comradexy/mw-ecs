package cn.comradexy.middleware.ecs.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 定时任务切面
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-21
 * @Description: 定时任务切面
 */
@Aspect
public class TaskHandlerAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(cn.comradexy.middleware.ecs.annotation.EzScheduled) " +
            "|| @annotation(cn.comradexy.middleware.ecs.annotation.EzSchedules)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        long begin = System.currentTimeMillis();
        try {
            preHandle(jp);
            return jp.proceed();
        } finally {
            postHandle(jp, begin);
        }
    }

    private void preHandle(ProceedingJoinPoint jp) {
        // 自定义扩展
    }

    private void postHandle(ProceedingJoinPoint jp, long begin) throws NoSuchMethodException {
        long end = System.currentTimeMillis();
        logger.info("[EasyCronScheduler] {}.{} successfully executed, cost {}ms",
                getTargetClass(jp).getSimpleName(), getMethod(jp).getName(), end - begin);
    }

    private Class<?> getTargetClass(ProceedingJoinPoint jp) {
        return jp.getTarget().getClass();
    }

    private Method getMethod(ProceedingJoinPoint jp) throws NoSuchMethodException {
        return getTargetClass(jp).getMethod(jp.getSignature().getName(),
                ((MethodSignature) jp.getSignature()).getParameterTypes());
    }
}
