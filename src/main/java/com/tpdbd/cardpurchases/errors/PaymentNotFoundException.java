package com.tpdbd.cardpurchases.errors;

public class PaymentNotFoundException extends NotFoundException {
    public PaymentNotFoundException(Long id) {
        super("Payment not found, code = %d", id);
    }
}
