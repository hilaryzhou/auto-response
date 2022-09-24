package com.hilary.web.config;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hilary.web.mapper.RelationMapper;
import com.hilary.web.mapper.RobotMapper;
import com.hilary.web.model.Relation;
import com.hilary.web.model.Robot;
import com.hilary.web.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.hilary.web.model.commons.BaseContants.*;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 19:03
 * @description:
 **/
@Component
@Slf4j
public class MsgInitConfiguration {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RobotMapper robotMapper;
    @Autowired
    RelationMapper relationMapper;

    @PostConstruct
    public void init() {
        //所有机器人
        List<Robot> robots = robotMapper.selectList(null);
        List<String> codeList = robots.stream().map(Robot::getCode).collect(Collectors.toList());
        // 先清空数据
        redisTemplate.delete(codeList);
        //查询机器人对应的目标对象
        codeList.forEach(source -> {
            List<String> targetList = relationMapper.selectList(Wrappers.lambdaQuery(Relation.class)
                    .eq(Relation::getSourceCode, source)).stream().map(Relation::getTargetCode).collect(Collectors.toList());
            String[] array = targetList.toArray(new String[targetList.size()]);
            redisTemplate.opsForSet().add(source, array);
            Robot robot = new Robot();
            robot.setCode(source);
            robot.setReceive(JSONArray.toJSONString(array));
            robotMapper.updateById(robot);
            //初始化机器人脚本
            robots.stream().map(Robot::getPassword).forEach(password -> {
                String fileName = ROBOT_FILE_PREFIX + robot.getCode() + ROBOT_SUFFIX;
                PropertiesUtils.setValue(ROBOT_CODE, robot.getCode(), fileName);
                PropertiesUtils.setValue(ROBOT_VERIFICATION, password, fileName);
            });

        });
        log.info("初始化 SourceRoBot :{}", codeList);
    }


}
