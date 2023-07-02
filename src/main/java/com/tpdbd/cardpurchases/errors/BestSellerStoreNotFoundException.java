package com.tpdbd.cardpurchases.errors;

public class BestSellerStoreNotFoundException extends NotFoundException {
    public BestSellerStoreNotFoundException(int year, int month) {
        super("Best seller store not found for year = %d and month =%d", year, month);
    } 
}
