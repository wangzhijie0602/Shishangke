package club._8b1t.service.impl;

import static club._8b1t.exception.ResultCode.*;

import club._8b1t.mapper.UserMapper;
import club._8b1t.model.entity.User;
import club._8b1t.service.CosService;
import club._8b1t.service.UserService;
import club._8b1t.util.ExceptionUtil;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 8bit
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2025-03-06 19:59:26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private CosService cosService;

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        // 上传头像
        String avatarUrl = cosService.uploadAvatar(userId, file);

        // 更新用户头像
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getAvatar, avatarUrl);

        boolean success = this.update(updateWrapper);

        ExceptionUtil.throwIfNot(success, INTERNAL_SERVER_ERROR, "更新头像失败");

        return avatarUrl;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");
        // 校验旧密码
        ExceptionUtil.throwIfNot(BCrypt.checkpw(oldPassword, user.getPassword()), BAD_REQUEST, "旧密码错误");

        // 更新新密码
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, BCrypt.hashpw(newPassword, BCrypt.gensalt()));

        return this.update(updateWrapper);
    }

    @Override
    public boolean updatePassword(Long userId, String newPassword) {

        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        // 更新新密码
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, BCrypt.hashpw(newPassword, BCrypt.gensalt()));

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateNickname(Long userId, String nickname) {

        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getNickname, nickname);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateEmail(Long userId, String email) {
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getEmail, email);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updatePhone(Long userId, String phone) {
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPhone, phone);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateStatus(Long userId, String status) {
        User user = this.getById(userId);
        ExceptionUtil.throwIfNull(user, BAD_REQUEST, "用户不存在");

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStatus, status);

        return this.update(updateWrapper);
    }

    @Override
    public Page<User> listUsers(User user, int pageNum, int pageSize) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (user != null) {
            // 添加查询条件
            queryWrapper.like(StrUtil.isNotBlank(user.getUsername()), User::getUsername, user.getUsername())
                    .like(StrUtil.isNotBlank(user.getNickname()), User::getNickname, user.getNickname())
                    .like(StrUtil.isNotBlank(user.getPhone()), User::getPhone, user.getPhone())
                    .like(StrUtil.isNotBlank(user.getEmail()), User::getEmail, user.getEmail())
                    .eq(StrUtil.isNotBlank(user.getRole()), User::getRole, user.getRole())
                    .eq(StrUtil.isNotBlank(user.getStatus()), User::getStatus, user.getStatus());
        }

        // 执行分页查询
        Page<User> page = new Page<>(pageNum, pageSize);

        return this.page(page, queryWrapper);
    }
}




