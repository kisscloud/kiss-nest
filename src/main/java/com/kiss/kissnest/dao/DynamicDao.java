package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Dynamic;

import java.util.List;

public interface DynamicDao {

    Integer createDynamic(Dynamic dynamic);

    List<Dynamic> getDynamics(Integer teamId, Integer start, Integer size, Integer groupId, Integer projectId);

    Integer getDynamicsCount(Dynamic dynamic);
}
