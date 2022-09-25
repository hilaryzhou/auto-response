package com.hilary.web.config;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hilary.web.mapper.PropagandaMapper;
import com.hilary.web.mapper.RelationMapper;
import com.hilary.web.mapper.RobotMapper;
import com.hilary.web.model.Propaganda;
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
    private StringRedisTemplate stringRedisTemplate;
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
        List<String> codeList = robots.stream().map(robot -> ROBOT_CODE_PREFIX + robot.getCode()
        ).collect(Collectors.toList());
        for (String code : codeList) {
            // 先清空数据
            boolean flag = stringRedisTemplate.delete(code);
            System.out.println("flag = " + flag);
        }
        //查询机器人对应的目标对象
        for (String source : codeList) {
            String sourceCode = source.split(":")[1];
            List<String> targetCodes = relationMapper.selectList(Wrappers.lambdaQuery(Relation.class)
                    .eq(Relation::getSourceCode, sourceCode)).stream().map(Relation::getTargetCode).collect(Collectors.toList());
            String[] array = targetCodes.stream().map(code -> {
                String type = propagandaMapper.selectOne(Wrappers
                        .lambdaQuery(Propaganda.class).eq(Propaganda::getCode, code)
                        .last("limit 1")).getType();
                return type + ":" + code;
            }).toArray(String[]::new);
            stringRedisTemplate.opsForSet().add(source, array);
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
            log.info("初始化 SourceRoBot :{}", codeList);
        }
    }
}
