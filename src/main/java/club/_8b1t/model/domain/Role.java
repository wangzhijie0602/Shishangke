package club._8b1t.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 角色表
 * @TableName role
 */
@TableName(value ="role")
@Data
public class Role {
    /**
     * 自增角色ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 角色唯一标识（如ADMIN）
     */
    private String roleCode;

    /**
     * 角色显示名称
     */
    private String roleName;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 是否系统内置角色
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