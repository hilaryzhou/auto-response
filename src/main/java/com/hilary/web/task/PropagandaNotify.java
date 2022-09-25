package com.hilary.web.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hilary.web.mapper.PropagandaMapper;
import com.hilary.web.model.Propaganda;
import com.hilary.web.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hilary.web.model.commons.BaseContants.*;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 10:31
 * @description: 喊话通知
 **/
@Component
@Slf4j
public class PropagandaNotify {

    @Resource
    private BotManager botManager;

    @Autowired
    PropagandaMapper propagandaMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Async
    @Scheduled(cron = "0 0/3 * * * ?")
    public void AutoPropagandaHandler() {
        List<Propaganda> propagandaList = propagandaMapper.selectList(null);
        //获取需要发送的msg
        List<String> contextList = propagandaList.stream()
                .filter(prod -> !prod.isSend()).map(Propaganda::getContext).collect(Collectors.toList());
        List<String> imageList = propagandaList.stream()
                .filter(prod -> !prod.isSend()).map(Propaganda::getImage).collect(Collectors.toList());

        //时间转换
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(System.currentTimeMillis());
        //获取所有机器人
        List<Bot> bots = botManager.getBots();
        for (Bot bot : bots) {
            String botCode = bot.getBotInfo().getBotCode();
            //获取每个机器人对应的receiveCode目标对象
            String key = ROBOT_CODE_PREFIX + botCode;
            Set<String> receiveCodeList = stringRedisTemplate.opsForSet().members(key);
            if (EmptyUtil.isNullOrEmpty(receiveCodeList)) {
                break;
            }
            if (!EmptyUtil.isNullOrEmpty(contextList)) {
                //循环发送消息
                io:
                for (String msg : contextList) {
                    //每个机器人对应的所有目标对象都发送一遍
                    for (String qq : receiveCodeList) {
                        String type = qq.split(":")[0];
                        qq = qq.split(":")[1];
                        LambdaQueryWrapper<Propaganda> wrapper = new LambdaQueryWrapper<>();
                        wrapper.eq(Propaganda::getContext, msg).eq(Propaganda::getCode, qq);
                        //目标对象
                        Propaganda propaganda = propagandaMapper.selectOne(wrapper);
                        //发送的消息里面有当前机器人要发的
                        if (!EmptyUtil.isNullOrEmpty(propaganda) && !EmptyUtil.isNullOrEmpty(propaganda.getType())
                                && propaganda.getCode().equals(qq) && propaganda.getContext().equals(msg)) {
                            //发送message
                            if (PRIVATE_MSG.equals(propaganda.getType())) {
                                //私聊
                                bot.getSender().SENDER.sendPrivateMsg(qq, msg);
                                log.info("{} => {} type :{}, msg: {}, time: {}", botCode, qq, type, msg, time);
                            } else if (GROUP_MSG.equals(propaganda.getType())) {
                                bot.getSender().SENDER.sendGroupMsg(qq, msg);
                                log.info("{} => {} type :{}, msg: {}, time: {}", botCode, qq, type, msg, time);
                            }
                        }
                        break io;
                    }
                }
            }
            if (!EmptyUtil.isNullOrEmpty(imageList)) {
                //循环发送图片
                imageList.forEach(image -> {
                    for (String qq : receiveCodeList) {
                        qq = qq.split(":")[1];
                        LambdaQueryWrapper<Propaganda> wrapper = new LambdaQueryWrapper<>();
                        wrapper.eq(Propaganda::getImage, image).eq(Propaganda::getCode, qq);
                        //目标对象
                        Propaganda propaganda = propagandaMapper.selectOne(wrapper);
                        if (!EmptyUtil.isNullOrEmpty(propaganda) && !EmptyUtil.isNullOrEmpty(propaganda.getType())
                                && propaganda.getCode().equals(qq) && propaganda.getImage().equals(image)) {
                            if (PRIVATE_MSG.equals(propaganda.getType())) {
                                //私聊
                                bot.getSender().SENDER.sendPrivateMsg(qq, image);
                                log.info("{} => {} type :{}, image :{}, time: {}", botCode, qq, propaganda.getType(), image, time);
                            } else {
                                bot.getSender().SENDER.sendGroupMsg(qq, image);
                                log.info("{} => {} type :{}, image :{}, time: {}", botCode, qq, propaganda.getType(), image, time);
                            }
                        }
                    }
                });
            }
        }
    }
}

