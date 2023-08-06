package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tpdbd.cardpurchases.model.Quota;
import com.tpdbd.cardpurchases.repositories.projections.Store;

public interface QuotaRepository extends CrudRepository<Quota, Long> {
    @Query(
        "SELECT " + 
        "   SUM(q.price) AS monthlyProfit, " + 
        "   q.purchase.store AS store, " + 
        "   q.purchase.cuitStore AS cuitStore " + 
        "FROM Quota q " + 
        "WHERE q.year = :year AND q.month = :month " +
        "GROUP BY q.purchase.store, q.purchase.cuitStore " +
        "ORDER BY monthlyProfit DESC")
    Page<Store> findTheBestSellerStores(@Param("year") int year, @Param("month") int month, Pageable pageable);
}
