package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色-权限关系表
 *
 * @TableName role_permission
 */
@TableName(value = "role_permission")
@Data
public class RolePermission {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 权限ID
     */
    private Integer permissionId;

    /**
     * 授权人ID
     */
    private Long grantedBy;

    /**
     * 授权时间
     */
    private Date grantedAt;
}