package club._8b1t.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 顾客信息表
 * @TableName customer
 */
@TableName(value ="customer")
@Data
public class Customer {
    /**
     * 顾客ID，主键，自增
     */
    @TableId(type = IdType.AUTO)
    private Long customerId;

    /**
     * 关联的用户ID（外键关联 user.id）
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别(MALE:男, FEMALE:女, OTHER:其他)
     */
    private String gender;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 默认收货地址
     */
    private String defaultAddress;

    /**
     * 饮食偏好（JSON格式）
     */
    private String preferences;

    /**
     * VIP等级
     */
    private Integer vipLevel;

    /**
     * 积分
     */
    private Integer points;

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