package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.LinkDao;
import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.enums.OperationTargetType;
import kiss.foundation.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private OperationLogService operationLogService;

    public Link createLink(@RequestBody Link link) {

        linkDao.createLink(link);
        operationLogService.saveOperationLog(link.getTeamId(),ThreadLocalUtil.getGuest(),null,link,"id",OperationTargetType.TYPE__CREATE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),link.getTeamId(),null,null,OperationTargetType.TYPE__CREATE_LINK,link);
        return link;
    }

    public Link updateLink(@RequestBody Link link) {

        Link old = linkDao.getLinkById(link.getId());
        linkDao.updateLink(link);
        operationLogService.saveOperationLog(link.getTeamId(),ThreadLocalUtil.getGuest(),old,link,"id",OperationTargetType.TYPE__UPDATE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),old.getTeamId(),null,null,OperationTargetType.TYPE__DELETE_LINK,old);
        return link;
    }

    public List<Link> getLinks(Integer teamId) {

        return linkDao.getLinks(teamId);
    }

    public void deleteLink(Integer linkId) {

        Link old = linkDao.getLinkById(linkId);
        linkDao.deleteLink(linkId);
        operationLogService.saveOperationLog(old.getTeamId(),ThreadLocalUtil.getGuest(),old,null,"id",OperationTargetType.TYPE__DELETE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),old.getTeamId(),null,null,OperationTargetType.TYPE__DELETE_LINK,old);
    }
}
