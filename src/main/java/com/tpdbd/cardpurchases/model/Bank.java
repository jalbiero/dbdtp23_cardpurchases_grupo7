package com.tpdbd.cardpurchases.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document 
public class Bank {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String cuit;

    private String address;

    private String telephone;

    @DocumentReference(lazy = true)
    private List<Promotion> promotions;

    @DocumentReference(lazy = true)
    private List<Card> cards;

    public Bank() {
    }

    public Bank(String name, String cuit, String address, String telephone) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.telephone = telephone;
        this.promotions = new ArrayList<Promotion>();
        this.cards = new ArrayList<Card>();
    }

    public String getId() {
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
}
