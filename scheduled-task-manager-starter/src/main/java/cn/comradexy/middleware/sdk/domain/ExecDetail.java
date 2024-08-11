package cn.comradexy.middleware.sdk.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

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
    private Date initTime = new Date();

    private Date endTime;

    private Date lastExecTime;

    @Builder.Default
    private Long execCount = 0L;

    @Builder.Default
    private ExecState state = ExecState.INIT;

    @Getter
    public enum ExecState {
        INIT("初始化"),
        RUNNING("运行中"),
        PAUSED("暂停"),
        COMPLETE("完成"),
        ERROR("错误"),
        BLOCKED("阻塞");

        private final String desc;

        ExecState(String desc) {
            this.desc = desc;
        }
    }
}
