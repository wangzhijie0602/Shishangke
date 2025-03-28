package club._8b1t.model.enums.merchant;

import com.baomidou.mybatisplus.annotation.EnumValue;

import lombok.Getter;

/**
 * 商家状态枚举类
 */
@Getter
public enum StatusEnum {
    
    OPEN("OPEN", "营业中"),
    CLOSED("CLOSED", "休息中"),
    SUSPENDED("SUSPENDED", "暂停营业"),
    PENDING_REVIEW("PENDING_REVIEW", "待审核"),
    REJECTED("REJECTED", "审核拒绝"),
    BANNED("BANNED", "已封禁");

    @EnumValue
    private final String code;
    private final String description;
    
    StatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据状态编码获取枚举
     *
     * @param code 状态编码
     * @return 对应的枚举值，如果不存在则返回null
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