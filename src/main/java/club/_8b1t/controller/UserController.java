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
        // 使用Service层登录
        User user = userService.login(request.getUsername(), request.getPassword());
        
        // 使用Sa-Token记录登录状态
        StpUtil.login(user.getId(), remember);
        
        return ResultUtil.success("登录成功", StpUtil.getTokenInfo());
    }

    @PostMapping("/register")
    public Result<Long> register(@RequestBody @Valid UserRegisterRequest request) {
        // 使用Service层注册
        Long userId = userService.register(request.getUsername(), request.getPassword());
        return ResultUtil.success("注册成功", userId);
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