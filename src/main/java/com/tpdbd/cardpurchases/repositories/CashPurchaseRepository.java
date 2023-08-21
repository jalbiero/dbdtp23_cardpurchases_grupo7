package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.mongodb.repository.Query;

import com.tpdbd.cardpurchases.model.CashPurchase;

public interface CashPurchaseRepository extends PurchaseRepository<CashPurchase> {
    @Query("{ _class: 'CashPurchase' }")
    Iterable<CashPurchase> findAll();
}
