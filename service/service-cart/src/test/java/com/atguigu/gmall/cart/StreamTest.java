package com.atguigu.gmall.cart;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Connor
 * @date 2022/9/9
 */
public class StreamTest {
    @Test
    public void testStreamForeach() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        list.stream()
//                .parallel()
                .forEach(i -> {
                    System.out.println("i = " + i + ", currentThread: " + Thread.currentThread().getName());
                });
    }

    public static void main1(String[] args) {

        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            String str = sc.nextLine();
            if(str.contains(".")){
                String[] part = str.split("\\.");
                Long result = 0L;
                for(int i = 0; i < part.length; i++){
                    result = result * 256 + Integer.parseInt(part[i]);
                }
                System.out.println(result);
            } else {
                Long ip = Long.parseLong(str);
                String result = "";
                for(int i = 0; i < 4; i++){
                    result = ip % 256 + result;
                    ip /= 256;
                }
                System.out.println(result.substring(0, result.length() - 1));
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        int num = Integer.parseInt(br.readLine());
//        List<String> list = new ArrayList<>();
//        for(int i = 0; i < num; i++){
//            list.add(br.readLine());
//        }
//        Collections.sort(list, (w1, w2) -> {
//            int index = 0;
//            while(index < w1.length() && index < w2.length()){
//                if(w1.charAt(index) < w2.charAt(index)){
//                    return 1;
//                } else if(w1.charAt(index) > w2.charAt(index)) {
//                    return -1;
//                } else {
//                    index++;
//                }
//            }
//        });
//    }
}
