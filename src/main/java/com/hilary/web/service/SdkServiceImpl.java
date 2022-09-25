package com.hilary.web.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
        String propId = IdWorker.getIdStr();
        propaganda.setId(propId);
        Robot robot = ThreadLocalUtils.getrobot();
        String source = robot.getCode();
        propaganda.setTime(new Date());
        Propaganda prop = propagandaMapper.selectOne(Wrappers.lambdaQuery(Propaganda.class)
                .eq(Propaganda::getCode, propaganda.getCode())
                .eq(Propaganda::getContext, propaganda.getContext()));
        if (EmptyUtil.isNullOrEmpty(prop)) {
            propagandaMapper.insert(propaganda);
        }else {
            //修改
            prop.setImage(propaganda.getImage());
            prop.setSend(propaganda.isSend());
            prop.setType(propaganda.getType());
            prop.setTime(new Date());
            propagandaMapper.updateById(prop);
        }
        //处理关联表
        LambdaQueryWrapper<Relation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Relation::getTargetCode, propaganda.getCode()).eq(Relation::getSourceCode, source);
        Relation relation = relationMapper.selectOne(queryWrapper);
        if (EmptyUtil.isNullOrEmpty(relation)) {
            relation = new Relation();
            relation.setId(IdWorker.getIdStr());
            relation.setSourceCode(source);
            relation.setTargetCode(propaganda.getCode());
            relation.setType(propaganda.getType());
            relationMapper.insert(relation);
        } else {
            relation.setType(propaganda.getType());
            relationMapper.updateById(relation);
        }
        stringRedisTemplate.opsForSet().add(ROBOT_CODE_PREFIX + source, propId);
    }

    @Override
    public void delete(String id) {
        super.removeById(id);
    }
}
