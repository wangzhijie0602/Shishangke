package club._8b1t.model.enums.menu;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 菜品状态枚举
 */
@Getter
public enum MenuStatus {
    /**
     * 启用 - 菜品可被展示和购买
     */
    ENABLED("ENABLED", "启用"),
    
    /**
     * 禁用 - 菜品不可被展示和购买
     */
    DISABLED("DISABLED", "禁用"),
    
    /**
     * 售罄 - 菜品当前已售完
     */
    SOLD_OUT("SOLD_OUT", "售罄");

    @EnumValue
    private final String code;
    private final String desc;

    MenuStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 状态编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static MenuStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (MenuStatus status : MenuStatus.values()) {
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
    
    @Override
    public String toString() {
        return this.desc;
    }
} 