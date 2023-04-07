package com.tpdbd.cardpurchases.errors;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CardHolderNotFoundException extends RuntimeException {
    public CardHolderNotFoundException(String dni) {
        super(String.format("Card holder not found, dni = %s", dni));
    }
}