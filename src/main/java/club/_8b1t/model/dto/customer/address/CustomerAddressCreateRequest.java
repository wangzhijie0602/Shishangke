package club._8b1t.model.dto.customer.address;

import club._8b1t.model.entity.CustomerAddress;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户添加地址请求
 */
@Data
@AutoMapper(target = CustomerAddress.class)
public class CustomerAddressCreateRequest {
    
    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 地址标签（如家、公司、学校等）
     */
    private String tag;

    /**
     * 是否默认地址(1是，0否)
     */
    private Integer isDefault;
} 