package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedVoucher;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, String> {
    @Query("{ _class: ?0 }")
    Iterable<T> findAllByType(String typeName);

    @Aggregation({ 
        "{ $group: { _id : '$card._id', card: { '$first': '$card' }, numOfPurchases: { $sum: 1 } } }",
        "{ $sort : { numOfPurchases : -1 } }",
        "{ $project: { _id: 0 } }"
    })
    Slice<NumOfPurchasesByCard> findTopPurchaserCards(Pageable pageable);  

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
