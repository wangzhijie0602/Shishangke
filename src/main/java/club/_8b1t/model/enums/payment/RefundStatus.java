package club._8b1t.model.enums.payment;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 退款状态枚举
 */
@Getter
public enum RefundStatus {
    /**
     * 待处理 - 退款申请已提交，等待处理
     */
    PENDING("PENDING", "待处理"),
    
    /**
     * 成功 - 退款已成功处理
     */
    SUCCESS("SUCCESS", "退款成功"),
    
    /**
     * 失败 - 退款处理失败
     */
    FAILED("FAILED", "退款失败");

    @EnumValue
    private final String code;
    private final String desc;

    RefundStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    /**
     * 根据code获取枚举实例
     * 
     * @param code 状态编码
     * @return 对应的枚举实例，若不存在则返回null
     */
    public static RefundStatus getByCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (RefundStatus status : RefundStatus.values()) {
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