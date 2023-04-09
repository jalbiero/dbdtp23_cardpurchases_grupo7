package com.tpdbd.cardpurchases.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String format, Object... args) {
        super(String.format(format, args));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
