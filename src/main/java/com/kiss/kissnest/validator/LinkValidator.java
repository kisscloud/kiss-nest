package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.status.NestStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class LinkValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateLinkInput.class) ||
                clazz.equals(UpdateLinkInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateLinkInput.class.isInstance(target)) {
            CreateLinkInput createLinkInput = (CreateLinkInput) target;
            validateTeamId(createLinkInput.getTeamId(), errors);
            validateTitle(createLinkInput.getTitle(), errors);
            validateUrl(createLinkInput.getUrl(), errors);

        } else if (UpdateLinkInput.class.isInstance(target)) {
            UpdateLinkInput updateLinkInput = (UpdateLinkInput) target;
            validateTitle(updateLinkInput.getTitle(), errors);
            validateUrl(updateLinkInput.getUrl(), errors);
        }
    }

    public void validateTeamId(Integer teamId, Errors errors) {

        if (teamId == null) {
            errors.rejectValue("teamId", String.valueOf(NestStatusCode.TEAM_ID_IS_EMPTY), "团队ID不能为空");
            return;
        }

    }

    public void validateTitle(String title, Errors errors) {

        if (title.isEmpty()) {
            errors.rejectValue("title", String.valueOf(NestStatusCode.LINK_TITLE_IS_EMPTY), "标题不能为空");
            return;
        }

    }

    public void validateUrl(String url, Errors errors) {

        if (url.isEmpty()) {
            errors.rejectValue("url", String.valueOf(NestStatusCode.LINK_URL_IS_EMPTY), "链接不能为空");
        }
    }
}
