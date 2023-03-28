package com.tpdbd.cardpurchases.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
public class Bank {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String cuit;

    private String address;

    private String telephone;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Promotion> promotions;

    public Bank() {
    }

    public Bank(String name, String cuit, String address, String telephone) {
        this(name, cuit, address, telephone, new LinkedHashSet<Promotion>());
    }

    public Bank(String name, String cuit, String address, String telephone, Set<Promotion> promotions) {
        this.name = name;
        this.cuit = cuit;
        this.address = address;
        this.telephone = telephone;
        this.promotions = promotions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Set<Promotion> getPromotions() {
        return Collections.unmodifiableSet(this.promotions);
    }

    public boolean addPromotion(Promotion promotion) {
        return this.promotions.add(promotion);
    }

    public boolean removePromotion(Promotion promotion) {
        return this.promotions.remove(promotion);
    }

    @Override
    public String toString() {
        return "Bank [id=" + id + ", name=" + name + ", cuit=" + cuit + ", address=" + address + ", telephone="
                + telephone + ", promotions=" + promotions + "]";
    }

}
