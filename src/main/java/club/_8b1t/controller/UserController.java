package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.user.*;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static club._8b1t.exception.ResultCode.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private Converter converter;

    // 定义常用错误消息常量
    private static final String NO_ADMIN_RIGHTS = "无管理员权限";

    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody @Valid LoginRequest request,
                                     @RequestParam(defaultValue = "false") Boolean remember) {
        // 根据用户名查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        // 用户不存在或密码错误
        ExceptionUtil.throwIf(
                user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword()),
                BAD_REQUEST,
                "用户不存在或密码错误"
        );

        // 检查用户状态
        ExceptionUtil.throwIf(
                "DISABLED".equals(user.getStatus()),
                FORBIDDEN,
                "用户已被禁用"
        );

        // 使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);

        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid RegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        // 检查用户名是否已存在
        ExceptionUtil.throwIf(
                userService.count(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0,
                BAD_REQUEST,
                "用户名已存在"
        );

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname("用户" + RandomUtil.randomString(8));
        user.setStatus("ENABLED");

        // 保存用户
        userService.save(user);

        return ResultUtil.success("注册成功", user.getId());
    }

    @GetMapping("/current")
    public Result<UserVO> current() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        // 获取当前用户信息
        User user = userService.getById(userId);
        ExceptionUtil.throwIfNull(user, NOT_FOUND, "用户不存在");
        // 转换为VO
        UserVO userVO = converter.convert(user, UserVO.class);
        return ResultUtil.success(userVO);
    }

    @GetMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }

    @PostMapping({"/update/avatar", "/{id}/update/avatar"})
    public Result<String> updateAvatar(@PathVariable(required = false) String id,
                                       @RequestParam("file") MultipartFile file) {
        long userId;

        if (id == null) {
            userId = StpUtil.getLoginIdAsLong();
        } else {
            ExceptionUtil.throwIfNot(
                    StpUtil.hasRole("ADMIN"),
                    UNAUTHORIZED,
                    NO_ADMIN_RIGHTS
            );
            userId = Long.parseLong(id);
        }

        // 更新用户头像
        String avatarUrl = userService.updateAvatar(userId, file);

        return ResultUtil.success("头像上传成功", avatarUrl);
    }

    @PostMapping({"/update/password", "/{id}/update/password"})
    public Result<Void> updatePassword(@PathVariable(required = false) String id,
                                         @RequestBody @Valid PasswordRequest request) {
        long userId;
        boolean success;

        if (id == null) {
            userId = StpUtil.getLoginIdAsLong();
            success = userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        } else {
            ExceptionUtil.throwIfNot(
                    StpUtil.hasRole("ADMIN"),
                    UNAUTHORIZED,
                    NO_ADMIN_RIGHTS
            );
            userId = Long.parseLong(id);
            success = userService.updatePassword(userId, request.getNewPassword());
        }

        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "密码更新失败");

        return ResultUtil.success("密码更新成功");
    }

    @PostMapping({"/update/nickname", "/{id}/update/nickname"})
    public Result<Void> updateNickname(@PathVariable(required = false) String id,
                                         @RequestParam String nickname) {
        long userId;
        if (id == null) {
            userId = StpUtil.getLoginIdAsLong();
        } else {
            ExceptionUtil.throwIfNot(
                    StpUtil.hasRole("ADMIN"),
                    UNAUTHORIZED,
                    NO_ADMIN_RIGHTS
            );
            userId = Long.parseLong(id);
        }

        // 更新用户昵称
        boolean success = userService.updateNickname(userId, nickname);

        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "昵称更新失败");

        return ResultUtil.success("昵称更新成功");
    }

    @PostMapping({"/update/email", "/{id}/update/email"})
    public Result<Void> updateEmail(@PathVariable(required = false) String id,
                                      @RequestParam String email) {
        long userId;
        if (id == null) {
            userId = StpUtil.getLoginIdAsLong();
        } else {
            ExceptionUtil.throwIfNot(
                    StpUtil.hasRole("ADMIN"),
                    UNAUTHORIZED,
                    NO_ADMIN_RIGHTS
            );
            userId = Long.parseLong(id);
        }

        // 更新用户邮箱
        boolean success = userService.updateEmail(userId, email);

        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "邮箱更新失败");

        return ResultUtil.success("邮箱更新成功");
    }

    @PostMapping("/update/phone")
    public Result<Void> updatePhone(@PathVariable(required = false) String id,
                                      @RequestParam String phone) {
        long userId;
        if (id == null) {
            userId = StpUtil.getLoginIdAsLong();
        } else {
            ExceptionUtil.throwIfNot(
                    StpUtil.hasRole("ADMIN"),
                    UNAUTHORIZED,
                    NO_ADMIN_RIGHTS
            );
            userId = Long.parseLong(id);
        }

        // 更新用户电话
        boolean success = userService.updatePhone(userId, phone);

        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "电话更新失败");

        return ResultUtil.success("电话更新成功");
    }

    @GetMapping("/{id}")
    @SaCheckRole("ADMIN")
    public Result<UserVO> get(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 获取用户信息
        User user = userService.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        UserVO userVO = converter.convert(user, UserVO.class);
        return ResultUtil.success(userVO);
    }

    @GetMapping("/{id}/delete")
    @SaCheckRole("ADMIN")
    public Result<Void> delete(@PathVariable String id) {
        boolean success = userService.removeById(id);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "删除失败");
        return ResultUtil.success("删除成功");
    }

    @GetMapping("/{id}/ban")
    @SaCheckRole("ADMIN")
    public Result<Void> ban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 禁用用户
        boolean success = userService.updateStatus(userId, "DISABLED");
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "禁用失败");
        return ResultUtil.success("禁用成功");
    }

    @GetMapping("/{id}/unban")
    @SaCheckRole("ADMIN")
    public Result<Void> unban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 解封用户
        boolean success = userService.updateStatus(userId, "ENABLED");
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "解封失败");
        return ResultUtil.success("解封成功");
    }

    @PostMapping("/list")
    @SaCheckRole("ADMIN")
    public Result<Page<UserVO>> getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestBody(required = false) QueryRequest request) {
        User user = converter.convert(request, User.class);

        // 查询用户列表
        Page<User> userPage = userService.listUsers(user, pageNum, pageSize);

        // 将Page<User>转换为Page<UserVO>
        Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        userVOPage.setRecords(converter.convert(userPage.getRecords(), UserVO.class));

        return ResultUtil.success(userVOPage);
    }
} 