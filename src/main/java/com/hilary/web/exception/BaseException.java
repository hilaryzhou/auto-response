package com.hilary.web.exception;


import com.hilary.web.model.enums.HttpCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @Description:
 * @Auther: zhouhuan
 * @Date: 2022/8/8-16:26
 */
@Data
@Slf4j
public class BaseException extends RuntimeException {


    // 异常处理的枚举
    private HttpCodeEnum httpCodeEnum;

    public BaseException(HttpCodeEnum httpCodeEnum) {
        this.httpCodeEnum = httpCodeEnum;
    }

    public BaseException(HttpCodeEnum httpCodeEnum, String msg) {
        httpCodeEnum.setMsg(msg);
        this.httpCodeEnum = httpCodeEnum;
    }
    public BaseException(String message, HttpCodeEnum httpCodeEnum) {
        super(message);
        this.httpCodeEnum = httpCodeEnum;
    }
    public HttpCodeEnum getHttpCodeEnum() {
        return httpCodeEnum;
    }

    public String getErrorMessage() {
        return this.httpCodeEnum.getMsg();
    }
}



