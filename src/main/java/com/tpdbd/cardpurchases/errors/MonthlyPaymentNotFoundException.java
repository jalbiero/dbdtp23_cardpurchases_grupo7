package com.tpdbd.cardpurchases.errors;

public class MonthlyPaymentNotFoundException extends NotFoundException {
    public MonthlyPaymentNotFoundException(String cardId, int year, int month) {
        super(
            "Monthly Payment not found for card id = %s, year = %d, month = %d", 
            cardId, year, month);
    }
}
