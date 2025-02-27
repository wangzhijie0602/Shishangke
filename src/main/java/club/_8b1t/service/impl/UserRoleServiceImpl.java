package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.UserRole;
import club._8b1t.service.UserRoleService;
import club._8b1t.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【user_role(用户-角色关系表)】的数据库操作Service实现
* @createDate 2025-02-27 16:59:22
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




