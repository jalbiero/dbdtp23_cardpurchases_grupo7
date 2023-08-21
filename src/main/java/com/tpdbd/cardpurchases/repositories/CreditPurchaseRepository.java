package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.Query;

import com.tpdbd.cardpurchases.model.CreditPurchase;

public interface CreditPurchaseRepository extends PurchaseRepository<CreditPurchase> {
    @Query("{ _class: 'CreditPurchase' }")
    Iterable<CreditPurchase> findAll();

    @Query("{ _id: ?0, _class: 'CreditPurchase' }")
    Optional<CreditPurchase> findById(String id);
}
