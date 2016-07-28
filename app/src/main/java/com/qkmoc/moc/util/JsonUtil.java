package com.qkmoc.moc.util;


import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json工具类
 */
public class JsonUtil {

    /**
     * 把bean对象转换为json字符串
     *
     * @param object bean对象
     * @return string json字符串
     */
    public static String beanToJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = null;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return json;
    }

    /**
     * json转 bean
     *
     * @param jsonStr
     * @param valueType
     * @return
     */
    public static <T> T jsonTobean(String jsonStr, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把string转成list
     *
     * @param string
     * @param valueTypeRef
     * @return
     */
    public static <T> T jsonToList(String jsonStr, TypeReference<T> valueTypeRef) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
