package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.output.ServerOutput;
import io.swagger.models.auth.In;

import java.util.List;

public interface ServerDao {

    Integer createServer(Server server);

    Integer deleteServerById(Integer id);

    Integer updateServer(Server server);

    Server getServerById(Integer id);

    List<Server> getServers();

    Server getServerByNameAndTeamId(Integer teamId,String name);

    List<Server> getServersByTeamId(Integer teamId,Integer start,Integer size,Integer envId);

    List<String> getServerInnerIpsByIds(String ids);

    List<ServerOutput> getServerOutputsByTeamId(Integer teamId,Integer start,Integer size,Integer envId);
}
