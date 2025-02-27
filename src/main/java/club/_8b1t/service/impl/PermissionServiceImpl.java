package club._8b1t.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import club._8b1t.model.entity.Permission;
import club._8b1t.service.PermissionService;
import club._8b1t.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author 8bit
* @description 针对表【permission(权限表)】的数据库操作Service实现
* @createDate 2025-02-27 16:59:22
*/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
    implements PermissionService{

}




