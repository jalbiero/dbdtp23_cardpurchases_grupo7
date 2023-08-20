package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Quota;
import com.tpdbd.cardpurchases.repositories.projections.Store;

public interface QuotaRepository extends CrudRepository<Quota, String> {
    @Aggregation({ 
        "{ $match: { $and: [ { 'year': ?0 }, { 'month': ?1 } ] } }",
        "{ $group: { _id : '$purchase.cuitStore', " + 
        "            store: { '$first': '$purchase.store' }, " +
        "            cuitStore: { '$first': '$purchase.cuitStore' }, " + 
        "            monthlyProfit: { $sum: $price } } }",
        "{ $sort : { monthlyProfit : -1 } }",
        "{ $project: { _id: 0 } }"        
    })
    Slice<Store> findTheBestSellerStores(int year, int month, Pageable pageable);
}
