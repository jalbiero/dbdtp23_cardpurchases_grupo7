package com.tpdbd.cardpurchases.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.tpdbd.cardpurchases.repositories.projections.Store;
import com.tpdbd.cardpurchases.errors.BestSellerStoreNotFoundException;
import com.tpdbd.cardpurchases.repositories.QuotaRepository;
import com.tpdbd.cardpurchases.services.QuotaService;

@Service
public class QuotaServiceImpl implements QuotaService {
    @Autowired
    private QuotaRepository quotaRepository;

    @Override
    public Store getTheBestSellerStore(int year, int month) {
        var bestSellers = this.quotaRepository.findTheBestSellerStores(year, month, PageRequest.of(0, 1));

        if (bestSellers.isEmpty())
            throw new BestSellerStoreNotFoundException(year, month);

        return bestSellers.getContent().get(0);
    }
}
