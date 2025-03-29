package club._8b1t.model.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatus {
    /**
     * 待支付 - 订单已创建但尚未支付
     */
    PENDING("PENDING", "待支付"),
    
    /**
     * 已支付 - 订单已完成支付
     */
    PAID("PAID", "已支付"),
    
    /**
     * 准备中 - 商家正在准备订单
     */
    PREPARING("PREPARING", "准备中"),
    
    /**
     * 配送中 - 订单正在配送
     */
    DELIVERING("DELIVERING", "配送中"),
    
    /**
     * 已完成 - 订单已送达并完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已退款 - 订单已退款
     */
    REFUNDED("REFUNDED", "已退款"),

    /**
     * 退款中 - 订单正在退款
     */
    REFUNDING("REFUNDING", "退款中"),

    /**
     * 已取消 - 订单已被取消
     */
    CANCELLED("CANCELLED", "已取消");

    @EnumValue
    private final String code;
    private final String desc;

    OrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 状态编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static OrderStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (OrderStatus status : OrderStatus.values()) {
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