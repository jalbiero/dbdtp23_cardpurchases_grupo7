package com.tpdbd.cardpurchases.errors;

public class BankNotFoundException extends NotFoundException {
    public BankNotFoundException(String cuil) {
        super("Bank not found, cuil = %s", cuil);
    }
}
