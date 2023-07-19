package com.tpdbd.cardpurchases.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;

public interface PaymentService {
    Payment find(String code);

    List<String> findAllCodes();

    Payment findMonthlyPayment(String cardNumber, int year, int month);

    Stream<MostEarnerBank> findTheMostEarnerBanks(int count);  

    void save(Payment payment);
}
