package com.francocorrea.agropeuapp.model;

import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {

    String id;
    String email;
    String senha;
    boolean isAdmin;
    String nivelAcesso;

    public Usuario() {
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void salvarUsuerFireBase() {
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        firebase.child("USUARIOS").child(id).setValue(this);
    }
}

