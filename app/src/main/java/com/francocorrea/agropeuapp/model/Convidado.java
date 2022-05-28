package com.francocorrea.agropeuapp.model;

import android.content.Context;

import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Convidado implements Serializable {

    String numeroConvite;
    String documento;
    String nome;
    String origem;
    String nascimento;
    String idade;
    String funcionario;
    String observacao;
    String status;
    String horarioChegada;
    private String conviteSendoLidoPor;


    public Convidado() {

    }


//


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHorarioChegada() {
        return horarioChegada;
    }

    public void setHorarioChegada(String horarioChegada) {
        this.horarioChegada = horarioChegada;
    }

    public String getNumeroConvite() {
        return numeroConvite;
    }

    public void setNumeroConvite(String numeroConvite) {
        this.numeroConvite = numeroConvite;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void salvarConvidadoFirebase(Context contextoParametro) {
        Preferencias preferencias;
        preferencias = new Preferencias(contextoParametro);
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS
        String basededados = preferencias.getEventoBaseDados();
        firebase.child("EVENTO").child(basededados).child("CONVIDADOS").child(numeroConvite).setValue(this);


    }

    public void removerTODOSConvidados() {
        //todo descomentar, pois comentei so para nao haver chances de acontecer algum erro acidental
        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase();
//        firebase.child("CONVIDADOS").removeValue();
        firebase.child("CONVIDADOSLISTA").removeValue();

    }

    public String getConviteSendoLidoPor() {
        return conviteSendoLidoPor;
    }

    public void setConviteSendoLidoPor(String conviteSendoLidoPor) {
        this.conviteSendoLidoPor = conviteSendoLidoPor;
    }
}
