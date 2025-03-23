package club._8b1t.model.vo;

import lombok.Data;
import java.util.Date;

/**
 * 用户地址VO
 */
@Data
public class CustomerAddressVO {
    
    /**
     * 地址ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String customerId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 地址标签（如家、公司、学校等）
     */
    private String tag;

    /**
     * 是否默认地址(1是，0否)
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 软删除标记
     */
    private Integer isDeleted;
} 