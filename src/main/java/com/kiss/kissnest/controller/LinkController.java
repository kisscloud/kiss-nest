package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.input.CreateLinkInput;
import com.kiss.kissnest.input.UpdateLinkInput;
import com.kiss.kissnest.service.LinkService;
import com.kiss.kissnest.validator.LinkValidator;
import entity.Guest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;
import utils.ThreadLocalUtil;

@RestController
@Api(tags = "Link", description = "团队超链接")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private LinkValidator linkValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(linkValidator);
    }

    @PostMapping("/link")
    @ApiOperation(value = "添加链表")
    public ResultOutput createLink(@Validated @RequestBody CreateLinkInput createLinkInput) {

        Guest guest = ThreadLocalUtil.getGuest();

        Link link = new Link();
        link.setTeamId(createLinkInput.getTeamId());
        link.setTitle(createLinkInput.getTitle());
        link.setUrl(createLinkInput.getUrl());
        link.setOperatorId(guest.getId());
        link.setOperatorName(guest.getName());

        return linkService.createLink(link);
    }

    @PutMapping("/link")
    @ApiOperation(value = "更新链接")
    public ResultOutput updateLink(@Validated @RequestBody UpdateLinkInput updateLinkInput) {

        Guest guest = ThreadLocalUtil.getGuest();

        Link link = new Link();
        link.setId(updateLinkInput.getId());
        link.setTitle(updateLinkInput.getTitle());
        link.setUrl(updateLinkInput.getUrl());
        link.setOperatorId(guest.getId());
        link.setOperatorName(guest.getName());

        return linkService.updateLink(link);
    }

    @GetMapping("/links")
    @ApiOperation(value = "获取链接列表")
    public ResultOutput getLinks(Integer teamId) {
        return linkService.getLinks(teamId);
    }

    @DeleteMapping("/link")
    @ApiOperation(value = "删除链接")
    public ResultOutput deleteLink(Integer id) {
        return linkService.deleteLink(id);
    }

}
