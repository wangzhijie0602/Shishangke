package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.model.dto.UserLoginRequest;
import club._8b1t.model.dto.UserRegisterRequest;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.linpeilie.Converter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static club._8b1t.exception.ErrorCode.FORBIDDEN_ERROR;
import static club._8b1t.exception.ErrorCode.PARAMS_ERROR;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Converter converter;

    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody @Valid UserLoginRequest request,
                                     @RequestParam(defaultValue = "false") Boolean remember) {
        // 根据用户名查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        // 用户不存在
        // 密码错误
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }

        if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException(FORBIDDEN_ERROR, "用户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(new Date());
        userService.updateById(user);

        // 登录成功，使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);

        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> register(@RequestBody @Valid UserRegisterRequest request) {
        // 检查用户名是否已存在
        if (userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())) > 0) {
            return ResultUtil.error("用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setNickname("用户" + RandomUtil.randomString(8));

        // 保存用户
        userService.save(user);

        return ResultUtil.success("注册成功", user.getId());
    }

    @GetMapping("/current")
    public Result<UserVO> current() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 查询用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR, "用户不存在");
        }

        // 创建UserVO对象
        UserVO userVO = converter.convert(user, UserVO.class);

        return ResultUtil.success(userVO);
    }

    @GetMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }
}
