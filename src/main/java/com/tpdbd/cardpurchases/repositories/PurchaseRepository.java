package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
    @Query("SELECT p.id FROM Purchase p")
    List<Long> findAllIds();
}
