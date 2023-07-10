package com.tpdbd.cardpurchases.errors;

public class CreditPurchaseNotFoundException extends NotFoundException {
    public CreditPurchaseNotFoundException(long id) {
        super("Credit purchase not found, id = %d. The provided id might be a cash purchase", id);
    }
}
