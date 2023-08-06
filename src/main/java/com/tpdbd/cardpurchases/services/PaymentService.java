package com.tpdbd.cardpurchases.services;

import java.util.List;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentService {
    Payment find(String id);

    List<String> findAllIds();

    Payment findMonthlyPayment(String cardId, int year, int month);

    List<MostEarnerBank> findTheMostEarnerBanks(int count);  

    void save(Payment payment);
}
