package com.francocorrea.agropeuapp.model;

import android.content.Context;

import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Evento implements Serializable {


    String dataCriacao;
    String CriadoPor;
    String nome;
    String image;
    String descEvento;
    String dataEvento;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image == null)
            image = "1";

        this.image = image;
    }

    public String getDescEvento() {
        return descEvento;
    }

    public void setDescEvento(String descEvento) {
        this.descEvento = descEvento;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getCriadoPor() {
        return CriadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        CriadoPor = criadoPor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    public void salvarEventoFirebase(Context contextoParametro) {
        Preferencias preferencias;
        preferencias = new Preferencias(contextoParametro);
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS
        String basededados = preferencias.getEventoBaseDados();

        firebase.child("EVENTO").child(basededados).child("dataEvento").setValue(dataEvento);
        firebase.child("EVENTO").child(basededados).child("criadoPor").setValue(getCriadoPor());
        firebase.child("EVENTO").child(basededados).child("dataCriacao").setValue(getDataCriacao());
        firebase.child("EVENTO").child(basededados).child("descEvento").setValue(getDescEvento());
        firebase.child("EVENTO").child(basededados).child("img").setValue(getImage());


    }

    public void removerEvento(String nome_evento) {
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        firebase.child("EVENTO").child(nome_evento).removeValue();
    }


}
