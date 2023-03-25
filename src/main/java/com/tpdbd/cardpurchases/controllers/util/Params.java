package com.tpdbd.cardpurchases.controllers.util;

import java.time.LocalDate;

/**
 * Provides records for modelling controller parameters (when classes from 
 * 'model' are not enough)
 */
public interface Params {

    record PaymentDates(LocalDate firstExpiration, LocalDate secondExpiration) {}
    
}
