package club._8b1t.service.impl;

import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ErrorCode;
import club._8b1t.mapper.UserMapper;
import club._8b1t.model.entity.User;
import club._8b1t.service.CosService;
import club._8b1t.service.UserService;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

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

    @Resource
    private Converter converter;

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));

        // 用户不存在或密码错误
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }

        // 检查用户状态
        if ("DISABLED".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
        }

        return user;
    }

    @Override
    public Long register(String username, String password) {
        // 检查用户名是否已存在
        if (this.count(new LambdaQueryWrapper<User>()
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
        this.save(user);

        return user.getId();
    }

    @Override
    public User getUser(Long userId) {
        // 查询用户信息
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        return user;
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        // 验证用户是否存在
        if (!this.existsById(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 上传头像
        String avatarUrl = cosService.uploadAvatar(userId, file);

        // 更新用户头像
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getAvatar, avatarUrl);

        boolean success = this.update(updateWrapper);

        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新头像失败");
        }

        return avatarUrl;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 验证原密码
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }

        // 更新新密码
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, BCrypt.hashpw(newPassword, BCrypt.gensalt()));

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateNickname(Long userId, String nickname) {
        if (!this.existsById(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getNickname, nickname);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateEmail(Long userId, String email) {

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getEmail, email);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updatePhone(Long userId, String phone) {
        if (!this.existsById(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPhone, phone);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateStatus(Long userId, String status) {
        if (!this.existsById(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStatus, status);

        return this.update(updateWrapper);
    }

    @Override
    public boolean updateLastLoginTime(Long userId, Date lastLoginTime) {
        this.existsById();

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getLastLoginTime, lastLoginTime);

        return this.update(updateWrapper);
    }

    @Override
    public Page<User> listUsers(int pageNum, int pageSize, String username, String nickname, String phone, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        queryWrapper.like(StrUtil.isNotBlank(username), User::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), User::getNickname, nickname)
                .like(StrUtil.isNotBlank(phone), User::getPhone, phone)
                .eq(StrUtil.isNotBlank(status), User::getStatus, status);

        // 执行分页查询
        Page<User> page = new Page<>(pageNum, pageSize);

        return this.page(page, queryWrapper);
    }

    private void existsById(Long id) {
        boolean b = this.count(new LambdaQueryWrapper<User>().eq(User::getId, id)) > 0;
        if (!b) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
    }
}




