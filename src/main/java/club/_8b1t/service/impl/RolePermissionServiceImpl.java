package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.RolePermission;
import club._8b1t.service.RolePermissionService;
import club._8b1t.mapper.RolePermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【role_permission(角色-权限关系表)】的数据库操作Service实现
* @createDate 2025-02-27 16:59:22
*/
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission>
    implements RolePermissionService{

}




