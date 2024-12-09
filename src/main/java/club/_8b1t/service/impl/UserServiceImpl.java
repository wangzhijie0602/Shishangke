package club._8b1t.service.impl;

import club._8b1t.interceptor.JwtInterceptor;
import club._8b1t.mapper.UserMapper;
import club._8b1t.model.entity.User;
import club._8b1t.model.enums.Role;
import club._8b1t.service.UserService;
import club._8b1t.utils.JwtUtil;
import club._8b1t.utils.PasswordEncoderUtil;
import club._8b1t.utils.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户实体的操作类
 *
 * @author 8bit
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final JwtInterceptor jwtInterceptor;

    @Override
    public List<User> getAllUsers(long id) throws Exception {
        if (!isAdmin(id)) {
            throw new Exception("权限不足");
        }

        return userMapper.getAllUsers();
    }

    @Override
    public User getUserInfo(long id) throws Exception {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        return user;
    }

    @Override
    public User userLogin(User user) throws Exception {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("username", user.getUsername())
                .or()
                .eq("email", user.getEmail());
        User res = userMapper.selectOne(queryWrapper);

        if (res == null) {
            throw new Exception("用户名或邮箱不存在");
        }
        if (!PasswordEncoderUtil.matches(user.getPassword(), res.getPassword())) {
            throw new Exception("密码错误");
        }

        return res;
    }

    @Override
    public long userRegister(User user) throws Exception {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.or()
                .eq("username", user.getUsername())
                .eq("email", user.getEmail());

        if (userMapper.selectOne(queryWrapper) != null) {
            throw new Exception("用户名或邮箱已经存在");
        }
        user.setPassword(PasswordEncoderUtil.encodePassword(user.getPassword()));
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public boolean userUpdate(User user) throws Exception {
        String token = ThreadLocalUtil.getToken();
        long id = JwtUtil.extractUserId(token);
        if (!isAdmin(id)) {
            throw new Exception("权限不足,修改失败");
        }
        System.out.println(user.toString());
        if (user.getPassword() != null) {
            user.setPassword(PasswordEncoderUtil.encodePassword(user.getPassword()));
        }
        int i = userMapper.updateById(user);
        return i == 1;
    }

    public boolean isAdmin(long id) throws Exception {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new Exception("当前未登录或授权已过期");
        }
        Role role = user.getRole();
        return Role.ADMIN.equals(role);
    }

}
