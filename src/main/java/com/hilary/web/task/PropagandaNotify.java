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
import java.util.stream.Stream;

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

    private static final String ROBOT_CODE_PREFIX = "botCode:";
    @Autowired
    StringRedisTemplate redisTemplate;

    @Async
    @Scheduled(cron = "0 0/1 * * * ?")
    public void AutoPropagandaHandler() {
        //获取需要发送的msg
        Stream<String> contextList = propagandaMapper.selectList(null).stream()
                .filter(Propaganda::isSend).map(Propaganda::getContext);
        if (EmptyUtil.isNullOrEmpty(contextList)) {
            log.info("无消息需要发送");
            return;
        }
        //获取所有机器人
        List<Bot> bots = botManager.getBots();
        bots.forEach(bot -> {
            String botCode = bot.getBotInfo().getBotCode();
            //获取每个机器人对应的receiveCode目标对象
            Set<String> receiveCodeList = redisTemplate.opsForSet().members(ROBOT_CODE_PREFIX + botCode);
            contextList.forEach(msg -> {
                LambdaQueryWrapper<Propaganda> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(!EmptyUtil.isNullOrEmpty(msg), Propaganda::getContext, msg);
                //目标对象
                Propaganda propaganda = propagandaMapper.selectOne(wrapper);
                //每个机器人对应的所有目标对象都发送一遍
                receiveCodeList.forEach(qq -> {
                    //发送的消息里面有当前机器人要发的
                    if (propaganda.getCode().equals(qq)) {
                        //发送message
                        bot.getSender().SENDER.sendPrivateMsg(qq, msg);
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
                        log.info("{} 对 {} 发送了一条消息: msg: {} time: {}", botCode, qq, msg, time);
                        if (!EmptyUtil.isNullOrEmpty(propaganda.getImage())) {
                            bot.getSender().SENDER.sendPrivateMsg(qq, propaganda.getImage());
                        }
                    }
                });
            });
        });
    }
}
