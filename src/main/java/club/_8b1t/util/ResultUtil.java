package club._8b1t.util;

import club._8b1t.model.dto.response.BaseResult;

/**
 * 统一返回类型
 *
 * @author 8bit
 */
public class ResultUtil {

    public static <T> BaseResult<T> success() {
        return new BaseResult<>(1, "success", null);
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<>(1, "success", data);
    }

    public static <T> BaseResult<T> error(String msg) {
        return new BaseResult<>(0, msg, null);
    }
}
