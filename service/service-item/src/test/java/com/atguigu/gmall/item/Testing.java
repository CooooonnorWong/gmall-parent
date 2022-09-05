package com.atguigu.gmall.item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @date 2022/9/2
 */
public class Testing {
    //    @Test
//    public void testLast() {
//        String trim = new Scanner(System.in).next();
//        int length = 0;
//        int index = trim.length() - 1;
//        char[] chars = trim.toCharArray();
//        while (index >= 0 && chars[index] != ' ') {
//            length++;
//            index--;
//        }
//        System.out.println(length);
//    }
    public static void main1(String[] args) throws IOException {
//        decimal(cache(), new Scanner(System.in));
//        mingmingRandomNum();
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String str = read.readLine();
        HashMap<Character,Integer> map = new HashMap<>();
        for(char ch : str.toCharArray()){
            if(!map.containsKey(ch)){
                map.put(ch,1);
            }else{
                map.put(ch,map.get(ch)+1);
            }
        }

    }

    private static void mingmingRandomNum() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int amount = Integer.parseInt(br.readLine());
        while (amount > 1000 || amount < 1) {
            amount = Integer.parseInt(br.readLine());
        }
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            int num = Integer.parseInt(br.readLine());
            while (num < 1 || num > 500) {
                num = Integer.parseInt(br.readLine());
            }
            set.add(num);
        }
        List<Integer> sorted = set.stream().sorted((n1, n2) -> n1 - n2).collect(Collectors.toList());
        sorted.forEach(System.out::println);
    }

    private static void decimal(Map<Character, Integer> cache, Scanner scanner) {
        if (scanner.hasNext()) {
            String next = scanner.next().substring(2);
            int decimal = 0;
            for (char c : next.toCharArray()) {
                decimal = decimal * 16 + cache.get(c);
            }
            System.out.println(decimal);
        }
    }

    private static Map<Character, Integer> cache() {
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('0', 0);
        map.put('1', 1);
        map.put('2', 2);
        map.put('3', 3);
        map.put('4', 4);
        map.put('5', 5);
        map.put('6', 6);
        map.put('7', 7);
        map.put('8', 8);
        map.put('9', 9);
        map.put('A', 10);
        map.put('B', 11);
        map.put('C', 12);
        map.put('D', 13);
        map.put('E', 14);
        map.put('F', 15);
        return map;
    }

}
