package club._8b1t.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MerchantVO {

    private String id;

    private String userId;

    private String name;

    private String logo;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String street;

    private String addressDetail;

    private String openTime;

    private String closeTime;

    private String description;

    private BigDecimal minPrice;

    private String status;

    private Date createTime;

}
