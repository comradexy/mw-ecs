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

    @Builder.Default
    private Date startTime = new Date();

    private Date endTime;

    private String cronExpr;

    private String jobKey;

    @Builder.Default
    private Integer state = ExecState.INIT.getKey();

    @Getter
    public enum ExecState {
        INIT(0, "初始化"),
        RUNNING(1, "运行中"),
        PAUSED(2, "暂停"),
        COMPLETE(3, "完成"),
        ERROR(4, "错误"),
        BLOCKED(5, "阻塞");

        private final int key;
        private final String desc;

        ExecState(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }
    }
}
