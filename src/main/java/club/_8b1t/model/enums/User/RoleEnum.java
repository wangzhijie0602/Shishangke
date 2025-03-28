package club._8b1t.model.enums.User;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.Getter;

/**
 * 用户角色枚举
 * 餐饮管理系统角色定义
 */
@Getter
public enum RoleEnum {
    ADMIN("ADMIN", "系统管理员"),
    BOSS("BOSS", "老板"),
    MANAGER("MANAGER", "店长"),
    CHEF("CHEF", "厨师"),
    CUSTOMER("CUSTOMER", "顾客"),
    DELIVERY("DELIVERY", "配送员");

    @EnumValue
    private final String code;
    private final String desc;

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 角色编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static RoleEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        
        return null;
    }

    /**
     * 检查角色编码是否有效
     * 
     * @param code 角色编码
     * @return 是否是有效的角色编码
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
} 