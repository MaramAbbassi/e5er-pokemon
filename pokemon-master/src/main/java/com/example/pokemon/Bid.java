package com.example.pokemon;

import jakarta.json.bind.annotation.JsonbTransient;

import java.time.LocalDateTime;


public class Bid {


    private Long BIDid;


    @JsonbTransient
    private Enchere enchere;

    private Long userId;
    private double amount;
    private LocalDateTime timestamp;
    public Bid(){}
    public Bid(Enchere enchere, Long userId, double amount, LocalDateTime timestamp) {
        this.enchere = enchere;
        this.userId = userId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
    // Getters et setters
    public Long getBIDid() {
        return BIDid;
    }

    public void setiBIDd(Long id) {
        this.BIDid = id;
    }

    public Enchere getEnchere() {
        return enchere;
    }

    public void setEnchere(Enchere enchere) {
        this.enchere = enchere;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
