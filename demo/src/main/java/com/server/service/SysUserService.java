package com.server.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.server.entity.SysUser;
import com.server.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    // 简单的登录逻辑
    public SysUser login(String username, String password) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", username).eq("password", password);
        return this.getOne(query);
    }
}