package com.hilary.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hilary.web.exception.CustException;
import com.hilary.web.mapper.PropagandaMapper;
import com.hilary.web.mapper.RelationMapper;
import com.hilary.web.model.Propaganda;
import com.hilary.web.model.Relation;
import com.hilary.web.model.Robot;
import com.hilary.web.model.enums.HttpCodeEnum;
import com.hilary.web.utils.EmptyUtil;
import com.hilary.web.utils.ThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:59
 * @description:
 **/
@Service
public class SdkServiceImpl extends ServiceImpl<PropagandaMapper,Propaganda> implements SdkService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    PropagandaMapper propagandaMapper;
    @Autowired
    RelationMapper relationMapper;

    @Override
    public List<Propaganda> list() {
        Robot robot = ThreadLocalUtils.getrobot();
        if (EmptyUtil.isNullOrEmpty(robot)) {
            CustException.cust(HttpCodeEnum.DATA_NOT_EXIST,"机器人不存在,请先注册");
        }
        //根据qq查询对应的喊话数据
        LambdaQueryWrapper<Propaganda> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Propaganda::getCode, robot.getCode()).orderByDesc(Propaganda::getTime);
        return propagandaMapper.selectList(wrapper);
    }

    @Override
    public void edit(Propaganda propaganda) {
        Robot robot = ThreadLocalUtils.getrobot();
        String source = robot.getCode();
        propaganda.setTime(new Date());
        LambdaUpdateWrapper<Propaganda> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Propaganda::getCode, source);
        super.saveOrUpdate(propaganda, wrapper);
        boolean flag = propagandaMapper.exists(wrapper);
        Relation relation = new Relation(null, source, propaganda.getCode());
        if (flag) {
            relationMapper.updateById(relation);
        }
        relationMapper.insert(relation);
        redisTemplate.opsForSet().add(source, propaganda.getCode());
    }

    @Override
    public void delete(String id) {
        super.removeById(id);
    }
}
