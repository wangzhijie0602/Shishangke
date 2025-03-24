package club._8b1t.util;

import club._8b1t.exception.BusinessException;
import club._8b1t.exception.ResultCode;

/**
 * 异常工具类，用于简化异常处理
 */
public class ExceptionUtil {

    /**
     * 抛出业务异常
     *
     * @param resultCode 结果码
     * @param message    错误消息
     */
    public static void throwBizException(ResultCode resultCode, String message) {
        throw new BusinessException(resultCode, message);
    }

    /**
     * 抛出业务异常（使用默认消息）
     *
     * @param resultCode 结果码
     */
    public static void throwBizException(ResultCode resultCode) {
        throw new BusinessException(resultCode);
    }

    /**
     * 条件为真时抛出异常
     *
     * @param condition  条件
     * @param resultCode 结果码
     * @param message    错误消息
     */
    public static void throwIf(boolean condition, ResultCode resultCode, String message) {
        if (condition) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 条件为真时抛出异常（使用默认消息）
     *
     * @param condition  条件
     * @param resultCode 结果码
     */
    public static void throwIf(boolean condition, ResultCode resultCode) {
        if (condition) {
            throw new BusinessException(resultCode);
        }
    }

    /**
     * 对象为空时抛出异常
     *
     * @param obj        检查对象
     * @param resultCode 结果码
     * @param message    错误消息
     */
    public static void throwIfNull(Object obj, ResultCode resultCode, String message) {
        if (obj == null) {
            throw new BusinessException(resultCode, message);
        }
    }

    /**
     * 对象为空时抛出异常（使用默认消息）
     *
     * @param obj        检查对象
     * @param resultCode 结果码
     */
    public static void throwIfNull(Object obj, ResultCode resultCode) {
        if (obj == null) {
            throw new BusinessException(resultCode);
        }
    }
    
    /**
     * 条件为假时抛出异常
     *
     * @param condition  条件
     * @param resultCode 结果码
     * @param message    错误消息
     */
    public static void throwIfNot(boolean condition, ResultCode resultCode, String message) {
        if (!condition) {
            throw new BusinessException(resultCode, message);
        }
    }
    
    /**
     * 条件为假时抛出异常（使用默认消息）
     *
     * @param condition  条件
     * @param resultCode 结果码
     */
    public static void throwIfNot(boolean condition, ResultCode resultCode) {
        if (!condition) {
            throw new BusinessException(resultCode);
        }
    }
} 