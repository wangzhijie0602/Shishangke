package club._8b1t.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * aop实现日志的输出
 *
 * @author 8bit
 * @version 1.0
 * @since 1.0
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // 定义切入点：匹配所有Controller类的方法
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    // 在方法执行前记录日志
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("执行方法: {} 类: {} 参数: {}", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getName(), joinPoint.getArgs());
    }

    // 在方法执行后记录日志
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("退出方法: {} 类: {} 结果: {}", joinPoint.getSignature().getName(), joinPoint.getTarget().getClass().getName(), result);
    }
}
