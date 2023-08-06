package com.tpdbd.cardpurchases.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankRepository extends CrudRepository<Bank, Long> {
    @Query("SELECT b.id FROM Bank b")
    List<Long> findAllIds();
}