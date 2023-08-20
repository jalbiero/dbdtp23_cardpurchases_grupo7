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

    //@Column(length = 50, nullable = false)
    private String completeName;

    //@Column(unique = true, length = 8, nullable = false)
    @Indexed(unique = true)
    private String dni;

    //@Column(unique = true, length = 20, nullable = false)
    @Indexed(unique = true)
    private String cuil;

    //@Column(length = 200, nullable = false)
    private String address;

    //@Column(length = 20, nullable = false)
    private String telephone;

    //@Column(nullable = false)
    private LocalDate entry;

    //@OneToMany
    // @JoinTable(
    //     name="card",
    //     joinColumns=@JoinColumn(name="card_holder_id"),
    //     inverseJoinColumns=@JoinColumn(name="id"))
    //@DBRef
    @DocumentReference(lazy = true)
    private List<Card> cards;

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
}
