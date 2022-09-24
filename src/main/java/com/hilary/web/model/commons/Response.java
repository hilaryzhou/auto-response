package com.hilary.web.model.commons;

import com.hilary.web.model.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: zhouhuan
 * @date: 2022-09-08 00:57
 * @description: 通用响应信息主体
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private T data;

    public static <T> Response<T> ok() {
        return restResult(null, HttpCodeEnum.SUCCESS);
    }

    public static <T> Response<T> ok(T data) {
        return restResult(data, HttpCodeEnum.SUCCESS);
    }

    public static <T> Response<T> ok(T data, String msg) {
        return restResult(data, HttpCodeEnum.SUCCESS, msg);
    }

    public static <T> Response<T> failed(HttpCodeEnum codeEnum) {
        return restResult(null, codeEnum);
    }

    public static <T> Response<T> failed(HttpCodeEnum codeEnum, String msg) {
        return restResult(null, codeEnum, msg);
    }

    private static <T> Response<T> restResult(T data, HttpCodeEnum httpCodeEnum) {
        Response<T> apiResult = new Response<>();
        apiResult.setData(data);
        apiResult.setCode(httpCodeEnum.getCode());
        apiResult.setMsg(httpCodeEnum.getMsg());
        return apiResult;
    }

    private static <T> Response<T> restResult(T data, HttpCodeEnum httpCodeEnum, String msg) {
        Response<T> apiResult = new Response<>();
        apiResult.setData(data);
        apiResult.setCode(httpCodeEnum.getCode());
        apiResult.setMsg(msg);
        return apiResult;
    }

    public boolean checkCode() {
        return this.getCode() == 0;
    }
}
