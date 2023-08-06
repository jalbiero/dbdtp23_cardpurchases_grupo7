package com.tpdbd.cardpurchases.errors;

public class BankNotFoundException extends NotFoundException {
    public BankNotFoundException(String id) {
        super("Bank not found, id = %s", id);
    }
}
