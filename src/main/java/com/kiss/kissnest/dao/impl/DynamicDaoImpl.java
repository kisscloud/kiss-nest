package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DynamicDao;
import com.kiss.kissnest.entity.Dynamic;
import com.kiss.kissnest.mapper.DynamicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DynamicDaoImpl implements DynamicDao {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public Integer createDynamic(Dynamic dynamic) {

        return dynamicMapper.createDynamic(dynamic);
    }

    @Override
    public List<Dynamic> getDynamics(Integer teamId, Integer start, Integer size, Integer groupId, Integer projectId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("start", start);
        params.put("size", size);
        params.put("groupId", groupId);
        params.put("projectId", projectId);
        return dynamicMapper.getDynamics(params);
    }

    @Override
    public Integer getDynamicsCount(Dynamic dynamic) {

        return dynamicMapper.getDynamicsCount(dynamic);
    }
}
