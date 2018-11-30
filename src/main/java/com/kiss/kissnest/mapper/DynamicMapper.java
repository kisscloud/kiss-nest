package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Dynamic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DynamicMapper {

    Integer createDynamic(Dynamic dynamic);

    List<Dynamic> getDynamics(Map params);

    Integer getDynamicsCount(Dynamic dynamic);
}
