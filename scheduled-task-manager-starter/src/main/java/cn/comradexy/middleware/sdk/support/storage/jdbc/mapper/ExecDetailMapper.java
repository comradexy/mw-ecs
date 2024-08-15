package cn.comradexy.middleware.sdk.support.storage.jdbc.mapper;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * ExecDetail对象关系映射
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-15
 * @Description: ExecDetail对象关系映射
 */
@Mapper
public interface ExecDetailMapper {
    @Insert("INSERT INTO ecs_exec_detail (`key`, `desc`, cron_expr, job_key, init_time, end_time, last_exec_time, " +
            "exec_count, state) " +
            "VALUES (#{key}, #{desc}, #{cronExpr}, #{jobKey}, #{initTime}, #{endTime}, #{lastExecTime}, " +
            "#{execCount}, #{state})")
    void addExecDetail(ExecDetail execDetail);

    @Delete("DELETE FROM ecs_exec_detail WHERE `key` = #{execDetailKey}")
    void deleteExecDetail(String execDetailKey);

    @Update("UPDATE ecs_exec_detail " +
            "SET `desc` = #{desc}, cron_expr = #{cronExpr}, job_key = #{jobKey}, init_time = #{initTime}, " +
            "end_time = #{endTime}, last_exec_time = #{lastExecTime}, exec_count = #{execCount}, state = #{state} " +
            "WHERE `key` = #{key}")
    void updateExecDetail(ExecDetail execDetail);

    @Select("SELECT `key`, `desc`, cron_expr, job_key, init_time, end_time, last_exec_time, exec_count, state " +
            "FROM ecs_exec_detail WHERE `key` = #{execDetailKey}")
    ExecDetail getExecDetail(String execDetailKey);

    @Select("SELECT `key`, `desc`, cron_expr, job_key, init_time, end_time, last_exec_time, exec_count, state " +
            "FROM ecs_exec_detail")
    List<ExecDetail> listExecDetails();
}
