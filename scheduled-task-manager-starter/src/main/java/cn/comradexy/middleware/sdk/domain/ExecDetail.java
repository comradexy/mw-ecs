package cn.comradexy.middleware.sdk.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 执行细节实体
 * <p>
 * 记录任务执行的细节信息；与Job实体关联：多对一。
 * </p>
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 执行细节实体
 */
@Builder
@Data
public class ExecDetail {
    private String key;

    private String desc;

    private String cronExpr;

    private String jobKey;

    @Builder.Default
    private LocalDateTime initTime = LocalDateTime.now();

    private LocalDateTime endTime;

    private LocalDateTime lastExecTime;

    @Builder.Default
    private Long execCount = 0L;

    @Builder.Default
    private ExecState state = ExecState.INIT;

    @Getter
    public enum ExecState {
        INIT(0, "初始化"),
        RUNNING(1, "运行中"),
        PAUSED(2, "暂停"),
        COMPLETE(3, "完成"),
        ERROR(4, "错误"),
        BLOCKED(5, "阻塞");

        private final int value;
        private final String desc;

        ExecState(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        public static ExecState valueOf(int value) {
            for (ExecState state : values()) {
                if (state.value == value) {
                    return state;
                }
            }
            throw new IllegalArgumentException("对象关系映射失败，未知的任务状态值：" + value);
        }
    }

    public static class ExecSateTypeHandler extends BaseTypeHandler<ExecState> {
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, ExecState parameter, JdbcType jdbcType) throws SQLException {
            ps.setInt(i, parameter.getValue());
        }

        @Override
        public ExecState getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return ExecState.valueOf(rs.getInt(columnName));
        }

        @Override
        public ExecState getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return ExecState.valueOf(rs.getInt(columnIndex));
        }

        @Override
        public ExecState getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return ExecState.valueOf(cs.getInt(columnIndex));
        }
    }
}
