package com.tpdbd.cardpurchases.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankRepository extends CrudRepository<Bank, Long> {
    Optional<Bank> findByCuit(String cuit);

    @Query("SELECT b.cuit FROM Bank b")
    List<String> findAllCuits();
}