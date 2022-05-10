package com.jcy.vo;

import com.jcy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {

    private Integer id;

    private String username;

    private String trueName;

    private Integer roleId;

    private Integer status;

    private Date createDate;

    public static UserInfoVo fromUser(User user) {
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(user, userInfoVo);
        return userInfoVo;
    }
}
