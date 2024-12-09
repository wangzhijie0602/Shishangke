package club._8b1t.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 用户密码加密工具类
 *
 * @author 8bit
 */
public class PasswordEncoderUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private static final String SALT = "educational-platform";

    /**
     * 加密密码
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public static String encodePassword(String rawPassword) {
        return ENCODER.encode(rawPassword + SALT);
    }

    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 密码是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword + SALT, encodedPassword);
    }
}
