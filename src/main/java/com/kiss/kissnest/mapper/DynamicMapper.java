package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Dynamic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DynamicMapper {

    Integer createDynamic(Dynamic dynamic);

    List<Dynamic> getDynamics(Dynamic dynamic);
}
