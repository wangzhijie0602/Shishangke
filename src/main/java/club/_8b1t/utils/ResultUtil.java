package club._8b1t.utils;

import club._8b1t.model.response.BaseResult;

/**
 * 统一返回类型
 *
 * @author 8bit
 */
public class ResultUtil {

    public static BaseResult success() { return new BaseResult(1, "success", null); }

    public static BaseResult success(Object data) { return new BaseResult(1, "success", data); }

    public static BaseResult error(String msg) { return new BaseResult(0, msg, null); }
}
