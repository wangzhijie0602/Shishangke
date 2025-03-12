package club._8b1t.model.dto.merchant;

import club._8b1t.model.entity.Merchant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@Data
@AutoMapper(target = Merchant.class)
public class MerchantCreateRequest {

    private String name;

    private String phone;

}