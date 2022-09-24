package com.hilary.web.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:42
 * @description: 用户表
 **/
@Data
@TableName("t_robot")
public class Robot {
    @TableId(value = "code")
    private String code;
    private String password;
    private String description;
    private String receive;
}
