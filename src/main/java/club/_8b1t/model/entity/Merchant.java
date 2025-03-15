package club._8b1t.model.entity;

import club._8b1t.model.vo.MerchantVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

/**
 * 
 * @TableName merchant
 */
@TableName(value ="merchant")
@Data
@AutoMapper(target = MerchantVO.class)
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
     * 每日开店时间，格式：HH:mm
     */
    private String openTime;

    /**
     * 每日关店时间，格式：HH:mm
     */
    private String closeTime;

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
    private String status;

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