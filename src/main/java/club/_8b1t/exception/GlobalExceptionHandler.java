package club._8b1t.exception;

import cn.dev33.satoken.util.SaResult;
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

    /**
     * 处理所有未捕获的异常
     *
     * @param e 异常对象
     * @return 包含异常信息的 JSON 响应
     */
    @ExceptionHandler(Exception.class)
    public SaResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return SaResult.error(e.getMessage());
    }
}
