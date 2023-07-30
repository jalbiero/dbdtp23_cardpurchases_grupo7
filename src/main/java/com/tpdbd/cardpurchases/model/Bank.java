package com.tpdbd.cardpurchases.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Bank {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(unique = true, length = 20, nullable = false)
    private String cuit;

    @Column(length = 200, nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String telephone;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name="promotion",
        joinColumns=@JoinColumn(name="bank_id"),
        inverseJoinColumns=@JoinColumn(name="id"))
    private List<Promotion> promotions;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name="card",
        joinColumns=@JoinColumn(name="bank_id"),
        inverseJoinColumns=@JoinColumn(name="id"))
    private List<Card> cards;

    public Bank() {
    }

    public Bank(String name, String cuit, String address, String telephone) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.telephone = telephone;
        this.promotions = new ArrayList<Promotion>();
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

    public List<Card> getCards() {
        return this.cards;
    }
}
