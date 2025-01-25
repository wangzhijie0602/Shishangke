package club._8b1t.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.domain.User;
import club._8b1t.service.UserService;
import club._8b1t.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【user(用户主表（业务层维护外键约束）)】的数据库操作Service实现
* @createDate 2025-01-25 17:56:09
*/
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
}




