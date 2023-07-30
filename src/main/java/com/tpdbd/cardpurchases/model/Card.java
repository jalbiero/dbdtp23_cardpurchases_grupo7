package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity 
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Bank bank;

    @ManyToOne 
    @JoinColumn(nullable = false)
    private CardHolder cardHolder;

    @Column(unique = true, length = 20, nullable = false)
    private String number;

    @Column(length = 3, nullable = false)
    private String ccv;

    @Column(length = 50, nullable = false)
    private String cardholderNameInCard;

    @Column(nullable = false)
    private LocalDate since;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name="purchase",
        joinColumns=@JoinColumn(name="card_id"),
        inverseJoinColumns=@JoinColumn(name="id"))
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
    }

    public Long getId() {
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
