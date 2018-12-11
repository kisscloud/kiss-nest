package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.LinkDao;
import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import output.ResultOutput;
import utils.ThreadLocalUtil;

@Service
public class LinkService {

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private OperationLogService operationLogService;

    public ResultOutput createLink(@RequestBody Link link) {

        linkDao.createLink(link);
        operationLogService.saveOperationLog(link.getTeamId(),ThreadLocalUtil.getGuest(),null,link,"id",OperationTargetType.TYPE__CREATE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),link.getTeamId(),null,null,OperationTargetType.TYPE__CREATE_LINK,link);
        return ResultOutputUtil.success(link);
    }

    public ResultOutput updateLink(@RequestBody Link link) {

        Link old = linkDao.getLinkById(link.getId());
        linkDao.updateLink(link);
        operationLogService.saveOperationLog(link.getTeamId(),ThreadLocalUtil.getGuest(),old,link,"id",OperationTargetType.TYPE__UPDATE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),old.getTeamId(),null,null,OperationTargetType.TYPE__DELETE_LINK,old);
        return ResultOutputUtil.success(link);
    }

    public ResultOutput getLinks(Integer teamId) {

        return ResultOutputUtil.success(linkDao.getLinks(teamId));
    }

    public ResultOutput deleteLink(Integer linkId) {

        Link old = linkDao.getLinkById(linkId);
        linkDao.deleteLink(linkId);
        operationLogService.saveOperationLog(old.getTeamId(),ThreadLocalUtil.getGuest(),old,null,"id",OperationTargetType.TYPE__DELETE_LINK);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(),old.getTeamId(),null,null,OperationTargetType.TYPE__DELETE_LINK,old);
        return ResultOutputUtil.success();
    }
}
