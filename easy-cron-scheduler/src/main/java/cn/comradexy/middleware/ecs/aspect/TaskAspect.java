package cn.comradexy.middleware.ecs.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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
public class TaskAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(cn.comradexy.middleware.ecs.annotation.EzScheduled)")
    public void aopPoint() {
    }

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint jp) throws Throwable {
        long begin = System.currentTimeMillis();
        Method method = getMethod(jp);
        try {
            logger.info("[EasyCronScheduler] Task start, class: {}, method: {}",
                    jp.getTarget().getClass().getSimpleName(), method.getName());
            return jp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            logger.info("[EasyCronScheduler] Task end, class: {}, method: {}, cost: {}ms",
                    jp.getTarget().getClass().getSimpleName(), method.getName(), end - begin);
        }
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return getClass(jp).getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    private Class<?> getClass(JoinPoint jp) {
        return jp.getTarget().getClass();
    }
}
