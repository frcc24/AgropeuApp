package com.francocorrea.agropeuapp.helper;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AlertDialogCustom extends DialogFragment {

    private String titulo, mensagem, positiveBtn, negativeBtn, neutroBtn;

    public AlertDialogCustom() {

    }

    public void setAlertDialogCustom(String titulo, String mensagem, String positiveBtn, String negativeBtn, String neutroBtn) {
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.positiveBtn = positiveBtn;
        this.negativeBtn = negativeBtn;
        this.neutroBtn = neutroBtn;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(this.mensagem);

        if (positiveBtn != null) {
            builder.setPositiveButton(positiveBtn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // FIRE ZE MISSILES!
                }
            });
        }

        if (negativeBtn != null) {
            builder.setNegativeButton(negativeBtn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
        }

        if (titulo != null) {
            builder.setTitle(titulo);
        }

        if (neutroBtn != null) {
            builder.setNeutralButton(neutroBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //to do
                }
            });
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
