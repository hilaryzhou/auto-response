package com.hilary.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
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

import static com.hilary.web.model.commons.BaseContants.ROBOT_CODE_PREFIX;

/**
 * @author: zhouhuan
 * @date: 2022-09-24 12:59
 * @description:
 **/
@Service
public class SdkServiceImpl extends ServiceImpl<PropagandaMapper, Propaganda> implements SdkService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    PropagandaMapper propagandaMapper;
    @Autowired
    RelationMapper relationMapper;

    @Override
    public List<Propaganda> list() {
        Robot robot = ThreadLocalUtils.getrobot();
        if (EmptyUtil.isNullOrEmpty(robot)) {
            CustException.cust(HttpCodeEnum.DATA_NOT_EXIST, "机器人不存在,请先注册");
        }
        //根据qq查询对应的喊话数据
        LambdaQueryWrapper<Propaganda> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Propaganda::getCode, robot.getCode()).orderByDesc(Propaganda::getTime);
        return propagandaMapper.selectList(wrapper);
    }

    @Override
    public void edit(Propaganda propaganda) {
        propaganda.setId(IdWorker.getId());
        Robot robot = ThreadLocalUtils.getrobot();
        String source = robot.getCode();
        propaganda.setTime(new Date());
        LambdaUpdateWrapper<Propaganda> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Propaganda::getCode, source).eq(Propaganda::getContext, propaganda.getContext());
        super.saveOrUpdate(propaganda, wrapper);
        LambdaQueryWrapper<Propaganda> queryWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Propaganda::getCode, source).eq(Propaganda::getCode, propaganda.getCode());
        boolean flag = propagandaMapper.exists(queryWrapper);
        Relation relation = new Relation(IdWorker.getId(), source, propaganda.getCode(), propaganda.getType());
        if (!flag) {
            relationMapper.insert(relation);
        }
        relationMapper.updateById(relation);

        stringRedisTemplate.opsForSet().add(ROBOT_CODE_PREFIX + source, propaganda.getType() + ":" + propaganda.getCode());
    }

    @Override
    public void delete(String id) {
        super.removeById(id);
    }
}
