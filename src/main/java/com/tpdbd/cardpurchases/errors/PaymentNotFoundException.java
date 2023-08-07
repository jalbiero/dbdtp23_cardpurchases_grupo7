package com.tpdbd.cardpurchases.errors;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(String id) {
        super("Payment not found, code = %s", id);
    }
}
