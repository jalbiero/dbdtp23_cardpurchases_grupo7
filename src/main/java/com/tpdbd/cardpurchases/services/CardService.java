package com.tpdbd.cardpurchases.services;

import java.time.LocalDate;
import java.util.List;

import com.tpdbd.cardpurchases.model.Card;

public interface CardService {
    List<Card> getSoonToExpire(LocalDate baseDate, Integer daysFromBaseDate);
}
