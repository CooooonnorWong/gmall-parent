package com.atguigu.cache.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public static String toStr(Object object) {
        //jackson
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static <T> T toObj(String jsonStr, Class<? extends T> clazz) {
        try {
            return MAPPER.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T toObj(String jsonStr, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(jsonStr, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
