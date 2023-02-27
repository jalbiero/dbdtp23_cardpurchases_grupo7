package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_card")
    private Long id;

    private String number;

    private String ccv;

    private String cardholderNameInCard;

    private LocalDate since;

    private LocalDate expirationDate;

    public Card(String number, String ccv, String cardholderNameInCard, LocalDate since, LocalDate expirationDate) {
        this.number = number;
        this.ccv = ccv;
        this.cardholderNameInCard = cardholderNameInCard;
        this.since = since;
        this.expirationDate = expirationDate;
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
}
