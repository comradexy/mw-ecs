package cn.comradexy.middleware.sdk.domain;

import cn.comradexy.middleware.sdk.domain.model.entity.Result;
import cn.comradexy.middleware.sdk.domain.model.entity.ScheduledTaskVO;
import cn.comradexy.middleware.sdk.domain.model.valobj.ServiceResponseStatusVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 定时任务管理服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理服务
 */
public class ScheduledTaskMgr implements IScheduledTaskMgr, DisposableBean {
    /**
     * 定时任务调度器
     */
    private TaskScheduler taskScheduler;

    /**
     * 定时任务列表(缓存)
     */
    private final Map<String, ScheduledFuture<?>> scheduledTasks;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduledTaskMgr() {
        this.scheduledTasks = new ConcurrentHashMap<>(64);
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        Assert.notNull(taskScheduler, "TaskScheduler must not be null");
        this.taskScheduler = taskScheduler;
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
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(taskHandler, new CronTrigger(cronExpr));

            // 保存定时任务
            scheduledTasks.put(taskId, scheduledFuture);

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
            ScheduledFuture<?> scheduledFuture = scheduledTasks.get(taskId);
            if (null != scheduledFuture) {
                scheduledFuture.cancel(true); // 取消定时任务
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
    public ServiceResponseStatusVO pauseTask(String taskId) {
        // TODO: 方案一：使用持久化存储保存任务状态，暂停逻辑为：
        //  先持久化任务，再取消任务，任务恢复时，根据任务状态恢复任务
        return null;
    }

    @Override
    public ServiceResponseStatusVO resumeTask(String taskId) {

        return null;
    }

    @Override
    public ServiceResponseStatusVO updateTask(String taskId, String cronExpr, Runnable taskHandler) {

        return null;
    }

    @Override
    public List<ScheduledTaskVO> listTasks() {
        try {

            return null;
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public ScheduledTaskVO getTask(String taskId) {

        return null;
    }

    /**
     * 任务相似性检测
     *
     * @param taskId 任务ID
     * @return 相似任务列表
     */
    private List<String> similarTaskDetection(String taskId) {
        // TODO: 任务相似性检测
        // 1. cron表达式相似度计算
        // 2. 任务处理器相似度计算
        // 3. 查询相似任务，返回相似任务ID列表

        return null;
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
