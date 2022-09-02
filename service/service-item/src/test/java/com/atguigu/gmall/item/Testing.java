package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

/**
 * @author Connor
 * @date 2022/9/2
 */
public class Testing {
    @Test
    public void testLast() {
        String trim = new Scanner(System.in).next().trim();
        String substring = trim.substring(trim.lastIndexOf(" ") + 1);
        System.out.println(substring.toCharArray().length);
    }

    private void outputCount(String str) {
        String newStr = null;
        if (!str.endsWith(" ")) {
            String substring = str.substring(str.lastIndexOf(" ") + 1);
            System.out.println(substring.toCharArray().length);
        } else {
            for (int i = 0; i < str.length() - 1; i++) {
                newStr += str.toCharArray()[i];
            }
            outputCount(newStr);
        }
    }
}
