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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Convidado;
import com.francocorrea.agropeuapp.model.Evento;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CriarEventoActivity extends AppCompatActivity {

    ConstraintLayout mProgressBar, excel_lo;
    Convidado convidado;
    TextView txt_dataEvento;
    EditText edt_nomeEvento;
    EditText edt_descricaoEvento;
    Preferencias preferencias;
    RadioButton rb1, rb2, rb3;
    ImageButton btn_calendar;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);
        evento = new Evento();

        preferencias = new Preferencias(this);
        mProgressBar = findViewById(R.id.progress_bar_layout);
        excel_lo = findViewById(R.id.excel_lo);
        edt_nomeEvento = findViewById(R.id.edt_nomeEvento);
        txt_dataEvento = findViewById(R.id.txt_dataEvento);
        edt_descricaoEvento = findViewById(R.id.edt_DescricaoEvento);
        rb1 = findViewById(R.id.rb_img1);
        rb2 = findViewById(R.id.rb_img2);
        rb3 = findViewById(R.id.rb_img3);
        btn_calendar = findViewById(R.id.btn_calendar);

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
                new DatePickerDialog(CriarEventoActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Button btn_criarEvento = findViewById(R.id.btn_Criar_Evento);
        btn_criarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_nomeEvento.getText().length() > 0) {
                    String sDataEvento = txt_dataEvento.getText().toString();
                    String nomeDoEventoMaiusculo = edt_nomeEvento.getText().toString().toUpperCase();
                    preferencias.salvarBaseDados(nomeDoEventoMaiusculo);


                    if (isDataValida(sDataEvento)) {
                        evento.setDataEvento(sDataEvento);
                        evento.setNome(nomeDoEventoMaiusculo);
                        evento.setCriadoPor(preferencias.getName());

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ");
                        String currentDateandTime = sdf.format(new Date());
                        evento.setDataCriacao(currentDateandTime);

                        evento.setDescEvento(edt_descricaoEvento.getText().toString());

                        if (evento.getImage() == null) {
                            Toast.makeText(CriarEventoActivity.this, "Nenhuma imagem selecionada", Toast.LENGTH_LONG).show();
                        }

                        evento.salvarEventoFirebase(CriarEventoActivity.this);

                        Toast.makeText(CriarEventoActivity.this, "Evento " + nomeDoEventoMaiusculo + ": criado com sussesso!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CriarEventoActivity.this, TelaEventosActivity.class));
                        finish();
                    } else {
                        Toast.makeText(CriarEventoActivity.this, "Data selecionada nao está no formato correto: dd/MM/aaaa", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(CriarEventoActivity.this, "Nome do Evento não pode ficar em branco", Toast.LENGTH_LONG).show();

                }

            }
        });


    }


    private boolean isDataValida(String data) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        formatter.setLenient(false);
        try {
            Date date = formatter.parse(data);

        } catch (ParseException e) {
            //If input date is in different format or invalid.
            Toast.makeText(CriarEventoActivity.this, "Data Invalida " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void updateLabelDataEvento() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        String novaDataEvento = sdf.format(myCalendar.getTime());
        txt_dataEvento.setText(novaDataEvento);

    }

    public void selecionarImagemEvento(View v) {

        AlertDialog alerta;
        View viewAlerta;
        viewAlerta = LayoutInflater.from(CriarEventoActivity.this).inflate(R.layout.dialog_lista_imagens, null);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(CriarEventoActivity.this);
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


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent i = new Intent(CriarEventoActivity.this, TelaEventosActivity.class);
//                i.putExtra("convidado" , convidados.get(position));
        startActivity(i);
        finish();

    }
}
