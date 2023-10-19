package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedPromotion;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, String> {
    @Aggregation({
        "{ $group: { _id : '$card._id', card: { '$first': '$card' }, numOfPurchases: { $sum: 1 } } }",
        "{ $sort : { numOfPurchases : -1 } }",
        "{ $project: { _id: 0 } }"
    })
    Slice<NumOfPurchasesByCard> findTopPurchaserCards(Pageable pageable);

    @Aggregation({
        "{ $match: { 'validPromotion': { $ne: null } } } ",
        "{ $group: { _id : '$validPromotion', validPromotion: { '$first': '$validPromotion' }, numOfPurchases: { $sum: 1 } } }",
        "{ $sort : { numOfPurchases : -1 } }",
        "{ $project: { _id: 0, 'promotion': $validPromotion, numOfPurchases: 1 } }"
    })
    Slice<MostUsedPromotion> findTheMostUsedPromotions(Pageable pageable);
}
