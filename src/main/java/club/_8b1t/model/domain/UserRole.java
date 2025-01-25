package club._8b1t.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户-角色关系表（业务层维护关联）
 * @TableName user_role
 */
@TableName(value ="user_role")
@Data
public class UserRole {
    /**
     * 自增主键ID
     */
    @TableId(type = IdType.AUTO)
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