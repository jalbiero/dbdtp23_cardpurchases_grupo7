package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Inheritance
public abstract class Promotion {
    @Id
    @GeneratedValue
    private Long id;

    // @ManyToOne(cascade = CascadeType.ALL)
    // private Bank bank;

    private String code;

    private String promotionTitle;

    private String nameStore;

    private String cuitStore;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private String comments;

    public Promotion() {
    }

    // public Promotion(Bank bank, String code, String promotionTitle, String
    // nameStore, String cuitStore, LocalDate validityStartDate, LocalDate
    // validityEndDate, String comments) {
    public Promotion(String code, String promotionTitle, String nameStore, String cuitStore,
            LocalDate validityStartDate, LocalDate validityEndDate, String comments) {
        // this.bank = bank;
        this.code = code;
        this.promotionTitle = promotionTitle;
        this.nameStore = nameStore;
        this.cuitStore = cuitStore;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
        this.comments = comments;
    }

    // public Bank getBank() {
    // return bank;
    // }

    // public void setBank(Bank bank) {
    // this.bank = bank;
    // }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPromotionTitle() {
        return promotionTitle;
    }

    public void setPromotionTitle(String promotionTitle) {
        this.promotionTitle = promotionTitle;
    }

    public String getNameStore() {
        return nameStore;
    }

    public void setNameStore(String nameStore) {
        this.nameStore = nameStore;
    }

    public String getCuitStore() {
        return cuitStore;
    }

    public void setCuitStore(String cuitStore) {
        this.cuitStore = cuitStore;
    }

    public LocalDate getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(LocalDate validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public LocalDate getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(LocalDate validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Promotion [id=" + id + /* ", bank=" + bank.getName() + */ ", code=" + code + ", promotionTitle="
                + promotionTitle
                + ", nameStore=" + nameStore + ", cuitStore=" + cuitStore + ", validityStartDate=" + validityStartDate
                + ", validityEndDate=" + validityEndDate + ", comments=" + comments + "]";
    }
}
