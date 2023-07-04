package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class CardHolder {
    @Id
    @GeneratedValue
    private Long id;

    private String completeName;

    private String dni;

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

    public String getCompleteName() {
        return completeName;
    }

    public void setCompleteName(String completeName) {
        this.completeName = completeName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
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

    public LocalDate getEntry() {
        return entry;
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
