package com.tpdbd.cardpurchases.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Where(clause = "deleted = false") // Note: SQL specific, Mongo version will need another solution
public abstract class Promotion {
    @Id
    @GeneratedValue
    @Column(name = "promotion_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(unique = true, length = 50, nullable = false)
    private String code;

    @Column(length = 100, nullable = false)
    private String promotionTitle;

    @Column(length = 50, nullable = false)
    private String nameStore;

    @Column(length = 20, nullable = false)
    private String cuitStore;

    @Column(nullable = false)
    private LocalDate validityStartDate;

    @Column(nullable = false)
    private LocalDate validityEndDate;

    @Column(length = 200, nullable = false)
    private String comments;

    // Implements a logical delete with the help of @Where annotation (see
    // PromotionRepository.deleteByCode for more information about its usage
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

    public Long getId() {
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
