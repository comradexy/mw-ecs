package cn.comradexy.middleware.test;

import cn.comradexy.middleware.sdk.domain.ExecDetail;
import cn.comradexy.middleware.sdk.domain.Job;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

/**
 * 单元测试
 *
 * @Author: ComradeXY
 * @CreateTime: 2024-08-09
 * @Description: 单元测试
 */
public class UnitTest {
    @Test
    public void test() {
        Job job = Job.builder()
                .key("key")
                .desc("desc")
                .beanClass(ExecDetail.class.getName())
                .beanName("beanName")
                .methodName("methodName")
                .build();

        String json = JSON.toJSONString(job);
        System.out.println(json);

        job = JSON.parseObject(json, Job.class);
        System.out.println(job);

        try {
            Class<?> clazz = Class.forName(job.getBeanClass());
            System.out.println(ExecDetail.class.equals(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
