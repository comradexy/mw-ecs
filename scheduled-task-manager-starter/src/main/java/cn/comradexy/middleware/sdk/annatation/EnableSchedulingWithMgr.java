package cn.comradexy.middleware.sdk.annatation;

import java.lang.annotation.*;


/**
 * 开启使用管理中心托管的定时任务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-24
 * @Description: 开启使用管理中心托管的定时任务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Import(SchedulingWithMgrConfiguration.class)
@Documented
public @interface EnableSchedulingWithMgr {

}
