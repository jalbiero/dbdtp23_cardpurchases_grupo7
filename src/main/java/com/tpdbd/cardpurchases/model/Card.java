package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class Card {
    @Id
    private String id;

    @DocumentReference
    private Bank bank;

    @DocumentReference
    private CardHolder cardHolder;

    @Indexed(unique = true)
    private String number;

    private String ccv;

    private String cardholderNameInCard;

    private LocalDate since;

    private LocalDate expirationDate;

    @DocumentReference(lazy = true)
    private List<Purchase> purchases;

    public Card() {
    }

    public Card(
        Bank bank,
        CardHolder cardHolder,
        String number,
        String ccv,
        LocalDate since,
        LocalDate expirationDate)
    {
        this.bank = bank;
        this.cardHolder = cardHolder;
        this.number = number;
        this.ccv = ccv;
        this.cardholderNameInCard = cardHolder.getCompleteName();
        this.since = since;
        this.expirationDate = expirationDate;
        this.purchases = new ArrayList<Purchase>();
    }

    public String getId() {
        return this.id;
    }

    public Bank getBank() {
        return this.bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public CardHolder getCardHolder() {
        return this.cardHolder;
    }

    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
        this.cardholderNameInCard = cardHolder.getCompleteName();
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCcv() {
        return this.ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getCardholderNameInCard() {
        return this.cardholderNameInCard;
    }

    public void setCardholderNameInCard(String cardholderNameInCard) {
        this.cardholderNameInCard = cardholderNameInCard;
    }

    public LocalDate getSince() {
        return this.since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }

    public LocalDate getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<Purchase> getPurchases() {
        return this.purchases;
    }
}
