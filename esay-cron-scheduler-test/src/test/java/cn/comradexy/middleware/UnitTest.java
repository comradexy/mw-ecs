package cn.comradexy.middleware;

import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

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
        byte[] a = {'1', '2', '3'};
        String b = "UTF-8";
        String c = new String(a, b);
        System.out.println(c);
    }
}
