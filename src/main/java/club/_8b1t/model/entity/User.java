package club._8b1t.model.entity;

import club._8b1t.model.enums.UserRole;
import club._8b1t.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 用户实体类
 *
 * @author 8bit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID（使用雪花算法生成）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色（ADMIN, STORE_MANAGER, FRANCHISEE）
     */
    private UserRole role;

    /**
     * 状态（ACTIVE, INACTIVE, SUSPENDED, PENDING）
     */
    private UserStatus status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 删除时间（用于逻辑删除）
     */
    private Date deletedAt;

    /**
     * 逻辑删除标识（0为未删除，1为已删除）
     */
    private Boolean isDeleted;

}


