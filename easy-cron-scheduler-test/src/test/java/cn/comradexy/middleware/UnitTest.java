package cn.comradexy.middleware;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 单元测试
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-20
 * @Description: 单元测试
 */
public class UnitTest {

    @Test
    public void test() throws UnsupportedEncodingException {
        Map<String, ExecDetail> execDetails = new HashMap<>();
        execDetails.put("key1", ExecDetail.builder()
                .key("key1")
                .taskHandlerKey("taskHandlerKey1")
                .desc("desc1")
                .cronExpr("cronExpr1")
                .build());
        System.out.println(execDetails.get("key1").getDesc());

        ExecDetail execDetail = execDetails.get("key1");
        execDetail.setDesc("desc2");

        System.out.println(execDetails.get("key1").getDesc());
    }
}
