package club._8b1t.exception;

import club._8b1t.common.Result;
import club._8b1t.util.ResultUtil;
import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author 8bit
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(NotLoginException.class)
    public Result<String> handleNotLoginException(NotLoginException nle) {
        return ResultUtil.error(40100, "用户未登录", nle.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException be) {
        log.error(be.getMessage(), be);
        return ResultUtil.error(be.getCode(), be.getMessage());
    }

    /**
     * 处理所有未捕获的异常
     *
     * @param e 异常对象
     * @return 包含异常信息的 JSON 响应
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResultUtil.error(e.getMessage());
    }
}
