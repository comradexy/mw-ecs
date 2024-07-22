package cn.comradexy.middleware.sdk.domain;

/**
 * @Author: ComradeXY
 * @CreateTime: 2024-07-22
 * @Description: 定时任务管理服务
 */
public class ScheduledTaskMgrService implements IScheduledTaskMgrService{
    @Override
    public void startTask(String taskId, String cronExpr, Runnable taskHandler) {
        System.out.println("startTask");
    }

    @Override
    public void stopTask(String taskId) {
        System.out.println("stopTask");
    }

    @Override
    public void updateTask(String taskId, String cronExpr, Runnable taskHandler) {
        System.out.println("updateTask");
    }

    @Override
    public void pauseTask(String taskId) {
        System.out.println("pauseTask");
    }

    @Override
    public void resumeTask(String taskId) {
        System.out.println("resumeTask");
    }

    @Override
    public void listTasks() {
        System.out.println("listTasks");
    }

    @Override
    public void getTask(String taskId) {
        System.out.println("getTask");
    }
}
