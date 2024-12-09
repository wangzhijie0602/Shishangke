package club._8b1t.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录接口的返回类
 *
 * @author 8bit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private String accessToken;
}
