package com.hilary.web.controller;

import com.hilary.web.model.Robot;
import com.hilary.web.model.commons.Response;
import com.hilary.web.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 14:04
 * @description:
 **/
@RestController
@RequestMapping("/robot")
public class BotController {

    @Autowired
    BotService botService;

    @PostMapping("/regist")
    public Response registerBot(@RequestBody Robot robot) {
        botService.addRoBot(robot);
        return Response.ok();
    }

    @PostMapping("/del")
    public Response deleteForBot(@RequestBody String robotCode) {
        botService.removeRobot(robotCode);
        return Response.ok();
    }

    @PostMapping("/edit")
    public Response updateBot(@RequestBody Robot robot) {
        botService.updateRobot(robot);
        return Response.ok();
    }

    @PostMapping("/list")
    public Response queryAllBot() {
        List<Robot> list = botService.list();
        return Response.ok(list);
    }

    @PostMapping("/login")
    public Response login(@RequestBody Robot robot) {
        return botService.login(robot);
    }

}
