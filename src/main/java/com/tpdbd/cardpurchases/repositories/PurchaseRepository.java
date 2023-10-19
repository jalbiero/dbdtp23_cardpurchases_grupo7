package com.tpdbd.cardpurchases.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tpdbd.cardpurchases.model.Purchase;
import com.tpdbd.cardpurchases.repositories.projections.MostUsedPromotion;
import com.tpdbd.cardpurchases.repositories.projections.NumOfPurchasesByCard;

public interface PurchaseRepository<T extends Purchase> extends CrudRepository<T, Long> {
    @Query(
        "SELECT " +
        "   COUNT(p) AS numOfPurchases, " +
        "   p.card AS card " +
        "FROM Purchase p " +
        "GROUP BY p.card " +
        "ORDER BY numOfPurchases DESC")
    Page<NumOfPurchasesByCard> findTopPurchaserCards(Pageable pageable);

    // TODO This method will be moved to PromotionRepository when the missing
    //      relationship Promotion <-1--n-> Purchase will be added
    @Query(
        "SELECT " +
        "   COUNT(p) AS numOfPurchases, " +
        "   p.validPromotion AS promotion " +
        "FROM Purchase p " +
        "WHERE p.validPromotion IS NOT NULL " +
        "GROUP BY p.validPromotion " +
        "ORDER BY numOfPurchases DESC")
    Page<MostUsedPromotion> findTheMostUsedPromotions(Pageable pageable);
}
