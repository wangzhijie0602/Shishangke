package club._8b1t.model.entity;

import club._8b1t.model.vo.CustomerAddressVO;
import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;
import java.util.Date;

import lombok.Data;

/**
 * 用户外卖地址表
 * @TableName customer_address
 */
@TableName(value ="customer_address")
@Data
@AutoMapper(target = CustomerAddressVO.class)
public class CustomerAddress {
    /**
     * 地址ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（外键关联 customer_id）
     */
    private Long customerId;

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
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;

    /**
     * 软删除标记
     */
    @TableLogic
    private Integer isDeleted;

}