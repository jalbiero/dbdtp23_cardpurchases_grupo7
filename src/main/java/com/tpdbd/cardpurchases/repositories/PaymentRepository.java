package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
