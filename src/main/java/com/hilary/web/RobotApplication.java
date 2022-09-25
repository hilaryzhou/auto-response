package com.hilary.web;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableSimbot
@EnableScheduling
@EnableAsync
public class RobotApplication {
    public static void main(String[] args) {
        SpringApplication.run(RobotApplication.class, args);
        log.info("机器人启动成功~~~~~~~~");
    }

    @Bean
    public IdWorker getIdWorker() {
        return new IdWorker();
    }
}
