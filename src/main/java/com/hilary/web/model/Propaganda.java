package com.hilary.web.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:26
 * @description: 喊话内容实体
 **/
@Data
@TableName("t_propaganda")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Propaganda {
    /**
     * 主键
     */
    @TableId("id")
    private String id;
    /**
     * 用户qq
     */
    private String code;
    /**
     * 消息内容
     */
    private String context;
    /**
     * 是否发送 0 true 1 false
     */
    @TableField("is_send")
    private boolean isSend;
    /**
     * 图片地址
     */
    private String image;
    /**
     * 创建时间
     */
    private Date time;
    /**
     * private/group
     */
    private String type;
}
