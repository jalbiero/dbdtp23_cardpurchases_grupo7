package com.tpdbd.cardpurchases.errors;

public class CreditPurchaseNotFoundException extends NotFoundException {
    public CreditPurchaseNotFoundException(String id) {
        super("Credit purchase not found, id = %s. The provided id might be a cash purchase", id);
    }
}
