package com.tpdbd.cardpurchases.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Bank {
    @Id
    @GeneratedValue
    @Column(name = "bank_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(unique = true, length = 20, nullable = false)
    private String cuit;

    @Column(length = 200, nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String telephone;

    @OneToMany(mappedBy = "bank")
    private List<Promotion> promotions;

    @OneToMany(mappedBy = "bank")
    private List<Card> cards;

    @ManyToMany(mappedBy = "banks")
    private List<CardHolder> cardHolders;

    public Bank() {
    }

    public Bank(String name, String cuit, String address, String telephone) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.telephone = telephone;
        this.promotions = new ArrayList<Promotion>();
        this.cards =  new ArrayList<Card>();
        this.cardHolders = new ArrayList<CardHolder>();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuit() {
        return this.cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Promotion> getPromotions() {
        return this.promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }


    public List<CardHolder> getCardHolders() {
        return this.cardHolders;
    }

    public void setCardHolders(List<CardHolder> cardHolders) {
        this.cardHolders = cardHolders;
    }
}
