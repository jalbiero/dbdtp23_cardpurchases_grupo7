package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tpdbd.cardpurchases.model.CreditPurchase;

public interface CreditPurchaseRepository extends PurchaseRepository<CreditPurchase> {
    // Note: The following queries (actually the WHERE clause) depends on the type 
    //       of the inheritance strategy defined in Purchase class. In this particular 
    //       case the filter by type is adjusted to 'strategy = InheritanceType.SINGLE_TABLE'

    @Query("SELECT p FROM Purchase p WHERE TYPE(p) = 'CreditPurchase'")
    Iterable<CreditPurchase> findAll();

    @Query("SELECT p FROM Purchase p WHERE TYPE(p) = 'CreditPurchase' AND p.id = :id")
    Optional<CreditPurchase> findById(@Param("id") long id);
}
