package com.tpdbd.cardpurchases.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.errors.BankNotFoundException;
import com.tpdbd.cardpurchases.errors.PaymentNotFoundException;
import com.tpdbd.cardpurchases.model.Bank;
import com.tpdbd.cardpurchases.model.Payment;
import com.tpdbd.cardpurchases.repositories.BankRepository;
import com.tpdbd.cardpurchases.repositories.PaymentRepository;


@Service
public class TestServiceImpl implements TestService {
    @Autowired
    BankRepository bankRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public List<String> getBankCuits() {
        return this.bankRepository.findAllCuits();
    }

    @Override
    public Bank getBank(String cuit) {
        return this.bankRepository
                .findByCuit(cuit)
                .orElseThrow(() -> new BankNotFoundException(cuit));
    }

    @Override
    public List<String> getPaymentCodes() {
        return this.paymentRepository.findAllCodes();
    }

    @Override
    public Payment getPayment(String code) {
        return this.paymentRepository
                .findByCode(code)
                .orElseThrow(() -> new PaymentNotFoundException(code));
    }
}