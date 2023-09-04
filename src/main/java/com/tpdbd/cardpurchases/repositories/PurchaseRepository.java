package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
    @Query(
        "SELECT " + 
        "   COUNT(p) AS numOfPurchases, " + 
        "   p.card AS card " + 
        "FROM Purchase p " + 
        "GROUP BY p.card " +
        "ORDER BY numOfPurchases DESC")
    Page<NumOfPurchasesByCard> findTopPurchaserCards(Pageable pageable);  
    
    @Query(
        "SELECT " + 
        "   COUNT(p) AS numOfPurchases, " + 
        "   p.paymentVoucher AS code " + 
        "FROM Purchase p " + 
        "WHERE p.paymentVoucher IS NOT NULL " +
        "GROUP BY p.paymentVoucher " +
        "ORDER BY numOfPurchases DESC")
    Page<MostUsedVoucher> findTheMostUsedVouchers(Pageable pageable);  
}
