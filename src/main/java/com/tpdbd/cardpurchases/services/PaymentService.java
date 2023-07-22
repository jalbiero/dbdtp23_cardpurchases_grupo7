package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentService {
    Payment find(Long id);

    List<Long> findAllIds();

    Payment findMonthlyPayment(long id, int year, int month);

    List<MostEarnerBank> findTheMostEarnerBanks(int count);  

    void save(Payment payment);
}
