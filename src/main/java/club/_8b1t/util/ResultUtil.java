package club._8b1t.util;

import club._8b1t.common.Result;

public class ResultUtil {
    public static <T> Result<T> success() {
        return new Result<>(1, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(1, message, null);
    }
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(1, message, data);
    }

    public static <T> Result<T> error() {
        return new Result<>(0, "error", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(0, message, null);
    }

    public static <T> Result<T> error(String message, T data) {
        return new Result<>(0, message, data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }
}
