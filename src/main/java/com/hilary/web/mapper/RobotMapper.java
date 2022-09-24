package com.hilary.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hilary.web.model.Robot;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 14:19
 * @description:
 **/
@Mapper
public interface RobotMapper extends BaseMapper<Robot> {
}
