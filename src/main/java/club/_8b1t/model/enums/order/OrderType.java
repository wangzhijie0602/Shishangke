package club._8b1t.model.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 订单类型枚举
 */
@Getter
public enum OrderType {
    /**
     * 外卖订单 - 需要配送的订单
     */
    DELIVERY("DELIVERY", "外卖"),
    
    /**
     * 堂食订单 - 在店内用餐的订单
     */
    DINE_IN("DINE_IN", "堂食");

    @EnumValue
    private final String code;
    private final String desc;

    OrderType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 类型编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static OrderType getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (OrderType type : OrderType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        
        return null;
    }

    /**
     * 检查类型编码是否有效
     * 
     * @param code 类型编码
     * @return 是否是有效的类型编码
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
} 