package cn.comradexy.middleware.sdk.task;

import cn.comradexy.middleware.sdk.common.ServiceResponseStatusVO;
import cn.comradexy.middleware.sdk.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定时任务调度器
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务调度器
 */
public class Scheduler implements IScheduler, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskScheduler taskScheduler;

    /**
     * 待执行的任务
     */
    private final Map<String, CronTask> pendingTasks = new ConcurrentHashMap<>(64);

    /**
     * 已被调度的任务
     */
    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(64);

    public void setTaskScheduler(TaskScheduler taskScheduler) {
        // 允许自定义TaskScheduler并注入
        // 也可以声明TaskScheduler为Bean，ScheduledWithMgrAnnotationProcessor中会识别并将其注入
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");

        // 检查scheduledTasks是否为空，如果不为空，说明已经有任务在运行，不允许更换TaskScheduler
        if (!scheduledTasks.isEmpty()) {
            logger.warn("已有任务在运行，不允许更换TaskScheduler");
            return;
        }

        this.taskScheduler = taskScheduler;
    }

    public boolean hasTaskScheduler() {
        return taskScheduler != null;
    }

    @Override
    public Result<String> createTask(String cronExpr, Runnable taskHandler) {
        try {
            // 校验cron表达式合法性
            if (!CronExpression.isValidExpression(cronExpr)) {
                logger.error("cron表达式[{}]不合法", cronExpr);
                return Result.failed(ServiceResponseStatusVO.CRON_INVALID);
            }

            // 生成任务ID
            // TODO: 唯一ID生成策略：UUID、雪花算法、自增，提供默认实现，同时支持自定义ID生成策略
            String taskId = UUID.randomUUID().toString();

            // 创建定时任务
            ScheduledTask scheduledTask = new ScheduledTask();
            scheduledTask.future = taskScheduler.schedule(taskHandler, new CronTrigger(cronExpr));

            // 保存定时任务
            scheduledTasks.put(taskId, scheduledTask);

            logger.info("任务[{}]已启动", taskId);
            return Result.success(taskId);
        } catch (Exception e) {
            logger.error("启动任务失败", e);
            return Result.failed(ServiceResponseStatusVO.FAILED, e.getMessage());
        }
    }

    @Override
    public Result<String> cancelTask(String taskId) {
        try {
            ScheduledTask scheduledTask = scheduledTasks.get(taskId);
            if (null != scheduledTask) {
                scheduledTask.cancel(true); // 取消定时任务
                scheduledTasks.remove(taskId); // 从任务列表中移除
                logger.info("任务[{}]已停止", taskId);
                return Result.success();
            } else {
                logger.warn("任务[{}]不存在", taskId);
                return Result.failed(ServiceResponseStatusVO.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("停止任务[{}]失败", taskId, e);
            return Result.failed(ServiceResponseStatusVO.FAILED, e.getMessage());
        }
    }


    @Override
    public void destroy() {
        // 销毁时，停止所有任务
        scheduledTasks.forEach((taskId, scheduledFuture) -> {
            scheduledFuture.cancel(true);
            logger.info("任务[{}]已停止", taskId);
        });
    }
}
