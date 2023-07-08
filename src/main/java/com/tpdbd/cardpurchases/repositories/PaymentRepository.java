package com.tpdbd.cardpurchases.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Optional<Payment> findByCode(String code);

    @Query("SELECT p.code FROM Payment p")
    List<String> findAllCodes();

    @Query(
        "SELECT p " + 
        "FROM Payment p " + 
        "WHERE p.card.number = :number AND p.year = :year AND p.month = :month")
    Optional<Payment> findMonthlyPayment(@Param("number") String cardNumber,
                                         @Param("year") int year, 
                                         @Param("month") int month);

    @Query(
        "SELECT " + 
        "   SUM(p.totalPrice) AS totalPaymentValue, " + 
        "   p.card.bank AS bank " + 
        "FROM Payment p " + 
        "GROUP BY p.card.bank " +
        "ORDER BY totalPaymentValue DESC")
    Page<MostEarnerBank> findTheMostEarnerBanks(Pageable pageable);  
}
