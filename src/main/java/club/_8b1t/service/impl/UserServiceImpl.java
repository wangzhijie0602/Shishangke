package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.User;
import club._8b1t.service.UserService;
import club._8b1t.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2025-03-06 19:59:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




