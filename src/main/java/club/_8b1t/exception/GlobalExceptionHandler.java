package club._8b1t.exception;

import club._8b1t.model.response.BaseResult;
import club._8b1t.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 *
 * @author 8bit
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     *
     * @param e 异常对象
     * @return 包含异常信息的 JSON 响应
     */
    @ExceptionHandler(Exception.class)
    public BaseResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ResultUtil.error(e.getMessage());
    }
}
