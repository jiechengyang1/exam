package com.jcy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jcy.entity.UserRole;

import java.util.List;

/**
 * @author JCY
 * @implNote 2022/02/10 9:05
 */
public interface UserRoleService extends IService<UserRole> {

    String getMenuInfo(Integer roleId);

    List<UserRole> getUserRole();
}
