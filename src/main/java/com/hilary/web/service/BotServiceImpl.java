package com.hilary.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hilary.web.exception.CustException;
import com.hilary.web.mapper.RobotMapper;
import com.hilary.web.model.Robot;
import com.hilary.web.model.commons.Response;
import com.hilary.web.model.enums.HttpCodeEnum;
import com.hilary.web.utils.EmptyUtil;
import com.hilary.web.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.hilary.web.model.commons.BaseContants.*;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 14:06
 * @description:
 **/
@Service
@Slf4j
public class BotServiceImpl extends ServiceImpl<RobotMapper, Robot> implements BotService {
    @Autowired
    RobotMapper robotMapper;
    @Override
    public void addRoBot(Robot robot) {
        String fileName = ROBOT_FILE_PREFIX + robot.getCode() + ROBOT_SUFFIX;
        try {
            PropertiesUtils.setValue(ROBOT_CODE, robot.getCode(), fileName);
            PropertiesUtils.setValue(ROBOT_VERIFICATION, robot.getPassword(), fileName);
        } catch (Exception e) {
            CustException.cust(HttpCodeEnum.EDIT_BOT_ERROR);
        }
        //保存数据库
        robot.setPassword(robot.getPassword());
        try {
            robotMapper.insert(robot);
        } catch (Exception e) {
            CustException.cust(HttpCodeEnum.ROBOT_ALREADY_EXISTS);
        }
        log.info("注册机器人成功 code:{}", robot.getCode());
    }

    @Override
    public void removeRobot(String robotCode) {
        String filePath = ROBOT_FILE_PREFIX + robotCode + ROBOT_SUFFIX;
        File file = new File(filePath);
        //删除机器人
        file.delete();
        robotMapper.deleteById(robotCode);
        log.info("删除机器人成功");

    }

    @Override
    public void updateRobot(Robot robot) {
        try {
            String fileName = ROBOT_FILE_PREFIX + robot.getCode() + ROBOT_SUFFIX;
            PropertiesUtils.setValue(ROBOT_CODE, robot.getCode(), fileName);
            PropertiesUtils.setValue(ROBOT_VERIFICATION, robot.getPassword(), fileName);
        } catch (Exception e) {
            CustException.cust(HttpCodeEnum.EDIT_BOT_ERROR);
        }
        log.info("编辑机器人成功 code:{}", robot.getCode());
        robotMapper.updateById(robot);
    }

    @Override
    public List<Robot> list() {
        return robotMapper.selectList(null);
    }

    @Override
    public Response login(Robot robot) {
        //校验用户是否存在
        LambdaQueryWrapper<Robot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Robot::getCode, robot.getCode());
        robot = super.getOne(wrapper);
        if (!EmptyUtil.isNullOrEmpty(robot)) {
            //登录成功
            HashMap<String, Object> map = new HashMap<>();
            //将密码置空(安全考虑)
            robot.setPassword("");
            map.put("robot", robot);
            return Response.ok(map);
        } else {
            return Response.failed(HttpCodeEnum.LOGIN_ERROR);
        }
    }
}
