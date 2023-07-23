package com.tpdbd.cardpurchases.errors;

public class BankNotFoundException extends NotFoundException {
    public BankNotFoundException(long id) {
        super("Bank not found, id = %d", id);
    }
}
