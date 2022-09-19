package com.atguigu.gmall.pay;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

/**
 * @author Connor
 * @date 2022/9/19
 */
public class CodeTest {
    @Test
    public void testPrimeNum() {
        int num = new Scanner(System.in).nextInt();
        System.out.println(num + "范围内有" + getPrimeNumCount(num) + "个质数");
    }

    private int getPrimeNumCount(int num) {
        int sum = 0;
        for (int i = 2; i < num; i++) {
            //假设当前数字是质数
            boolean flag = false;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    //当前数字可以被1和自身之外的数整除
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                sum++;
            }
        }
        return sum;
    }
}
