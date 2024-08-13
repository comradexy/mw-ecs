package cn.comradexy.middleware.sdk.admin.controller;

import cn.comradexy.middleware.sdk.domain.Request;
import cn.comradexy.middleware.sdk.domain.Result;
import cn.comradexy.middleware.sdk.admin.service.IScheduleService;
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
@RestController
@RequestMapping("/schedule/api")
public class AdminController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IScheduleService scheduleService;

    @GetMapping("/list")
    public Result<?> list() {
        logger.info("查询所有任务");
        return Result.success(scheduleService.queryAllTasks());
    }

    @GetMapping("/query")
    public Result<?> query(@RequestParam("taskKey") String taskKey) {
        logger.info("查询任务：{}", taskKey);
        return Result.success(scheduleService.queryTask(taskKey));
    }

    @PostMapping("/cancel")
    public Result<?> cancel(@RequestBody Request request) {
        logger.info("取消任务：{}", request.getTaskKey());
        scheduleService.cancelTask(request.getTaskKey());
        return Result.success();
    }

    @PostMapping("/pause")
    public Result<?> pause(@RequestBody Request request) {
        logger.info("暂停任务：{}", request.getTaskKey());
        scheduleService.pasueTask(request.getTaskKey());
        return Result.success();
    }

    @PostMapping("/resume")
    public Result<?> resume(@RequestBody Request request) {
        logger.info("恢复任务：{}", request.getTaskKey());
        scheduleService.resumeTask(request.getTaskKey());
        return Result.success();
    }
}
