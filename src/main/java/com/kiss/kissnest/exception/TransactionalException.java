package com.kiss.kissnest.exception;

import lombok.Data;

@Data
public class TransactionalException extends RuntimeException{

    private Integer code;

    public TransactionalException(Integer code) {
        this.code = code;
    }
}
