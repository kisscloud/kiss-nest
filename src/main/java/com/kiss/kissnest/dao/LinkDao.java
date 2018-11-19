package com.kiss.kissnest.dao;


import com.kiss.kissnest.entity.Link;
import java.util.List;

public interface LinkDao {

    Integer createLink(Link link);

    Integer updateLink(Link link);

    List<Link> getLinks(Integer teamId);

    Integer deleteLink(Integer linkId);

    Link getLinkById(Integer id);
}
