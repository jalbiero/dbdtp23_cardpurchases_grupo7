package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
    List<T> findByCardNumber(String cardNumber);
    List<T> findByCardNumberAndCuitStore(String cardNumber, String cuitStore);
}
