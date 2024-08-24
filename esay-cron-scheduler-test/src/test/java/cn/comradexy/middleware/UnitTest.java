package cn.comradexy.middleware;

import cn.comradexy.middleware.ecs.domain.ExecDetail;
import org.apache.commons.lang.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
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
        Set<ExecDetail> execDetails = new HashSet<>();
        execDetails.add(ExecDetail.builder()
                .key("key1")
                .taskHandlerKey("taskHandlerKey1")
                .desc("desc1")
                .cronExpr("cronExpr1")
                .build());
        execDetails.forEach(System.out::println);

        Set<ExecDetail> execDetails2 = new HashSet<>();
        execDetails.forEach(execDetail -> execDetails2.add((ExecDetail) SerializationUtils.clone(execDetail)));
        execDetails2.forEach(execDetail -> execDetail.setKey(execDetail.getKey() + "_2"));
        execDetails2.forEach(System.out::println);

        execDetails.forEach(System.out::println);
    }
}
