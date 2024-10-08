package com.harera.hayat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FieldLimitException extends FieldException {

    public FieldLimitException(String code, String field, String value) {
        super(String.format("Invalid %s: %s Limit", field, value), code, field);
    }

    public FieldLimitException(String code, String field, String value, String limitFrom,
                    String limitTo) {
        super(String.format("Invalid %s: %s Limit Should be from %s to %s", field, value,
                        limitFrom, limitTo), code, field);
    }
}
