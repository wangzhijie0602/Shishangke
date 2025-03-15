package club._8b1t.model.dto.merchant;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家查询请求
 */
@Data
public class MerchantQueryRequest {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商家名称
     */
    private String name;
    
    /**
     * 商家联系电话
     */
    private String phone;
    
    /**
     * 商家状态
     */
    private String status;
}
