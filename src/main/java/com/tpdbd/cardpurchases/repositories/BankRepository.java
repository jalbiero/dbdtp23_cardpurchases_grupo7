package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Bank;

public interface BankRepository extends CrudRepository<Bank, Long>  
{  
}  