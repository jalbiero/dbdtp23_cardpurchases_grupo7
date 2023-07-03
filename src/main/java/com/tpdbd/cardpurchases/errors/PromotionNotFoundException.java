package com.tpdbd.cardpurchases.errors;

public class PromotionNotFoundException extends NotFoundException {
    public PromotionNotFoundException(String code) {
        super("Promotion not found, code = %s", code);
    }
}
