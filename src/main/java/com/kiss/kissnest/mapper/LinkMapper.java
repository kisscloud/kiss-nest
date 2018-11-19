package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Link;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LinkMapper {

    Integer createLink(Link link);

    Integer updateLink(Link link);

    List<Link> getLinks(Integer teamId);

    Integer deleteLink(Integer linkId);

    Link getLinkById(Integer id);
}
