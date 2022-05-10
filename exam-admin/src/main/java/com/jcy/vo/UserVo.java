package com.jcy.vo;

import com.jcy.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserVo {

    private String username;

    private String password;

    private String trueName;

    public static UserVo fromUser(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

}
