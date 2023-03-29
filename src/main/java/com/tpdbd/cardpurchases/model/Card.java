package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bank bank;

    @ManyToOne(fetch = FetchType.LAZY)
    private CardHolder cardHolder;

    private String number;

    private String ccv;

    private String cardholderNameInCard;

    private LocalDate since;

    private LocalDate expirationDate;

    public Card() {
    }

    public Card(
    // @formatter:off        
        Bank bank,
        CardHolder cardHolder, 
        String number, 
        String ccv, 
        LocalDate since,
        LocalDate expirationDate) 
    // @formatter:on
    {
        this.bank = bank;
        this.cardHolder = cardHolder;
        this.number = number;
        this.ccv = ccv;
        this.cardholderNameInCard = cardHolder.getCompleteName();
        this.since = since;
        this.expirationDate = expirationDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public CardHolder getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(CardHolder cardHolder) {
        this.cardHolder = cardHolder;
        this.cardholderNameInCard = cardHolder.getCompleteName();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getCardholderNameInCard() {
        return cardholderNameInCard;
    }

    public void setCardholderNameInCard(String cardholderNameInCard) {
        this.cardholderNameInCard = cardholderNameInCard;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "Card [id=" + id + ", bank=" + bank + ", cardHolder=" + cardHolder + ", number=" + number + ", ccv="
                + ccv + ", cardholderNameInCard=" + cardholderNameInCard + ", since=" + since + ", expirationDate="
                + expirationDate + "]";
    }
}
