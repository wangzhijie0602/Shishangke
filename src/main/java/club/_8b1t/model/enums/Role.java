package club._8b1t.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author 8bit
 */
@Getter
public enum Role {
    ADMIN("admin"),
    TEACHER("teacher"),
    STUDENT("student");

    @EnumValue
    private final String value;

    Role(String value) {
        this.value = value;
    }

}
