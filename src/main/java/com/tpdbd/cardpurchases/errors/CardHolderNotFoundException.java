package com.tpdbd.cardpurchases.errors;

public class CardHolderNotFoundException extends NotFoundException {
    public CardHolderNotFoundException(long id) {
        super("Card holder not found, id = %d", id);
    }
}
