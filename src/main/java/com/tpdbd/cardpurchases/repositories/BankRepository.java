package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankRepository extends CrudRepository<Bank, Long> {
    Optional<Bank> findByCuit(String cuit);
}