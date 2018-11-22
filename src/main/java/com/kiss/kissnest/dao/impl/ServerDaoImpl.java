package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.mapper.ServerMapper;
import com.kiss.kissnest.output.ServerOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Server getServerByNameAndTeamId(Integer teamId, String name) {

        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("teamId",teamId);

        return serverMapper.getServerByNameAndTeamId(params);
    }

    @Override
    public List<Server> getServersByTeamId(Integer teamId, Integer start, Integer size,Integer envId) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("start",start);
        params.put("size",size);
        params.put("envId",envId);

        return serverMapper.getServersByTeamId(params);
    }

    @Override
    public List<String> getServerInnerIpsByIds(String ids) {

        return serverMapper.getServerInnerIpsByIds(ids);
    }

    @Override
    public List<ServerOutput> getServerOutputsByTeamId(Integer teamId, Integer start, Integer size, Integer envId) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("start",start);
        params.put("size",size);
        params.put("envId",envId);

        return serverMapper.getServerOutputsByTeamId(params);
    }

    @Override
    public Integer getServerOutputCount(Integer teamId, Integer envId) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("envId",envId);

        return serverMapper.getServerOutputCount(params);
    }

    @Override
    public List<Server> getServersByEnvId(Integer envId) {

        return serverMapper.getServersByEnvId(envId);
    }

    @Override
    public String getServerIpsByIds(String ids) {

        return serverMapper.getServerIpsByIds(ids);
    }
}
