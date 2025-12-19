package com.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.server.comon.Result;
import com.server.entity.SysUser;
import com.server.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    // 登录接口
    @PostMapping("/login")
    public Result<SysUser> login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        // 1. 去数据库查这个账号
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));

        // 2. 账号不存在
        if (user == null) {
            return Result.error("账号不存在");
        }

        // 3. 密码错误 (简单判断，实际建议加密)
        if (!user.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        // 4. 登录成功，返回完整的用户信息（包含 role）
        return Result.success(user);
    }

    // 获取用户信息
    @GetMapping("/info/{id}")
    public Result<SysUser> getUserInfo(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    // 注册/新增员工
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody SysUser user) {
        // 默认角色是 USER，除非传了别的
        if(user.getRole() == null) user.setRole("USER");
        if(user.getBalance() == null) user.setBalance(new java.math.BigDecimal(0));
        sysUserService.save(user);
        return Result.success(true);
    }
    // 补充：获取所有用户列表 (用于员工管理页面)
    @GetMapping("/list")
    public Result<java.util.List<SysUser>> list() {
        // 使用 LambdaQueryWrapper 查出所有用户
        return Result.success(sysUserService.list());
    }
}