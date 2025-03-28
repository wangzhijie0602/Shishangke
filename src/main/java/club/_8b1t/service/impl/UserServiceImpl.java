package club._8b1t.service.impl;

import static club._8b1t.exception.ResultCode.*;

import club._8b1t.mapper.UserMapper;
import club._8b1t.model.entity.User;
import club._8b1t.model.enums.User.StatusEnum;
import club._8b1t.service.CosService;
import club._8b1t.service.UserService;
import club._8b1t.util.ExceptionUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务实现类
 * 实现UserService接口中定义的所有业务方法
 * 提供用户相关的具体业务逻辑实现
 * 
 * @author 8bit
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-03-06 19:59:26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    // 注入腾讯云对象存储服务，用于处理头像上传
    @Resource
    private CosService cosService;

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 上传头像到云存储
        String avatarUrl = cosService.uploadAvatar(userId, file);
        
        // 更新用户头像URL
        user.setAvatar(avatarUrl);
        boolean success = this.updateById(user);
        ExceptionUtil.throwIfNot(success, INTERNAL_SERVER_ERROR, "更新头像失败");
        return avatarUrl;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 验证旧密码是否正确
        ExceptionUtil.throwIfNot(BCrypt.checkpw(oldPassword, user.getPassword()), BAD_REQUEST, "旧密码错误");
        
        // 使用BCrypt加密新密码并更新
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return this.updateById(user);
    }

    @Override
    public boolean updatePassword(Long userId, String newPassword) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 直接更新密码，不验证旧密码（用于管理员重置密码）
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return this.updateById(user);
    }

    @Override
    public boolean updateNickname(Long userId, String nickname) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 更新昵称
        user.setNickname(nickname);
        return this.updateById(user);
    }

    @Override
    public boolean updateEmail(Long userId, String email) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 更新邮箱
        user.setEmail(email);
        return this.updateById(user);
    }

    @Override
    public boolean updatePhone(Long userId, String phone) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 更新手机号
        user.setPhone(phone);
        return this.updateById(user);
    }

    @Override
    public boolean updateStatus(Long userId, StatusEnum status) {
        // 获取用户信息
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        
        // 更新用户状态
        user.setStatus(status);
        return this.updateById(user);
    }

    @Override
    public Page<User> listUsers(User user, int pageNum, int pageSize) {
        // 创建查询条件包装器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (user != null) {
            // 根据传入的查询参数构建查询条件
            // 使用模糊查询匹配用户名、昵称、手机号和邮箱
            // 使用精确匹配角色和状态
            queryWrapper.like(StrUtil.isNotBlank(user.getUsername()), User::getUsername, user.getUsername())
                    .like(StrUtil.isNotBlank(user.getNickname()), User::getNickname, user.getNickname())
                    .like(StrUtil.isNotBlank(user.getPhone()), User::getPhone, user.getPhone())
                    .like(StrUtil.isNotBlank(user.getEmail()), User::getEmail, user.getEmail())
                    .eq(user.getRole() != null, User::getRole, user.getRole())
                    .eq(user.getStatus() != null, User::getStatus, user.getStatus());
        }

        // 创建分页对象并执行查询
        Page<User> page = new Page<>(pageNum, pageSize);
        return this.page(page, queryWrapper);
    }
}




