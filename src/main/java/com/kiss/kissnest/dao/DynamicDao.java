package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Dynamic;

import java.util.List;

public interface DynamicDao {

    Integer createDynamic(Dynamic dynamic);

    List<Dynamic> getDynamics(Dynamic dynamic);
}
