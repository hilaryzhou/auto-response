package com.hilary.web.task;

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

import static com.hilary.web.model.commons.BaseContants.PRIVATE_MSG;
import static com.hilary.web.model.commons.BaseContants.ROBOT_CODE_PREFIX;

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
    public void autoPropagandaHandler() {
        //时间转换
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(System.currentTimeMillis());
        //获取所有机器人
        List<Bot> bots = botManager.getBots();
        for (Bot bot : bots) {
            String botCode = bot.getBotInfo().getBotCode();
            //获取每个机器人对应的receiveCode要发送的消息
            String key = ROBOT_CODE_PREFIX + botCode;
            Set<String> receiveCodeList = stringRedisTemplate.opsForSet().members(key);
            if (EmptyUtil.isNullOrEmpty(receiveCodeList)) {
                break;
            }
            receiveCodeList.forEach(receive -> {
                Propaganda propaganda = propagandaMapper.selectById(receive);
                String code = propaganda.getCode();
                String msg = propaganda.getContext();
                String image = propaganda.getImage();
                if (EmptyUtil.isNullOrEmpty(propaganda.getContext()) && !EmptyUtil.isNullOrEmpty(propaganda.getImage())) {
                    //只发图片
                    if (PRIVATE_MSG.equals(propaganda.getType())) {
                        bot.getSender().SENDER.sendPrivateMsg(code, image);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    } else {
                        bot.getSender().SENDER.sendGroupMsg(code, image);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    }
                } else if (!EmptyUtil.isNullOrEmpty(propaganda.getContext()) && EmptyUtil.isNullOrEmpty(propaganda.getImage())) {
                    //只发文字
                    if (PRIVATE_MSG.equals(propaganda.getType())) {
                        bot.getSender().SENDER.sendPrivateMsg(code, msg);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    } else {
                        bot.getSender().SENDER.sendGroupMsg(code, msg);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    }
                } else if (!EmptyUtil.isNullOrEmpty(propaganda.getContext()) && !EmptyUtil.isNullOrEmpty(propaganda.getImage())) {
                    //只发文字
                    if (PRIVATE_MSG.equals(propaganda.getType())) {
                        bot.getSender().SENDER.sendPrivateMsg(code, msg);
                        bot.getSender().SENDER.sendPrivateMsg(code, image);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    } else {
                        bot.getSender().SENDER.sendGroupMsg(code, msg);
                        bot.getSender().SENDER.sendGroupMsg(code, image);
                        log.info("{} => {} type :{}, msg: {}, image: {},time: {}", botCode, code, propaganda.getType(), msg, image, time);
                    }
                }
            });
        }
    }
}





