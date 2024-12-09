package club._8b1t.model.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 * 代表系统中用户的不同角色。
 *
 * @author 8bit
 */
@Getter
public enum UserRole {

    /**
     * 管理员角色，拥有系统的全部权限
     */
    ADMIN,

    /**
     * 店铺经理角色，负责管理店铺的运营
     */
    STORE_MANAGER,

    /**
     * 加盟商角色，拥有参与系统的基本权限
     */
    FRANCHISEE;

}
