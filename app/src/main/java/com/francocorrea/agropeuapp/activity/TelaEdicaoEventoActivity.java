package com.francocorrea.agropeuapp.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Evento;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TelaEdicaoEventoActivity extends AppCompatActivity {
    Preferencias preferencias;
    TextView txtNomeEvento;
    Button btn_removeEvent, btn_salvarEvento;
    ImageButton btn_calendar;
    String nomeEvento;
    Calendar myCalendar;
    TextView dataEvento, txtCriadoPor;
    EditText edt_descEvento;
    Evento evento;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_edicao_evento);

        evento = (Evento) getIntent().getSerializableExtra("evento");


        txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtCriadoPor = findViewById(R.id.txt_CriadoPor);
        edt_descEvento = findViewById(R.id.edt_descEvento);
        btn_removeEvent = findViewById(R.id.btn_removerEvento);
        btn_salvarEvento = findViewById(R.id.btn_salvarEvento);
        btn_calendar = findViewById(R.id.btn_calendar);

        dataEvento = findViewById(R.id.txt_dataEvento);
        dataEvento.setText(evento.getDataEvento());
        txtCriadoPor.setText("Evento criado por: " + evento.getCriadoPor());


        preferencias = new Preferencias(this);
        nomeEvento = preferencias.getEventoBaseDados();

        txtNomeEvento.setText(nomeEvento);
        edt_descEvento.setText(evento.getDescEvento());

        btn_removeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEvent();
            }
        });

        btn_salvarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarEdicao();
            }
        });


        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDataEvento();
            }

        };

        btn_calendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(TelaEdicaoEventoActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }

    private void updateLabelDataEvento() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        String novaDataEvento = sdf.format(myCalendar.getTime());
        dataEvento.setText(novaDataEvento);
        evento.setDataEvento(novaDataEvento);

    }

    private void salvarEdicao() {
        evento.setDescEvento(edt_descEvento.getText().toString());
        evento.salvarEventoFirebase(TelaEdicaoEventoActivity.this);
        Toast.makeText(TelaEdicaoEventoActivity.this, "Evento " + nomeEvento + ": Alterado com sussesso!", Toast.LENGTH_LONG).show();
        finish();
    }


    private void removeEvent() {

        AlertDialog alerta;
        View viewAlerta;
        viewAlerta = LayoutInflater.from(TelaEdicaoEventoActivity.this).inflate(R.layout.dialog_alerta, null);

        TextView txt_mensagem = viewAlerta.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = viewAlerta.findViewById(R.id.txt_titulo_alerta);
        txt_tirulo.setText("EXCLUIR EVENTO?");
        txt_mensagem.setText("Ao confirmar, todos os dados do evento serão apagados do servidor. DESEJA CONTINUAR?");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaEdicaoEventoActivity.this);
        builder.setView(viewAlerta);
        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                new Evento().removerEvento(nomeEvento);
                Intent i = new Intent(TelaEdicaoEventoActivity.this, TelaEventosActivity.class);
//                i.putExtra("convidado" , convidados.get(position));
                startActivity(i);
                finish();

            }
        });
        //define um botão como negativo.

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }


    public void selecionarImagemEvento(View v) {

        AlertDialog alerta;
        View viewAlerta;
        viewAlerta = LayoutInflater.from(TelaEdicaoEventoActivity.this).inflate(R.layout.dialog_lista_imagens, null);

        final RadioButton rb_img1 = viewAlerta.findViewById(R.id.rb_img1);
        final RadioButton rb_img2 = viewAlerta.findViewById(R.id.rb_img2);
        final RadioButton rb_img3 = viewAlerta.findViewById(R.id.rb_img3);
        final RadioButton rb_img4 = viewAlerta.findViewById(R.id.rb_img4);
        final RadioButton rb_img5 = viewAlerta.findViewById(R.id.rb_img5);
        TextView txt_mensagem = viewAlerta.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = viewAlerta.findViewById(R.id.txt_titulo_alerta);


        RadioGroup radioGroup = viewAlerta.findViewById(R.id.rg_listaImagens);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb_img1.getId())
                    evento.setImage("1");
                else if (checkedId == rb_img2.getId())
                    evento.setImage("2");
                else if (checkedId == rb_img3.getId())
                    evento.setImage("3");
                else if (checkedId == rb_img4.getId())
                    evento.setImage("4");
                else if (checkedId == rb_img5.getId())
                    evento.setImage("5");
            }
        });


//        txt_tirulo.setText("EXCLUIR EVENTO?");
//        txt_mensagem.setText("Ao confirmar, todos os dados do evento serão apagados do servidor. DESEJA CONTINUAR?");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaEdicaoEventoActivity.this);
        builder.setView(viewAlerta);
        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
//                new Evento().removerEvento(nomeEvento);
//                Intent i = new Intent(TelaEdicaoEventoActivity.this, TelaEventosActivity.class);
////                i.putExtra("convidado" , convidados.get(position));
//                startActivity(i);
//                finish();


            }
        });
        //define um botão como negativo.

//        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//
//            }
//        });

        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }


}
