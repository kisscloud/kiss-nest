package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Server;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ServerMapper {

    Integer createServer(Server server);

    Integer deleteServerById(Integer id);

    Integer updateServer(Server server);

    Server getServerById(Integer id);

    List<Server> getServers();
}
