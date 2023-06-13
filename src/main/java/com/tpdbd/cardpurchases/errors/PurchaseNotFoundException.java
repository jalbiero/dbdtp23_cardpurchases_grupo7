package com.tpdbd.cardpurchases.errors;

public class PurchaseNotFoundException extends NotFoundException {
    public PurchaseNotFoundException(long id) {
        super("Purchase not found, id = %d", id);
    }
}
