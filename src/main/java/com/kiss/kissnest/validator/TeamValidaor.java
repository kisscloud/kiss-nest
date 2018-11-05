package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.input.CreateTeamInput;
import com.kiss.kissnest.input.UpdateTeamInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TeamValidaor implements Validator {

    @Autowired
    private TeamDao teamDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateTeamInput.class) ||
                clazz.equals(UpdateTeamInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateTeamInput.class.isInstance(target)) {
            CreateTeamInput createTeamInput = (CreateTeamInput) target;
            validateName(createTeamInput.getName(),errors);
        } else if (UpdateTeamInput.class.isInstance(target)) {
            UpdateTeamInput updateTeamInput = (UpdateTeamInput) target;
            validateId(updateTeamInput.getId(),"id",errors);
            validateName(updateTeamInput.getName(),errors);
        } else {
            errors.rejectValue("name", "", "数据绑定错误");
        }
    }

    public void validateName (String name,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name","","团队名字不能为空");
            return;
        }

        Team team = teamDao.getTeamByName(name);

        if (team != null) {
            errors.rejectValue("name","","团队名字已经存在");
        }
    }

    public boolean validateId (Integer id,String idName,Errors errors) {

        if (id == null) {
            errors.rejectValue(idName,"","团队id不能为空");
            return false;
        }

        Team team = teamDao.getTeamById(id);

        if (team == null) {
            errors.rejectValue(idName,"","团队id不存在");
            return false;
        }

        return true;
    }
}
