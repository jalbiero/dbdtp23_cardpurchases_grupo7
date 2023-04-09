package com.tpdbd.cardpurchases.errors;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(String code) {
        super("Payment not found, code = %s", code);
    }
}
