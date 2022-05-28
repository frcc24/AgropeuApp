package com.francocorrea.agropeuapp.interfaces;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface OnGetDataListener {
    void onStart();

    void onSuccess(DataSnapshot data, int convidadosPresentes, int inativos);

    void onFailed(DatabaseError databaseError);
}