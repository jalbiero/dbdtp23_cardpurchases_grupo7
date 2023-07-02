package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tpdbd.cardpurchases.model.Quota;
import com.tpdbd.cardpurchases.repositories.projections.Store;

public interface QuotaRepository extends CrudRepository<Quota, Long> {
    @Query(
        "SELECT SUM(v.price) AS monthlyProfit, v.purchase.store AS store, v.purchase.cuitStore AS cuitStore " + 
        "FROM Quota v " + 
        "WHERE v.year = :year AND v.month = :month " +
        "GROUP BY v.purchase.store, v.purchase.cuitStore " +
        "ORDER BY SUM(v.price) DESC")
    Page<Store> findBestSellerStores(@Param("year") int year, @Param("month") int month, Pageable pageable);
}
