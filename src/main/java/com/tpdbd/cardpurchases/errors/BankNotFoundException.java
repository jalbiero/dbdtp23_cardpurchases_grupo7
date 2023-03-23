package com.tpdbd.cardpurchases.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(String cuil) {
        super(String.format("Bank not found, cuil = %s", cuil));
    }
}
