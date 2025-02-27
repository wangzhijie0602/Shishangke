package club._8b1t.util;

import club._8b1t.common.Result;

public class ResultUtil {
    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    public static Result success(String message) {
        return new Result(1, message, null);
    }
    public static Result success(String message, Object data) {
        return new Result(1, message, data);
    }

    public static Result error() {
        return new Result(0, "error", null);
    }

    public static Result error(String message) {
        return new Result(0, message, null);
    }

    public static Result error(String message, Object data) {
        return new Result(0, message, data);
    }

    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }

    public static Result error(int code, String message, Object data) {
        return new Result(code, message, data);
    }
}
