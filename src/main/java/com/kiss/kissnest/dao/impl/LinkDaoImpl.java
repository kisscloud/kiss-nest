package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.LinkDao;
import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.mapper.LinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkDaoImpl implements LinkDao {

    @Autowired
    private LinkMapper linkMapper;

    @Override
    public Integer createLink(Link link) {

        return linkMapper.createLink(link);
    }

    @Override
    public Integer updateLink(Link link) {
        return linkMapper.updateLink(link);
    }

    @Override
    public List<Link> getLinks(Integer teamId) {
        return linkMapper.getLinks(teamId);
    }

    @Override
    public Integer deleteLink(Integer linkId) {

        return linkMapper.deleteLink(linkId);
    }
}
