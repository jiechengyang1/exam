package com.jcy.utils;

import com.jcy.entity.Answer;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Objects;

public class SaltEncryption {

    //盐值加密,将密码加密，防止数据库泄露
    public static String saltEncryption(String password, String salt) {
        String current = password + salt;
        return DigestUtils.md5DigestAsHex(current.getBytes());
    }

    //根据题目id获取答案列表中的答案索引
    public static int getIndex(List<Answer> list, Integer questionId) {
        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(list.get(i).getQuestionId(), questionId)) {
                return i;
            }
        }
        return -1;
    }
}
