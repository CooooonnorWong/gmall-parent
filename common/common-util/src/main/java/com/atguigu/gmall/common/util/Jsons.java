package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang.StringUtils;

/**
 * @author Connor
 * @date 2022/8/28
 */
public class Jsons {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 把对象转为json字符串
     *
     * @param object
     * @return
     */
    public static String toStr(@NotNull Object object) {
        //jackson
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }

    public static <T> T toObj(String jsonStr, Class<? extends T> clazz) {
        try {
            return MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
