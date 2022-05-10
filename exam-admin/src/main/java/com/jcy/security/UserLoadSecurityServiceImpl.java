package com.jcy.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcy.entity.User;
import com.jcy.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
@RequiredArgsConstructor
public class UserLoadSecurityServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapperByUsername = new QueryWrapper<User>().eq("username", username);
        User user = Optional.of(userMapper.selectOne(queryWrapperByUsername))
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(user.getRoleId() + ""));

        return withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(roles)
                .build();
    }
}
