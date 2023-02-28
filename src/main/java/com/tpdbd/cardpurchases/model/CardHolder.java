package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
public class CardHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String completeName;

    private String dni;

    private String cuil;

    private String address;

    private String telephone;

    private LocalDate entry;

    // @formatter:off
    public CardHolder(
        String completeName, 
        String dni, 
        String cuil, 
        String address, 
        String telephone, 
        LocalDate entry) 
    // @formatter:off
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
}
