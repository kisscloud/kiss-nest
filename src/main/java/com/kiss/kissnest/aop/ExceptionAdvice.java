package com.kiss.kissnest.aop;

import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import output.ResultOutput;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionAdvice {

    @Autowired
    private CodeUtil codeUtil;

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ResultOutput exceptionHandle(HttpServletRequest request, Exception e) {

        if (e instanceof TransactionalException) {

            return ResultOutputUtil.error(((TransactionalException) e).getCode());
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodException = ((MethodArgumentNotValidException) e);
            BindingResult bindingResult = methodException.getBindingResult();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            Map<String, List<String>> formVerifieds = new HashMap<>();
            String language = StringUtils.isEmpty(request.getHeader("X-LANGUAGE")) ? "zh-CN" : request.getHeader("X-LANGUAGE");

            if (fieldErrorList != null) {
                for (FieldError fieldError : fieldErrorList) {
                    String message = fieldError.getDefaultMessage();
                    String field = fieldError.getField();
                    List<String> messageList = new ArrayList<>();

                    if (formVerifieds.containsKey(field)) {
                        messageList = formVerifieds.get(field);
                    }

                    String returnMessage = codeUtil.getMessage(Integer.parseInt(message));
                    messageList.add(returnMessage == null ? message : returnMessage);
                    formVerifieds.put(field, messageList);
                }
            }

            return ResultOutputUtil.error(NestStatusCode.VALIDATE_ERROR, codeUtil.getMessage(NestStatusCode.VALIDATE_ERROR), formVerifieds);
        }

        return ResultOutputUtil.error(NestStatusCode.SERVICE_ERROR);
    }
}
