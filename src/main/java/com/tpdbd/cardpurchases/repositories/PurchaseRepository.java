package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
}
