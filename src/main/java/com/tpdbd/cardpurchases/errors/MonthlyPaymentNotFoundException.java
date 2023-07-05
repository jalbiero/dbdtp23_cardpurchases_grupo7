package com.tpdbd.cardpurchases.errors;

public class MonthlyPaymentNotFoundException extends NotFoundException {
    public MonthlyPaymentNotFoundException(String cardNumber, int year, int month) {
        super(
            "Monthly Payment not found for card number = %s, year = %d, month = %d", 
            cardNumber, year, month);
    }
}
