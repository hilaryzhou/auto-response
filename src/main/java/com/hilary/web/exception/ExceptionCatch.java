package com.hilary.web.exception;


import com.hilary.web.model.commons.Response;
import com.hilary.web.model.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author huan
 * @serial 每天一百行, 致敬未来的自己
 * @Description 目的是给用户提供友好的提示信息
 */
@Slf4j
@Configuration
@RestControllerAdvice   // Springmvc 异常处理拦截注解
public class ExceptionCatch {
    /**
     * 解决项目中所有的异常拦截
     *
     * @return
     */
    @ExceptionHandler(Exception.class)  // exception 所有子类
    public Response exception(Exception ex) {
        // 记录日志
        log.error("捕获到全局异常 ex:{}", ex);
        return Response.failed(HttpCodeEnum.SYSTEM_ERROR, "系统异常，请稍后重试");
    }

    /**
     * 捕获自定义异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BaseException.class)  // exception 所有子类
    public Response custom(BaseException ex) {
        // 记录日志
        log.error("捕获到自定义异常 ex:{}", ex);
        return Response.failed(ex.getHttpCodeEnum());
    }

    /**
     * 参数校验
     *
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException ex:{}", ex);
        ex.printStackTrace();
        return Response.failed(HttpCodeEnum.INVALID_PARAM_ERROR, ex.getBindingResult().getFieldError().getDefaultMessage());
    }
}