package club._8b1t.exception;

import lombok.Getter;

/**
 * 统一响应状态码
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(20000, "操作成功"),

    // 客户端错误 (4xx)
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未授权"),
    FORBIDDEN(40300, "禁止访问"),
    NOT_FOUND(40400, "请求资源不存在"),
    METHOD_NOT_ALLOWED(40500, "请求方法不允许"),
    REQUEST_TIMEOUT(40800, "请求超时"),
    CONFLICT(40900, "资源冲突"),
    PAYLOAD_TOO_LARGE(41300, "请求体过大"),
    UNSUPPORTED_MEDIA_TYPE(41500, "不支持的媒体类型"),
    TOO_MANY_REQUESTS(42900, "请求过于频繁"),

    // 认证和授权错误
    NOT_LOGIN(40100, "未登录"),
    NO_AUTH(40101, "无权限"),
    TOKEN_EXPIRED(40102, "令牌已过期"),
    TOKEN_INVALID(40103, "无效的令牌"),

    // 服务器错误 (5xx)
    INTERNAL_SERVER_ERROR(50000, "服务器内部错误"),
    SERVICE_UNAVAILABLE(50300, "服务不可用"),
    GATEWAY_TIMEOUT(50400, "网关超时"),

    // 业务错误
    OPERATION_FAILED(50001, "操作失败"),
    DATA_ALREADY_EXISTS(50002, "数据已存在"),
    DATA_NOT_EXISTS(50003, "数据不存在"),
    UPLOAD_FAILED(50004, "上传失败"),
    DOWNLOAD_FAILED(50005, "下载失败"),
    PROCESSING_FAILED(50006, "处理失败"),
    VALIDATION_FAILED(50007, "数据验证失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}