package com.kiss.kissnest.config;

import com.alibaba.fastjson.JSON;
import com.kiss.kissnest.util.LangUtil;
import feign.FeignException;
import com.kiss.foundation.exception.StatusException;
import com.kiss.foundation.status.BaseStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionAdvice {

    @Autowired
    private LangUtil langUtil;

    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public StatusException exceptionHandle(HttpServletRequest request, HttpServletResponse response, Exception e) {

        if (e instanceof StatusException) {

            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());

            return (StatusException) e;

        } else if (e instanceof FeignException) {

            Integer status = ((FeignException) e).status();

            if (status.equals(HttpStatus.UNPROCESSABLE_ENTITY.value())) {
                response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
                String message = e.getMessage().replaceAll("status 422 .+ content:", "");
                return JSON.parseObject(message, StatusException.class);
            }

        } else if (e instanceof MethodArgumentNotValidException) {

            MethodArgumentNotValidException methodException = ((MethodArgumentNotValidException) e);
            BindingResult bindingResult = methodException.getBindingResult();
            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            Map<String, List<String>> formFields = new HashMap<>();

            for (FieldError fieldError : fieldErrorList) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                List<String> messageList = new ArrayList<>();

                if (formFields.containsKey(field)) {
                    messageList = formFields.get(field);
                }

                String returnMessage = langUtil.getCodeMessage(Integer.parseInt(code));
                messageList.add(returnMessage == null ? code : returnMessage);
                formFields.put(field, messageList);
            }

            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());

            return new StatusException(BaseStatusCode.VALIDATE_ERROR, formFields);
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new StatusException(BaseStatusCode.SERVICE_ERROR, e.getMessage());
    }
}
