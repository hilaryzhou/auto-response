package com.hilary.web.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: zhouhuan
 * @date: 2022-09-08 01:03
 * @description: 平台HTTP统一错误码枚举
 **/
@AllArgsConstructor
public enum HttpCodeEnum {
    /**
     * 成功
     */
    SUCCESS(0, "success"),
    /**
     * 未知的错误
     */
    UNKNOW_ERROR(-1, "unknow error"),
    /**
     * 系统异常
     */
    SYSTEM_ERROR(10001, "system error"),
    /**
     * 远程调用失败
     */
    REMOTE_SERVER_ERROR(10002, "remote server error"),
    /**
     * 无效参数
     */
    INVALID_PARAM_ERROR(10003, "invalid params"),
    /**
     * 参数不存在
     */
    REQUEST_PARAM_MISS(10004, "param miss"),
    /**
     * 目标不存在
     */
    TARGER_NOT_EXIST_ERROR(10005, "target not exist error"),
    /**
     * 权限错误
     */
    PERMISSION_ERROR(10006, "permission error"),
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(10007, "data not exist"),
    EDIT_BOT_ERROR(10008, "edit bot error"),
    ROBOT_ALREADY_EXISTS(10009, "robot already Exists"), LOGIN_ERROR(10010, "robot login error"), TOKEN_NOT_EXIST_ERROR(10011, "token not exist error"), TOKEN_CHECK_ERROR(10012, "token check error"),;

    @Getter
    int code;

    @Getter
    @Setter
    String msg;

}
