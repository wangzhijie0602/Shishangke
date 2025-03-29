package club._8b1t.model.enums.payment;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 支付状态枚举
 */
@Getter
public enum PaymentStatus {
    /**
     * 待支付 - 支付已创建但尚未完成
     */
    PENDING("PENDING", "待支付"),
    
    /**
     * 成功 - 支付已成功完成
     */
    SUCCESS("SUCCESS", "支付成功"),
    
    /**
     * 失败 - 支付处理失败
     */
    FAILED("FAILED", "支付失败"),
    
    /**
     * 已退款 - 支付已被退款
     */
    REFUNDED("REFUNDED", "已退款");

    @EnumValue
    private final String code;
    private final String desc;

    PaymentStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 状态编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static PaymentStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (PaymentStatus status : PaymentStatus.values()) {
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