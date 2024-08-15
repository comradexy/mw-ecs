package cn.comradexy.middleware.sdk.support.storage.jdbc.mapper;

import cn.comradexy.middleware.sdk.domain.Job;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Job对象关系映射
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-15
 * @Description: Job对象关系映射
 */
@Mapper
public interface JobMapper {
    @Insert("INSERT INTO ecs_job (`key`, `desc`, bean_class_name, bean_name, method_name) " +
            "VALUES (#{key}, #{desc}, #{beanClassName}, #{beanName}, #{methodName})")
    void addJob(Job job);

    @Delete("DELETE FROM ecs_job WHERE `key` = #{key}")
    void deleteJob(String jobKey);

    @Update("UPDATE ecs_job " +
            "SET `desc` = #{desc}, bean_class_name = #{beanClassName}, bean_name = #{beanName}, method_name = #{methodName} " +
            "WHERE `key` = #{key}")
    void updateJob(Job job);

    @Select("SELECT `key`, `desc`, bean_class_name, bean_name, method_name " +
            "FROM ecs_job WHERE `key` = #{jobKey}")
    Job getJob(String jobKey);

    @Select("SELECT `key`, `desc`, bean_class_name, bean_name, method_name " +
            "FROM ecs_job")
    List<Job> listJobs();
}
