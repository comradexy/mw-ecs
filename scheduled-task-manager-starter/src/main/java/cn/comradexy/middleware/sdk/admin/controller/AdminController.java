package cn.comradexy.middleware.sdk.admin.controller;

import cn.comradexy.middleware.sdk.admin.service.IScheduleService;
import cn.comradexy.middleware.sdk.domain.Request;
import cn.comradexy.middleware.sdk.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 管理端controller
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-13
 * @Description: 管理端controller
 */
@ResponseBody
@CrossOrigin
@RequestMapping("/schedule/api")
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IScheduleService scheduleService;

    @GetMapping("/list")
    public Result<?> list() {
        try {
            logger.info("查询所有任务");
            return Result.success(scheduleService.queryAllTasks());
        } catch (Exception e) {
            logger.error("请求失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/query")
    public Result<?> query(@RequestParam("taskKey") String taskKey) {
        try {
            logger.info("查询任务：{}", taskKey);
            return Result.success(scheduleService.queryTask(taskKey));
        } catch (Exception e) {
            logger.error("请求失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public Result<?> cancel(@RequestBody Request request) {
        try {
            logger.info("取消任务：{}", request.getTaskKey());
            scheduleService.cancelTask(request.getTaskKey());
            return Result.success();
        } catch (Exception e) {
            logger.error("请求失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/pause")
    public Result<?> pause(@RequestBody Request request) {
        try {
            logger.info("暂停任务：{}", request.getTaskKey());
            scheduleService.pasueTask(request.getTaskKey());
            return Result.success();
        } catch (Exception e) {
            logger.error("请求失败", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/resume")
    public Result<?> resume(@RequestBody Request request) {
        try {
            logger.info("恢复任务：{}", request.getTaskKey());
            scheduleService.resumeTask(request.getTaskKey());
            return Result.success();
        } catch (Exception e) {
            logger.error("请求失败", e);
            return Result.fail(e.getMessage());
        }
    }
}
