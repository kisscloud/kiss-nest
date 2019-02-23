package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.output.ServerOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServerMapper {

    Integer createServer(Server server);

    Integer deleteServerById(Integer id);

    Integer updateServer(Server server);

    Server getServerById(Integer id);

    List<Server> getServers();

    Server getServerByNameAndTeamId(Map params);

    List<Server> getServersByTeamId(Map params);

    List<String> getServerInnerIpsByIds(String ids);

    List<ServerOutput> getServerOutputsByTeamId(Map params);

    Integer getServerOutputCount(Map params);

    List<Server> getServersByEnvId(Integer envId);

    List<Server> getMonitorServers(Integer teamId);

    String getServerIpsByIds(List<Integer> ids);

    Server getServerByEnvIdAndInnerIp(@Param("envId") Integer envId, @Param("innerIp") String innerIp);
}
