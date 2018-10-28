package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Server;

import java.util.List;

public interface ServerDao {

    Integer createServer(Server server);

    Integer deleteServerById(Integer id);

    Integer updateServer(Server server);

    Server getServerById(Integer id);

    List<Server> getServers();
}
