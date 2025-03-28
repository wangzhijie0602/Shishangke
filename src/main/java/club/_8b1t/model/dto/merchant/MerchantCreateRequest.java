package club._8b1t.model.dto.merchant;

import club._8b1t.model.entity.Merchant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AutoMapper(target = Merchant.class)
public class MerchantCreateRequest {

    private String name;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String street;

    private String addressDetail;

    private BigDecimal minPrice;

}