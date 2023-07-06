package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
    @Query("SELECT p.id FROM Purchase p")
    List<Long> findAllIds();

    @Query(
        "SELECT " + 
        "   COUNT(p) AS numOfPurchases, " + 
        "   p.card AS card " + 
        "FROM Purchase p " + 
        "GROUP BY p.card " +
        "ORDER BY COUNT(p) DESC")
    Page<NumOfPurchasesByCard> findTopPurchaserCards(Pageable pageable);    
}
