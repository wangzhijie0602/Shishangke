package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 权限表
 *
 * @TableName permission
 */
@TableName(value = "permission")
@Data
public class Permission {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 权限唯一标识（如USER:CREATE）
     */
    private String permissionCode;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 父权限ID，用于构建权限树
     */
    private Integer parentId;

    /**
     * 是否系统内置权限
     */
    private Integer isSystem;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}