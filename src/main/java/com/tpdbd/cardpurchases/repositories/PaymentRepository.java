package com.tpdbd.cardpurchases.repositories;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentRepository extends CrudRepository<Payment, String> {
    Optional<Payment> findByCard_IdAndYearAndMonth(String id, int year, int month);

    @Aggregation({ 
        "{ $group: { _id : '$card.bank', bankId: { '$first': '$card.bank' }, totalPaymentValue: { $sum: $totalPrice } } }",
        "{ $sort : { totalPaymentValue : -1 } }",
        "{ $project: { _id: 0 } }"
    })
    Slice<MostEarnerBank> findTheMostEarnerBanks(Pageable pageable);  
}
