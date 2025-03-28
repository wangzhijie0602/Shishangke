package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.model.dto.user.*;
import club._8b1t.model.enums.User.RoleEnum;
import club._8b1t.util.ExceptionUtil;
import club._8b1t.model.entity.User;
import club._8b1t.model.enums.User.StatusEnum;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static club._8b1t.exception.ResultCode.*;

/**
 * 用户控制器
 * 提供用户相关的接口，包括用户认证、信息管理、状态管理等
 * 
 * @author 8bit
 * @description 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private Converter converter;

    // 定义常用错误消息常量
    private static final String NO_ADMIN_RIGHTS = "无管理员权限";

    /**
     * 用户登录
     * 验证用户身份并生成登录令牌
     *
     * @param request 登录请求，包含用户名和密码
     * @param remember 是否记住登录状态
     * @return 登录成功返回令牌信息
     */
    @PostMapping("/login")
    @Operation(operationId = "user_login")
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
                StatusEnum.DISABLED.equals(user.getStatus()),
                FORBIDDEN,
                "用户已被禁用"
        );

        user.setLastLoginTime(new Date());
        userService.updateById(user);

        // 使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);

        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    /**
     * 用户注册
     * 创建新用户账号，设置初始信息
     *
     * @param request 注册请求，包含用户名和密码
     * @return 注册成功返回用户ID
     */
    @PostMapping("/register")
    @Operation(operationId = "user_register")
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
        user.setRole(RoleEnum.BOSS);
        user.setStatus(StatusEnum.ACTIVE);

        // 保存用户
        userService.save(user);

        return ResultUtil.success("注册成功", user.getId());
    }

    /**
     * 获取当前登录用户信息
     * 返回当前已登录用户的详细信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current")
    @Operation(operationId = "user_get_current")
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

    /**
     * 用户登出
     * 清除当前用户的登录状态
     *
     * @return 登出结果
     */
    @GetMapping("/logout")
    @Operation(operationId = "user_logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }

    /**
     * 更新用户头像
     * 上传并更新用户的头像图片
     *
     * @param id 用户ID（可选，管理员可指定）
     * @param file 头像文件
     * @return 更新后的头像URL
     */
    @PostMapping({"/update/avatar", "/{id}/update/avatar"})
    @Operation(operationId = "user_update_avatar")
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

    /**
     * 更新用户密码
     * 支持用户修改自己的密码（需要验证旧密码）和管理员重置密码
     *
     * @param id 用户ID（可选，管理员可指定）
     * @param request 密码更新请求
     * @return 更新结果
     */
    @PostMapping({"/update/password", "/{id}/update/password"})
    @Operation(operationId = "user_update_password")
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

    /**
     * 更新用户昵称
     * 修改用户的显示名称
     *
     * @param id 用户ID（可选，管理员可指定）
     * @param nickname 新的昵称
     * @return 更新结果
     */
    @PostMapping({"/update/nickname", "/{id}/update/nickname"})
    @Operation(operationId = "user_update_nickname")
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

    /**
     * 更新用户邮箱
     * 修改用户的联系邮箱
     *
     * @param id 用户ID（可选，管理员可指定）
     * @param email 新的邮箱地址
     * @return 更新结果
     */
    @PostMapping({"/update/email", "/{id}/update/email"})
    @Operation(operationId = "user_update_email")
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

    /**
     * 更新用户电话
     * 修改用户的联系电话
     *
     * @param id 用户ID（可选，管理员可指定）
     * @param phone 新的电话号码
     * @return 更新结果
     */
    @PostMapping("/update/phone")
    @Operation(operationId = "user_update_phone")
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

    /**
     * 获取指定用户信息
     * 管理员接口，获取指定用户的详细信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_get_by_id")
    public Result<UserVO> get(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 获取用户信息
        User user = userService.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        UserVO userVO = converter.convert(user, UserVO.class);
        return ResultUtil.success(userVO);
    }

    /**
     * 删除用户
     * 管理员接口，删除指定用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @GetMapping("/{id}/delete")
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_delete")
    public Result<Void> delete(@PathVariable String id) {
        boolean success = userService.removeById(id);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "删除失败");
        return ResultUtil.success("删除成功");
    }

    /**
     * 禁用用户
     * 管理员接口，禁用指定用户的账号
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @GetMapping("/{id}/ban")
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_ban")
    public Result<Void> ban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 禁用用户
        boolean success = userService.updateStatus(userId, StatusEnum.DISABLED);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "禁用失败");
        return ResultUtil.success("禁用成功");
    }

    /**
     * 解封用户
     * 管理员接口，解除指定用户的禁用状态
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @GetMapping("/{id}/unban")
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_unban")
    public Result<Void> unban(@PathVariable String id) {
        // 将字符串ID转换为Long
        Long userId = Long.valueOf(id);
        // 解封用户
        boolean success = userService.updateStatus(userId, StatusEnum.ACTIVE);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED, "解封失败");
        return ResultUtil.success("解封成功");
    }

    /**
     * 获取用户列表
     * 管理员接口，分页获取用户列表，支持条件查询
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param request 查询条件
     * @return 用户列表分页数据
     */
    @PostMapping("/list")
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_list")
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

    /**
     * 创建用户
     *
     * @param request 创建用户请求
     * @return 创建的用户信息
     */
    @PostMapping
    @SaCheckRole("ADMIN")
    @Operation(operationId = "user_create")
    public UserVO createUser(@Valid @RequestBody CreateRequest request) {
        User user = converter.convert(request, User.class);
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname("用户" + RandomUtil.randomString(8));
        }
        boolean success = userService.save(user);
        ExceptionUtil.throwIfNot(success, OPERATION_FAILED);
        return converter.convert(user, UserVO.class);
    }
} 