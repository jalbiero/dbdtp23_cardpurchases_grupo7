package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class CardHolder {
    @Id
    private String id;

    private String completeName;

    @Indexed(unique = true)
    private String dni;

    @Indexed(unique = true)
    private String cuil;

    private String address;

    private String telephone;

    private LocalDate entry;

    @DocumentReference(lazy = true)
    private List<Card> cards;

    @DocumentReference(lazy = true)
    private List<Bank> banks;

    public CardHolder() {
    }

    public CardHolder(
        String completeName,
        String dni,
        String cuil,
        String address,
        String telephone,
        LocalDate entry)
    {
        this.completeName = completeName;
        this.dni = dni;
        this.cuil = cuil;
        this.address = address;
        this.telephone = telephone;
        this.entry = entry;
        this.cards = new ArrayList<Card>();
        this.banks = new ArrayList<Bank>();
    }

    public String getId() {
        return this.id;
    }

    public String getCompleteName() {
        return this.completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getDni() {
        return this.dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCuil() {
        return this.cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
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

    public LocalDate getEntry() {
        return this.entry;
    }

    public void setEntry(LocalDate entry) {
        this.entry = entry;
    }

    public List<Card> getCards() {
        return this.cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Bank> getBanks() {
        return this.banks;
    }

    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }
}
