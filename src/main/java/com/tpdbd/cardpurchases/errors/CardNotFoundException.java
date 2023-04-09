package com.tpdbd.cardpurchases.errors;

public class CardNotFoundException extends NotFoundException {
    public CardNotFoundException(String number) {
        super("Card not found, number = %s", number);
    }
}
