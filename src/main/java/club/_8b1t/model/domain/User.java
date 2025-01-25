package club._8b1t.model.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户主表（业务层维护外键约束）
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * 雪花算法生成的64位唯一ID
     */
    @TableId
    private Long id;

    /**
     * 唯一用户名（应用层处理大小写）
     */
    private String username;

    /**
     * 已验证邮箱（NULL表示未验证）
     */
    private String email;

    /**
     * 国际格式号码（如：+86-13812345678）
     */
    private String phone;

    /**
     * Argon2id加密的密码哈希值
     */
    private String passwordHash;

    /**
     * 认证提供商（如wechat/github）
     */
    private String provider;

    /**
     * 第三方用户ID
     */
    private String providerId;

    /**
     * Base32编码的MFA密钥
     */
    private String mfaSecret;

    /**
     * 是否启用MFA
     */
    private Integer mfaEnabled;

    /**
     * AES加密的恢复代码（JSON数组）
     */
    private String recoveryCodes;

    /**
     * JWT格式重置令牌
     */
    private String passwordResetToken;

    /**
     * 令牌过期时间
     */
    private Date resetTokenExpiry;

    /**
     * 验证状态
     */
    private Integer isVerified;

    /**
     * 账户激活状态
     */
    private Integer isActive;

    /**
     * 锁定状态
     */
    private Integer isLocked;

    /**
     * 连续登录失败次数
     */
    private Integer failedLoginAttempts;

    /**
     * 软删除时间戳
     */
    @TableLogic
    private Date deletedAt;

    /**
     * 最后登录时间
     */
    private Date lastLogin;

    /**
     * 最后登录IP（支持IPv6）
     */
    private String lastLoginIp;

    /**
     * 最后登录设备信息
     */
    private String lastLoginUserAgent;

    /**
     * 最后密码修改时间
     */
    private Date passwordChangedAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}