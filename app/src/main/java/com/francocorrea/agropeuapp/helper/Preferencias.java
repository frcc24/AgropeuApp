package com.francocorrea.agropeuapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private final Context contexto;
    private final SharedPreferences preferences;
    private final String NOME_ARQUIVO = "AGROPEU.preferencias";
    private final int MODE = 0;
    private final SharedPreferences.Editor editor;

    private final String CHAVE_USUARIO = "players";
    private final String CHAVE_NIVEL_ACESSO = "acesso";
    private final String CHAVE_NOME = "nome";
    private final String CHAVE_MODO = "modo";
    private final String CHAVE_INTERVALO_INICIAL = "I_INICIAL";
    private final String CHAVE_INTERVALO_FINAL = "I_FINAL";
    private final String CHAVE_BASE_DE_DADOS = "base_dados";


    public Preferencias(Context contextoParametro) {
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();

    }

    public void restoreDefault() {
        editor.putString(CHAVE_USUARIO, "");

        editor.commit();
    }

    public void salvarUserName(String user_name) {
        int i = user_name.indexOf("@");
        if (i != -1) {
            String nome = user_name.substring(0, i);
            editor.putString(CHAVE_NOME, nome);
        } else {
            editor.putString(CHAVE_NOME, user_name);
        }
        editor.putString(CHAVE_USUARIO, user_name);
        editor.commit();
    }

    public void salvarAcesso(String nivelAcesso) {
        editor.putString(CHAVE_NIVEL_ACESSO, nivelAcesso);
        editor.commit();
    }

    public void salvarBaseDados(String baseDados) {
        editor.putString(CHAVE_BASE_DE_DADOS, baseDados);
        editor.commit();
    }

    public void salvarModoAcesso(String modoAcesso) {
        editor.putString(CHAVE_MODO, modoAcesso);
        editor.commit();
    }

    public String getModoAcesso() {

        return preferences.getString(CHAVE_MODO, "online");
    }

    public void salvarIntervaloInicial(String modoAcesso) {
        editor.putString(CHAVE_INTERVALO_INICIAL, modoAcesso);
        editor.commit();
    }

    public String getIntervaloInicial() {

        return preferences.getString(CHAVE_INTERVALO_INICIAL, "0");
    }

    public void salvarIntervaloFinal(String modoAcesso) {
        editor.putString(CHAVE_INTERVALO_FINAL, modoAcesso);
        editor.commit();
    }

    public String getIntervaloFinal() {

        return preferences.getString(CHAVE_INTERVALO_FINAL, "0");
    }

    public String getUserName() {

        return preferences.getString(CHAVE_USUARIO, "");
    }

    public String getName() {

        return preferences.getString(CHAVE_NOME, "");
    }

    public String getNivelAcesso() {
        return preferences.getString(CHAVE_NIVEL_ACESSO, "1");
    }


    public String getEventoBaseDados() {
        return preferences.getString(CHAVE_BASE_DE_DADOS, "ANIVERSÁRIO PARTE 1");
    }


}
