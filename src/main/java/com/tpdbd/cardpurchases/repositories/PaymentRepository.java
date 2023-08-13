package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentRepository extends CrudRepository<Payment, String> {
    Optional<Payment> findByCard_IdAndYearAndMonth(String id, int year, int month);

    @Query(
        "SELECT " + 
        "   SUM(p.totalPrice) AS totalPaymentValue, " + 
        "   p.card.bank AS bank " + 
        "FROM Payment p " + 
        "GROUP BY p.card.bank " +
        "ORDER BY totalPaymentValue DESC")
    Page<MostEarnerBank> findTheMostEarnerBanks(Pageable pageable);  
}
