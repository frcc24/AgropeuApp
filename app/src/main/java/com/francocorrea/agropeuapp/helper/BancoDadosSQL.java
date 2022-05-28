package com.francocorrea.agropeuapp.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDadosSQL extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public BancoDadosSQL(Context context, String name, int version) {
        super(context, name, null, version);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqld) {
        sqld.execSQL("CREATE TABLE convidados_tbl ("
                + "id INTEGER PRIMARY KEY autoincrement,"
                + "id_num_convite varchar(145) NOT NULL,"
                + " nome varchar(145) NOT NULL ,"
                + " documento varchar(145) NOT NULL ,"
                + " origem varchar(145) NOT NULL ,"
                + " nascimento varchar(45) NOT NULL ,"
                + " idade varchar(45) NOT NULL ,"
                + " funcionario varchar(145) NOT NULL ,"
                + " observacao varchar(450) NOT NULL,"
                + " status varchar(50) NOT NULL,"
                + " horarioChegada varchar(90) NOT NULL,"
                + " conviteSendoLidoPor varchar(95) NOT NULL"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqld, int i, int i1) {
        //IMPLEMENTAR
    }
}


