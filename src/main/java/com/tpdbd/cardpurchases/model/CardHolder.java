package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class CardHolder {
    @Id
    @GeneratedValue
    @Column(name = "card_holder_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String completeName;

    @Column(unique = true, length = 8, nullable = false)
    private String dni;

    @Column(unique = true, length = 20, nullable = false)
    private String cuil;

    @Column(length = 200, nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String telephone;

    @Column(nullable = false)
    private LocalDate entry;

    @OneToMany(mappedBy = "cardHolder")
    private List<Card> cards;

    @ManyToMany
    @JoinTable(
        name="banks_card_holders",
        joinColumns=@JoinColumn( name="card_holder_id"),
        inverseJoinColumns=@JoinColumn(name="bank_id"))
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

    public Long getId() {
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

    public List<Bank> getBanks() {
        return this.banks;
    }

    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }
}
