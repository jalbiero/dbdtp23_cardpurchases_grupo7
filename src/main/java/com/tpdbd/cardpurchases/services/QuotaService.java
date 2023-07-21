package com.tpdbd.cardpurchases.services;

import com.tpdbd.cardpurchases.repositories.projections.Store;

public interface QuotaService {
    Store getTheBestSellerStore(int year, int month);
}
