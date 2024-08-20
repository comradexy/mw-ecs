package cn.comradexy.middleware.ecs.support.storage.jdbc.mapper;

import cn.comradexy.middleware.ecs.domain.TaskHandler;
import org.apache.ibatis.annotations.*;

import java.util.Set;

/**
 * Job对象关系映射
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-15
 * @Description: Job对象关系映射
 */
@Mapper
public interface TaskHandlerMapper {
    @Insert("INSERT IGNORE INTO ecs_task_handler(`key`, `desc`, bean_class_name, bean_name, method_name) " +
            "VALUES(#{key}, #{desc}, #{beanClassName}, #{beanName}, #{methodName})")
    void addTaskHandler(TaskHandler taskHandler);

    @Delete("DELETE FROM ecs_task_handler WHERE `key` = #{key}")
    void deleteTaskHandler(String taskHandlerKey);

    @Update("UPDATE ecs_task_handler " +
            "SET `desc` = #{desc}, bean_class_name = #{beanClassName}, bean_name = #{beanName}, method_name = " +
            "#{methodName} " +
            "WHERE `key` = #{key}")
    void updateTaskHandler(TaskHandler taskHandler);

    @Select("SELECT `key`, `desc`, bean_class_name, bean_name, method_name " +
            "FROM ecs_task_handler WHERE `key` = #{taskHandlerKey}")
    TaskHandler getTaskHandler(String taskHandlerKey);

    @Select("SELECT `key`, `desc`, bean_class_name, bean_name, method_name " +
            "FROM ecs_task_handler")
    Set<TaskHandler> queryAllTaskHandlers();
}
