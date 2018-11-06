package com.kiss.kissnest.validator;

import com.kiss.account.input.ValidateAccountInput;
import com.kiss.kissnest.feign.AccountServiceFeign;
import com.kiss.kissnest.feign.ClientServiceFeign;
import com.kiss.kissnest.input.CreateMemberAccessInput;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import output.ResultOutput;
import utils.ThreadLocalUtil;

@Component
public class MemberValidator implements Validator {

    @Autowired
    private AccountServiceFeign accountServiceFeign;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateMemberAccessInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateMemberAccessInput.class.isInstance(target)) {
            CreateMemberAccessInput createMemberAccessInput = (CreateMemberAccessInput) target;
            validatePassword(createMemberAccessInput.getPassword(),errors);
        } else {
            errors.rejectValue("password","","数据绑定错误");
        }
    }

    public void validatePassword (String password,Errors errors) {

        Guest guest = ThreadLocalUtil.getGuest();
        ValidateAccountInput validateAccountInput = new ValidateAccountInput();
        validateAccountInput.setId(guest.getId());
        validateAccountInput.setPassword(password);
        ResultOutput resultOutput = accountServiceFeign.validateAccount(validateAccountInput);
        if (resultOutput.getCode() != 200) {
            errors.rejectValue("password","","密码错误");
        }
    }
}
