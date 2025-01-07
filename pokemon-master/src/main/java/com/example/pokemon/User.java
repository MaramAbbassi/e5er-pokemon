package com.example.pokemon;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;



public class User {

    private Long id;


    private String username;


    private String email;


    private String password;


    private int limCoins;


    private String role = "User";

    @Transient
    private List<Pokemon> pokemons = new ArrayList<>();

    @Transient
    private List<Enchere> encheres = new ArrayList<>();

    // Getter and Setter for role

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public List<Enchere> getEncheres() {
        return encheres;
    }

    public void setEncheres(List<Enchere> encheres) {
        this.encheres = encheres;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLimCoins() {
        return limCoins;
    }

    public void setLimCoins(int limCoins) {
        this.limCoins = limCoins;
    }
}
