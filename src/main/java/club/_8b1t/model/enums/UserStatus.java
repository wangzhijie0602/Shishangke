package club._8b1t.model.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 * 代表系统中用户的不同状态。
 *
 * @author 8bit
 */
@Getter
public enum UserStatus {

    /**
     * 活跃状态，表示用户可以正常使用系统的功能
     */
    ACTIVE,

    /**
     * 非活跃状态，表示用户暂时停用
     */
    INACTIVE,

    /**
     * 被暂停状态，表示用户因违规等原因被暂时禁止使用
     */
    SUSPENDED,

    /**
     * 待处理状态，表示用户的注册或请求正在审批中
     */
    PENDING;

}
