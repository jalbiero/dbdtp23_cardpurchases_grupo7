package com.tpdbd.cardpurchases.errors;

public class PurchaseNotFoundException extends NotFoundException {
    public PurchaseNotFoundException(String id) {
        super("Purchase not found, id = %s", id);
    }
}
