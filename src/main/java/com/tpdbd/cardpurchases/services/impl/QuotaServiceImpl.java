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
        // For debugging purposes: Set a greater value and uncomment the 
        // logging code a few lines below
        final var NUM_OF_STORES = 1; 

        var bestSellers = this.quotaRepository.findTheBestSellerStores(year, month, PageRequest.of(0, NUM_OF_STORES));

        if (bestSellers.isEmpty())
            throw new BestSellerStoreNotFoundException(year, month);

        // TODO Add a propper logger instead of the console output
        // bestSellers.forEach(store -> {
        //     System.out.println(
        //         "Store: " + store.getStore() +
        //         ", cuit: " + store.cuitStore() + 
        //         ", monthly profit: $" + store.getMonthlyProfit());
        // });

        return bestSellers.getContent().get(0);
    }
}
