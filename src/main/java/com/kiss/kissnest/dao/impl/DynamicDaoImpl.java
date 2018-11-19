package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DynamicDao;
import com.kiss.kissnest.entity.Dynamic;
import com.kiss.kissnest.mapper.DynamicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DynamicDaoImpl implements DynamicDao {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public Integer createDynamic(Dynamic dynamic) {

        return dynamicMapper.createDynamic(dynamic);
    }

    @Override
    public List<Dynamic> getDynamics(Dynamic dynamic) {

        return dynamicMapper.getDynamics(dynamic);
    }
}
