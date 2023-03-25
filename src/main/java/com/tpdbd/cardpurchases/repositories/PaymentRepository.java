package com.tpdbd.cardpurchases.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByCode(String code);

    @Query("SELECT u.code FROM Payment u")
    List<String> findAllCodes();
}
