package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.UserLoginRequest;
import club._8b1t.model.dto.UserRegisterRequest;
import club._8b1t.model.entity.User;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody @Valid UserLoginRequest loginRequest, @RequestParam(defaultValue = "false") Boolean remember) {
        // 根据用户名查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginRequest.getUsername()));

        // 用户不存在
        if (user == null) {
            return ResultUtil.error("用户名不存在");
        }

        // 密码错误
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResultUtil.error("密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        userService.updateById(user);

        // 登录成功，使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);

        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    public Result register(@RequestBody @Valid UserRegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerRequest.getUsername())) > 0) {
            return ResultUtil.error("用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());

        // 保存用户
        userService.save(user);

        return ResultUtil.success("注册成功", user.getId());
    }

    @GetMapping("/current")
    public Result current() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return ResultUtil.error("用户不存在");
        }

        // 出于安全考虑，不返回密码
        user.setPassword(null);

        return ResultUtil.success(user);
    }

    @GetMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }
}
