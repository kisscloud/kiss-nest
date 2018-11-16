package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.LinkDao;
import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import output.ResultOutput;

import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkDao linkDao;

    public ResultOutput createLink(@RequestBody Link link) {

        linkDao.createLink(link);

        return ResultOutputUtil.success(link);
    }

    public ResultOutput updateLink(@RequestBody Link link) {

        linkDao.updateLink(link);

        return ResultOutputUtil.success(link);
    }

    public ResultOutput getLinks(Integer teamId) {

        return ResultOutputUtil.success(linkDao.getLinks(teamId));
    }

    public ResultOutput deleteLink(Integer linkId) {

        linkDao.deleteLink(linkId);

        return ResultOutputUtil.success();
    }
}
