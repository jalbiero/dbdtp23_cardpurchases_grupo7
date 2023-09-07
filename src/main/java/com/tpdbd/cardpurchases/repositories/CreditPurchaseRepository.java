package com.tpdbd.cardpurchases.repositories;

import com.tpdbd.cardpurchases.model.CreditPurchase;

/**
 * This class is necessary because declaring a repository variable as 'PurchaseRepository<CreditPurchase>'
 * is not enough to filter in by credit purchases. With this class definition, even a variable 
 * declared as 'PurchaseRepository<CreditPurchase>' will correctly filter in by Credit purchases.
 * A companion class CashPurchaseRepository is not necessary now, because there are no requirements for
 * cash purchase queries.
 */
public interface CreditPurchaseRepository extends PurchaseRepository<CreditPurchase> {
    
}
