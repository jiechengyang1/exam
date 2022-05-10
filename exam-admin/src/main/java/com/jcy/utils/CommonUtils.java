package com.jcy.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;

import java.util.Map;

public class CommonUtils {

    public static <T> void setEqualsQueryWrapper(QueryWrapper<T> queryWrapper, Map<String, Object> fieldsMap) {
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                if (StringUtils.hasLength((String) value)) {
                    queryWrapper.eq(entry.getKey(), value);
                }
            } else if (value instanceof Integer) {
                queryWrapper.eq(entry.getKey(), value);
            }
        }
    }

    public static <T> void setLikeWrapper(QueryWrapper<T> queryWrapper, Map<String, Object> fieldsMap) {
        for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                if (StringUtils.hasLength((String) value)) {
                    queryWrapper.like(entry.getKey(), value);
                }
            } else if (value instanceof Integer) {
                queryWrapper.like(entry.getKey(), value);
            }
        }
    }

}
