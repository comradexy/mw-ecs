package cn.comradexy.middleware.ecs.support.storage.jdbc.mapper;

import cn.comradexy.middleware.ecs.domain.ErrorMsg;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Set;

/**
 * ErrorMsg对象关系映射
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-24
 * @Description: ErrorMsg对象关系映射
 */
@Mapper
public interface ErrorMsgMapper {
    @Insert("INSERT IGNORE INTO ecs_error_msg(exec_detail_key, error_msg) VALUES(#{execDetailKey}, #{errorMsg})")
    void insert(String execDetailKey, String errorMsg);

    @Select("SELECT exec_detail_key, error_msg FROM ecs_error_msg WHERE exec_detail_key = #{execDetailKey}")
    ErrorMsg query(String execDetailKey);

    @Delete("DELETE FROM ecs_error_msg WHERE exec_detail_key = #{execDetailKey}")
    void delete(String execDetailKey);

    @Select("SELECT exec_detail_key, error_msg FROM ecs_error_msg")
    Set<ErrorMsg> queryAll();
}
