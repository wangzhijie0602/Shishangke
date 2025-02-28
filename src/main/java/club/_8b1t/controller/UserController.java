package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.UserCreateRequest;
import club._8b1t.model.dto.UserLoginRequest;
import club._8b1t.model.dto.UserRegisterRequest;
import club._8b1t.model.dto.UserUpdateRequest;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.linpeilie.Converter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Converter converter;

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
        user.setPassword(BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt()));
        user.setNickname("用户" + RandomUtil.randomString(8));

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

        // 创建UserVO对象
        UserVO userVO = converter.convert(user, UserVO.class);

        return ResultUtil.success(userVO);
    }

    @GetMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }

    @PostMapping
    public Result create(@RequestBody @Valid UserCreateRequest createRequest) {
        User user = new User();
        user.setUsername(createRequest.getUsername());
        user.setPassword(createRequest.getPassword());
        user.setNickname(createRequest.getNickname());
        user.setEmail(createRequest.getEmail());
        user.setPhone(createRequest.getPhone());
        userService.save(user);
        return ResultUtil.success("创建成功", user.getId());
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResultUtil.error("用户不存在");
        }

        // 创建UserVO对象
        UserVO userVO = converter.convert(user, UserVO.class);

        return ResultUtil.success(userVO);
    }

    @PostMapping("/update")
    public Result update(@RequestBody @Valid UserUpdateRequest updateRequest) {
        User user = userService.getById(updateRequest.getId());
        if (user == null) {
            return ResultUtil.error("用户不存在");
        }
        user.setUsername(updateRequest.getUsername());
        user.setPassword(updateRequest.getPassword());
        user.setNickname(updateRequest.getNickname());
        user.setEmail(updateRequest.getEmail());
        user.setPhone(updateRequest.getPhone());
        userService.updateById(user);
        return ResultUtil.success("更新成功");
    }

    @PostMapping("/{id}/delete")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return ResultUtil.success("删除成功");
    }
}
