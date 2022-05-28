package com.francocorrea.agropeuapp.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckStatus {

    public static boolean isInternetConnected(Context contexto) {
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);//Pego a conectividade do contexto o qual o metodo foi chamado
        NetworkInfo netInfo = cm.getActiveNetworkInfo();//Crio o objeto netInfo que recebe as informacoes da NEtwork
        //Se o objeto for nulo ou nao tem conectividade retorna false
        return (netInfo != null) && (netInfo.isConnectedOrConnecting()) && (netInfo.isAvailable());
    }
}
