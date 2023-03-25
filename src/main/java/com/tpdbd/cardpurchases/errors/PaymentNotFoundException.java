package com.tpdbd.cardpurchases.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String code) {
        super(String.format("Payment not found, code = %s", code));
    }
}
