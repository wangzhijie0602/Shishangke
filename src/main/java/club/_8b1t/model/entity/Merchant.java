package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName merchant
 */
@TableName(value ="merchant")
@Data
public class Merchant {
    /**
     * 商家ID，主键，自增
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家logo图片URL
     */
    private String logo;

    /**
     * 商家联系电话
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
     * 街道
     */
    private String street;

    /**
     * 详细地址
     */
    private String addressDetail;

    /**
     * 每日开店时间，如09:00
     */
    private Date openTime;

    /**
     * 每日关店时间，如22:00
     */
    private Date closeTime;

    /**
     * 商家描述信息
     */
    private String description;

    /**
     * 最低起送价
     */
    private BigDecimal minPrice;

    /**
     * 商家状态
     */
    private Object status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     *
     */
    @TableLogic
    private Integer isDeleted;
}