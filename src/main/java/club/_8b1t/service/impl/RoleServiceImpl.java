package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.domain.Role;
import club._8b1t.service.RoleService;
import club._8b1t.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【role(角色表)】的数据库操作Service实现
* @createDate 2025-01-25 18:00:35
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




