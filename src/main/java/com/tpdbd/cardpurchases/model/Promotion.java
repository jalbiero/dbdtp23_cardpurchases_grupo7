package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public abstract class Promotion {
    @Id
    private String id;

    @DocumentReference
    private Bank bank;

    @Indexed(unique = true)
    private String code;

    private String promotionTitle;

    private String nameStore;

    private String cuitStore;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private String comments;

    // Implements a logical delete with the help of the repository (see
    // PromotionRepository.deleteByCode for more information about its usage)
    @SuppressWarnings("unused")
    private Boolean deleted;

    public Promotion() {
    }

    public Promotion(
        Bank bank,
        String code,
        String promotionTitle,
        String nameStore,
        String cuitStore,
        LocalDate validityStartDate,
        LocalDate validityEndDate,
        String comments)
    {
        this.bank = bank;
        this.code = code;
        this.promotionTitle = promotionTitle;
        this.nameStore = nameStore;
        this.cuitStore = cuitStore;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
        this.comments = comments;
        this.deleted = false;
    }

    public String getId() {
        return this.id;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

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
}
