package club._8b1t.controller;

import club._8b1t.common.Result;
import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.model.dto.user.UserChangePassword;
import club._8b1t.model.dto.user.UserInfoUpdateRequest;
import club._8b1t.model.dto.user.UserLoginRequest;
import club._8b1t.model.dto.user.UserRegisterRequest;
import club._8b1t.model.entity.User;
import club._8b1t.model.vo.UserVO;
import club._8b1t.service.UserService;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    private UserService userService;
    
    @Resource
    private Converter converter;

    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody @Valid UserLoginRequest request,
                                     @RequestParam(defaultValue = "false") Boolean remember) {
        // 根据用户名查询用户
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));

        // 用户不存在或密码错误
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 检查用户状态
        if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
        }
        
        // 使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);
        
        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid UserRegisterRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        
        // 检查用户名是否已存在
        if (userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名已存在");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname("用户" + RandomUtil.randomString(8));
        user.setStatus("ENABLED"); // 默认启用状态

        // 保存用户
        userService.save(user);

        return ResultUtil.success("注册成功", user.getId());
    }

    @GetMapping("/current")
    public Result<UserVO> current() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 获取当前用户信息
        User user = userService.getUser(userId);
        
        // 转换为VO
        UserVO userVO = converter.convert(user, UserVO.class);
        
        return ResultUtil.success(userVO);
    }

    @GetMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return ResultUtil.success();
    }

    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户头像
        String avatarUrl = userService.updateAvatar(userId, file);
        
        return ResultUtil.success("头像上传成功", avatarUrl);
    }

    @PostMapping("/updatePassword")
    public Result<String> updatePassword(@RequestBody @Valid UserChangePassword request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户密码
        boolean success = userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码更新失败");
        }
        
        return ResultUtil.success("密码更新成功");
    }

    @PostMapping("/updateNickname")
    public Result<String> updateNickname(@RequestParam String nickname) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户昵称
        boolean success = userService.updateNickname(userId, nickname);
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "昵称更新失败");
        }
        
        return ResultUtil.success("昵称更新成功");
    }
    
    @PostMapping("/updateEmail")
    public Result<String> updateEmail(@RequestParam String email) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户邮箱
        boolean success = userService.updateEmail(userId, email);
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "邮箱更新失败");
        }
        
        return ResultUtil.success("邮箱更新成功");
    }
    
    @PostMapping("/updatePhone")
    public Result<String> updatePhone(@RequestParam String phone) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户电话
        boolean success = userService.updatePhone(userId, phone);
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "电话更新失败");
        }
        
        return ResultUtil.success("电话更新成功");
    }

    @PostMapping("/updateInfo")
    public Result<Void> updateInfo(@RequestBody @Valid UserInfoUpdateRequest request) {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 更新用户信息（按照单个字段更新）
        boolean success = true;
        
        if (request.getNickname() != null) {
            success = success && userService.updateNickname(userId, request.getNickname());
        }
        
        if (request.getEmail() != null) {
            success = success && userService.updateEmail(userId, request.getEmail());
        }
        
        if (request.getPhone() != null) {
            success = success && userService.updatePhone(userId, request.getPhone());
        }
        
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }
        
        return ResultUtil.success("更新成功");
    }
} 