package club._8b1t.model.enums.User;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum StatusEnum {

    ACTIVE("ACTIVE", "正常"),
    /**
     * 被管理员封禁
     */
    DISABLED("DISABLED", "已禁用"),

    /**
     * 已锁定（多次登录失败）
     */
    LOCKED("LOCKED", "已锁定");

    @EnumValue
    private final String code;
    private final String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 状态编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static StatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (StatusEnum status : StatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        
        return null;
    }
    
    /**
     * 检查状态编码是否有效
     *
     * @param code 状态编码
     * @return 是否是有效的状态编码
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
} 