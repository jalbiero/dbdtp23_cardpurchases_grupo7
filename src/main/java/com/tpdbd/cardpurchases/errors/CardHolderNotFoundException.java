package com.tpdbd.cardpurchases.errors;

public class CardHolderNotFoundException extends NotFoundException {
    public CardHolderNotFoundException(String dni) {
        super("Card holder not found, dni = %s", dni);
    }
}
