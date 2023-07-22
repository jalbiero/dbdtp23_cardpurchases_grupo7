package com.tpdbd.cardpurchases.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.MonthlyPaymentNotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;
import com.tpdbd.cardpurchases.repositories.projections.MostEarnerBank;
import com.tpdbd.cardpurchases.services.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment find(Long id) {
        return this.paymentRepository
            .findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));    
    }

    @Override
    public List<Long> findAllIds() {
        return this.paymentRepository.findAllIds();
    }

    @Override
    public Payment findMonthlyPayment(long cardId, int year, int month)
    {
        return this.paymentRepository
            .findMonthlyPayment(cardId, year, month)
            .orElseThrow(() -> new MonthlyPaymentNotFoundException(cardId, year, month));
    }

    @Override
    public List<MostEarnerBank> findTheMostEarnerBanks(int count) {
        var mostEarnerBanks = this.paymentRepository
            .findTheMostEarnerBanks(PageRequest.of(0, count));

        return mostEarnerBanks.get().toList();
    }

    @Override
    public void save(Payment payment) {
        this.paymentRepository.save(payment);
    }
}
