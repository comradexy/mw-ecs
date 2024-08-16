package cn.comradexy.middleware.ecs.support.storage.jdbc.mapper;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
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
    @Insert("INSERT IGNORE INTO ecs_exec_detail (`key`, `desc`, cron_expr, task_handler_key, init_time, end_time, " +
            "last_exec_time, exec_count, max_exec_count, `state`) " +
            "VALUES (#{key}, #{desc}, #{cronExpr}, #{taskHandlerKey}, #{initTime}, #{endTime}, #{lastExecTime}, " +
            "#{execCount}, #{maxExecCount}, #{state})")
    void addExecDetail(ExecDetail execDetail);

    @Delete("DELETE FROM ecs_exec_detail WHERE `key` = #{execDetailKey}")
    void deleteExecDetail(String execDetailKey);

    @Update("UPDATE ecs_exec_detail " +
            "SET `desc` = #{desc}, cron_expr = #{cronExpr}, task_handler_key = #{taskHandlerKey}, init_time = " +
            "#{initTime}, end_time = #{endTime}, last_exec_time = #{lastExecTime}, exec_count = #{execCount}, " +
            "max_exec_count = #{maxExecCount}, `state` = #{state} " +
            "WHERE `key` = #{key}")
    void updateExecDetail(ExecDetail execDetail);

    @Select("SELECT `key`, `desc`, cron_expr, task_handler_key, init_time, end_time, last_exec_time, exec_count, " +
            "max_exec_count, `state` " +
            "FROM ecs_exec_detail WHERE `key` = #{execDetailKey}")
    ExecDetail getExecDetail(String execDetailKey);

    @Select("SELECT `key`, `desc`, cron_expr, task_handler_key, init_time, end_time, last_exec_time, exec_count, " +
            "max_exec_count, `state` " +
            "FROM ecs_exec_detail")
    List<ExecDetail> listExecDetails();
}
