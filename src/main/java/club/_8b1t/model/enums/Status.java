package club._8b1t.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author 8bit
 */
@Getter
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive"),
    SUSPENDED("suspended");

    @EnumValue
    private final String value;

    Status(String value) {
        this.value = value;
    }

}
