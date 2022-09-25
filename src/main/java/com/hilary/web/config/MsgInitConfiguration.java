package com.hilary.web.config;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hilary.web.mapper.PropagandaMapper;
import com.hilary.web.mapper.RelationMapper;
import com.hilary.web.mapper.RobotMapper;
import com.hilary.web.model.Relation;
import com.hilary.web.model.Robot;
import com.hilary.web.utils.EmptyUtil;
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
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RobotMapper robotMapper;
    @Autowired
    PropagandaMapper propagandaMapper;
    @Autowired
    RelationMapper relationMapper;


    @PostConstruct
    public void init() {
        //所有机器人
        List<Robot> robots = robotMapper.selectList(null);
        if (EmptyUtil.isNullOrEmpty(robots)) {
            return;
        }
        List<String> codeList = robots.stream().map(robot -> ROBOT_CODE_PREFIX + robot.getCode()
        ).collect(Collectors.toList());
        // 先清空数据
        codeList.forEach(code -> stringRedisTemplate.delete(code));
        //查询机器人对应的目标对象
        for (String source : codeList) {
            String sourceCode = source.split(":")[1];
            String[] targetCodes = relationMapper.selectList(Wrappers.lambdaQuery(Relation.class)
                    .eq(Relation::getSourceCode, sourceCode)).stream().map(code ->
                    String.valueOf(code.getId())).toArray(String[]::new);
            if (!EmptyUtil.isNullOrEmpty(targetCodes)) {
                stringRedisTemplate.opsForSet().add(source, targetCodes);
            }
            Robot robot = new Robot();
            robot.setCode(source);
            robot.setReceive(JSONArray.toJSONString(targetCodes));
            robotMapper.updateById(robot);
            //初始化机器人脚本
            robots.stream().map(Robot::getPassword).forEach(password -> {
                String fileName = ROBOT_FILE_PREFIX + robot.getCode() + ROBOT_SUFFIX;
                PropertiesUtils.setValue(ROBOT_CODE, robot.getCode(), fileName);
                PropertiesUtils.setValue(ROBOT_VERIFICATION, password, fileName);
            });
            log.info("初始化 SourceRoBot :{}", codeList);
        }
    }
}
