package com.hilary.web.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 20:42
 * @description:
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_relation")
public class Relation {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField("source_code")
    private String sourceCode;
    @TableField("target_code")
    private String targetCode;
    /**
     * private/group
     */
    private String type;
}
