package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.dto.AddUserDto;
import com.jcy.dto.LoginDto;
import com.jcy.dto.RegisterDto;
import com.jcy.dto.UpdateUserInfoDto;
import com.jcy.entity.User;
import com.jcy.vo.PageResponse;
import com.jcy.vo.UserInfoVo;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface UserService extends IService<User> {

    String register(RegisterDto registerDto);

    Boolean checkUsername(String username);

    String login(LoginDto loginDto);

    User getUserByUsername(String username);

    // 这里要reset cache 所以必须要有更新后的数据返回
    User updateUserInfo(UpdateUserInfoDto updateUserInfoDto);

    PageResponse<UserInfoVo> getUser(String loginName, String trueName, Integer pageNo, Integer pageSize);

    void handlerUser(Integer type, String userIds);

    void addUser(AddUserDto addUserDto);

    UserInfoVo getUserInfoById(Integer userId);
}
