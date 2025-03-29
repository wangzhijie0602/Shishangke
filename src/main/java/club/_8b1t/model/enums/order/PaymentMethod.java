package club._8b1t.model.enums.order;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PaymentMethod {
    /**
     * 微信支付
     */
    WECHAT("WECHAT", "微信支付"),
    
    /**
     * 支付宝支付
     */
    ALIPAY("ALIPAY", "支付宝"),
    
    /**
     * 信用卡支付
     */
    CREDIT_CARD("CREDIT_CARD", "信用卡"),
    
    /**
     * 现金支付
     */
    CASH("CASH", "现金");

    @EnumValue
    private final String code;
    private final String desc;

    PaymentMethod(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 支付方式编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static PaymentMethod getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        
        return null;
    }
    
    /**
     * 检查支付方式编码是否有效
     *
     * @param code 支付方式编码
     * @return 是否是有效的支付方式编码
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
    
    @Override
    public String toString() {
        return this.desc;
    }
} 