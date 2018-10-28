package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.mapper.ServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerDaoImpl implements ServerDao {

    @Autowired
    private ServerMapper serverMapper;

    @Override
    public Integer createServer(Server server) {

        return serverMapper.createServer(server);
    }

    @Override
    public Integer deleteServerById(Integer id) {

        return serverMapper.deleteServerById(id);
    }

    @Override
    public Integer updateServer(Server server) {

        return serverMapper.updateServer(server);
    }

    @Override
    public Server getServerById(Integer id) {

        return serverMapper.getServerById(id);
    }

    @Override
    public List<Server> getServers() {

        return serverMapper.getServers();
    }
}
