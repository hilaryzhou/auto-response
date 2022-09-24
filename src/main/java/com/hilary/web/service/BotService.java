package com.hilary.web.service;

import com.hilary.web.model.Robot;
import com.hilary.web.model.commons.Response;

import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 14:06
 * @description:
 **/
public interface BotService {
    /**
     * 添加机器人
     * @param robot
     */
    void addRoBot(Robot robot);

    /**
     * 删除机器人
     * @param robotCode
     */
    void removeRobot(String robotCode);

    /**
     * 编辑机器人
     * @param robot
     */
    void updateRobot(Robot robot);

    /**
     * 查询所有机器人
     * @return List<Robot>
     */
    List<Robot> list();

    Response login(Robot robot);
}
