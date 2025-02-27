package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户-角色关系表
 *
 * @TableName user_role
 */
@TableName(value = "user_role")
@Data
public class UserRole {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 分配人ID
     */
    private Long assignedBy;

    /**
     * 分配时间
     */
    private Date assignedAt;
}