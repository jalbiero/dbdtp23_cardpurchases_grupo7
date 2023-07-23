package com.tpdbd.cardpurchases.errors;

public class MonthlyPaymentNotFoundException extends NotFoundException {
    public MonthlyPaymentNotFoundException(long cardId, int year, int month) {
        super(
            "Monthly Payment not found for card id = %d, year = %d, month = %d", 
            cardId, year, month);
    }
}
