package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.input.CreateMemberAccessInput;
import com.kiss.kissnest.output.MemberOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.ThreadLocalUtil;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    public ResultOutput createMember(Member member) {

        Member exist = memberDao.getMemberByAccountId(member.getAccountId());

        if (exist != null ) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_NAME_EXIST);
        }

        Integer count = memberDao.createMember(member);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_MEMBER_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(member,MemberOutput.class));
    }

    public ResultOutput deleteMember(Integer id) {

        Member member = memberDao.getMemberById(id);

        if (member == null) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_NOT_EXIST);
        }

        Integer count = memberDao.deleteMemberById(id);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.DELETE_MEMBER_FAILED);
        }

        return ResultOutputUtil.success();
    }

    public ResultOutput updateMember(Member member) {

        Member exist = memberDao.getMemberById(member.getId());

        if (exist == null) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_NOT_EXIST);
        }

        Integer count = memberDao.updateMember(member);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_MEMBER_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(member,MemberOutput.class));
    }

    public ResultOutput getMemberById(Integer id) {

        Member member = memberDao.getMemberById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(member,MemberOutput.class));
    }

    public ResultOutput getMemberAccess (CreateMemberAccessInput createMemberAccessInput) {

        try {
            String token = gitlabApiUtil.getAccessToken(createMemberAccessInput.getPassword());

            if (StringUtils.isEmpty(token)) {
                return ResultOutputUtil.error(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
            }

            Guest guest = ThreadLocalUtil.getGuest();

            Member member = memberDao.getMemberByAccountId(guest.getId());


            if (member != null) {
                Integer count = memberDao.updateAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId(),token);

                if (count == 0) {
                    return ResultOutputUtil.error(NestStatusCode.UPDATE_MEMBER_ACCESS_FAILED);
                }

            } else {
                member = new Member();
                member.setAccountId(guest.getId());
                member.setOperatorId(guest.getId());
                member.setOperatorName(guest.getName());
                member.setAccessToken(token);
                Integer count = memberDao.createMember(member);

                if (count == 0) {
                    return ResultOutputUtil.error(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
                }
            }

            return ResultOutputUtil.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultOutputUtil.error(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
        }
    }
}
