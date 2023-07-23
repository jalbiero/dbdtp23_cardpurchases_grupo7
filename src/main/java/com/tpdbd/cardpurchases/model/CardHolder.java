package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class CardHolder {
    @Id
    @GeneratedValue
    private Long id;

    private String completeName;

    @Column(unique = true)
    private String dni;

    @Column(unique = true)
    private String cuil;

    private String address;

    private String telephone;

    private LocalDate entry;

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

    @Override
    public String toString() {
        return "CardHolder [id=" + id + ", completeName=" + completeName + ", dni=" + dni + ", cuil=" + cuil
                + ", address=" + address + ", telephone=" + telephone + ", entry=" + entry + "]";
    }
}
