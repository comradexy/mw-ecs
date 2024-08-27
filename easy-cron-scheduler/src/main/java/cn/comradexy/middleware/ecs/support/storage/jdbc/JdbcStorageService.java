package cn.comradexy.middleware.ecs.support.storage.jdbc;

import cn.comradexy.middleware.ecs.common.ScheduleContext;
import cn.comradexy.middleware.ecs.domain.ErrorMsg;
import cn.comradexy.middleware.ecs.domain.ExecDetail;
import cn.comradexy.middleware.ecs.domain.TaskHandler;
import cn.comradexy.middleware.ecs.support.storage.IStorageService;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.ErrorMsgMapper;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.ExecDetailMapper;
import cn.comradexy.middleware.ecs.support.storage.jdbc.mapper.TaskHandlerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.sql.Statement;
import java.util.Set;

/**
 * JDBC存储服务
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-12
 * @Description: JDBC存储服务
 */
public class JdbcStorageService implements IStorageService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskHandlerMapper taskHandlerMapper;
    private ExecDetailMapper execDetailMapper;
    private ErrorMsgMapper errorMsgMapper;

    @Autowired
    public void setJobMapper(TaskHandlerMapper taskHandlerMapper) {
        this.taskHandlerMapper = taskHandlerMapper;
    }

    @Autowired
    public void setExecDetailMapper(ExecDetailMapper execDetailMapper) {
        this.execDetailMapper = execDetailMapper;
    }

    @Autowired
    public void setErrorMsgMapper(ErrorMsgMapper errorMsgMapper) {
        this.errorMsgMapper = errorMsgMapper;
    }

    @Override
    public void init() {
        logger.info("[EasyCronScheduler] init storage service: JDBC");

        // 初始化数据库，创建表（如果不存在）
        try (Statement statement = ScheduleContext.applicationContext
                .getBean("comradexy-middleware-data-source", DataSource.class)
                .getConnection()
                .createStatement()) {
            // 获取 resources 目录下的 schema.sql 文件，并执行
            ClassPathResource resource = new ClassPathResource("data/schema.sql");
            String schemaSql = new String(resource.getInputStream().readAllBytes());
            // 按分号分割每条SQL语句
            String[] sqlStatements = schemaSql.split(";");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (Exception e) {
            throw new RuntimeException("[EasyCronScheduler] Init storage service failed", e);
        }
    }

    @Override
    public void insertErrorMsg(String execDetailKey, String errorMsg) {
        errorMsgMapper.insert(execDetailKey, errorMsg);
    }

    @Override
    public ErrorMsg queryErrorMsg(String execDetailKey) {
        return errorMsgMapper.query(execDetailKey);
    }

    @Override
    public Set<ErrorMsg> queryAllErrorMsgs() {
        return errorMsgMapper.queryAll();
    }

    @Override
    public void deleteErrorMsg(String execDetailKey) {
        errorMsgMapper.delete(execDetailKey);
    }

    @Override
    public void insertTaskHandler(TaskHandler job) {
        taskHandlerMapper.addTaskHandler(job);
    }

    @Override
    public void updateJob(TaskHandler job) {
        taskHandlerMapper.updateTaskHandler(job);
    }

    @Override
    public void deleteTaskHandler(String jobKey) {
        taskHandlerMapper.deleteTaskHandler(jobKey);
    }

    @Override
    public void insertExecDetail(ExecDetail execDetail) {
        execDetailMapper.addExecDetail(execDetail);
    }

    @Override
    public void updateExecDetail(ExecDetail execDetail) {
        execDetailMapper.updateExecDetail(execDetail);
    }

    @Override
    public void deleteExecDetail(String execDetailKey) {
        execDetailMapper.deleteExecDetail(execDetailKey);
    }

    @Override
    public Set<TaskHandler> queryAllTaskHandlers() {
        return taskHandlerMapper.queryAllTaskHandlers();
    }

    @Override
    public Set<ExecDetail> queryAllExecDetails() {
        return execDetailMapper.queryAllExecDetails();
    }
}
