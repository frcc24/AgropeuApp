package com.francocorrea.agropeuapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.adapter.ListaEventosAdapter;
import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.NivelAcesso;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TelaEventosActivity extends AppCompatActivity {

    List<Evento> eventos = new ArrayList<>();
    List<Evento> eventosAux = new ArrayList<>();
    ArrayAdapter adapter;
    Preferencias preferencias;
    //    TextView txt_label_pesquisa;
    ConstraintLayout mProgressBar;
    ConstraintLayout fab_lo;
    private ValueEventListener firebaseListener;
    private boolean filtroMostrarEventosAntigos = false;
    private boolean filtroSomenteMeusEventos = false;
//    EditText edt_filtro;
//    Spinner spinner_filtros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_eventos);

        mProgressBar = findViewById(R.id.progress_bar_layout);
        //comecar com a barra de progresso visivel pois vai buscar os eventos todos ja na abertura
        exibirProgresso(true);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        preferencias = new Preferencias(this);

        fab_lo = findViewById(R.id.fab_lo);

        //so permite criar evento determinados niveis de usuario
        if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_03) ||
                preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_04) ||
                preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_10)) {
            fab_lo.setVisibility(View.VISIBLE);
        } else {
            fab_lo.setVisibility(View.GONE);
        }


        TextView txt_wecome = findViewById(R.id.txt_welcome);
        txt_wecome.setText(preferencias.getName());


        ListView lv_eventos = findViewById(R.id.lv_convidados_na_festa);
//        txt_label_pesquisa = findViewById(R.id.label_pesquisa);
//        edt_filtro = findViewById(R.id.edt_filtro);
//        spinner_filtros = findViewById(R.id.spinner_filtros);
//        final ImageView buscar = findViewById(R.id.img_buscar);

        ArrayAdapter adapter_spinner = ArrayAdapter.createFromResource(this, R.array.filtros, R.layout.spinner_item);
        adapter_spinner.setDropDownViewResource(R.layout.spinner_drop_item);
//        spinner_filtros.setAdapter(adapter_spinner);


//        buscar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check if no view has focus: Esconde o teclado apos apertar a lupa
//                View view = getCurrentFocus();
//                if (view != null) {
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                }
//                buscar();
//                limpaEdtPesquisa();
//            }
//        });
//
//        edt_filtro.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    buscar();
//                    limpaEdtPesquisa();
//                }
//                return false;
//            }
//        });


        adapter = new ListaEventosAdapter(TelaEventosActivity.this, eventos);

        lv_eventos.setAdapter(adapter);

        lv_eventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                preferencias.salvarBaseDados(eventos.get(position).getNome());
                String msg = "Evento setado para " + eventos.get(position).getNome();
                Log.e("AgropeuLog", msg);
//                Toast.makeText(TelaEventosActivity.this, msg, Toast.LENGTH_LONG).show();

                Intent i = new Intent(TelaEventosActivity.this, MainActivity.class);
                i.putExtra("evento", eventos.get(position));
                startActivity(i);
                finish();

            }
        });


        //já iniciar a tela com a lista preenchida
        buscar();

    }

//    private void limpaEdtPesquisa() {
//        edt_filtro.setText("");
//    }


    public void buscar() {
        exibirProgresso(true);
//        String pesquisa = edt_filtro.getText().toString().toUpperCase();
        filtrarResultados();
//        filtrarResultados(spinner_filtros.getSelectedItem().toString(), pesquisa);
//        if(pesquisa.length() > 0 )
//            txt_label_pesquisa.setText("Resultados para: " + pesquisa);
//        else
//            txt_label_pesquisa.setText("Mostrando os convidados");
    }

    private void filtrarResultados() {


        String basededados = preferencias.getEventoBaseDados();
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO");

        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
//                    new connectAsyncTaskEvento(dataSnapshot, filtro, pesquisa).execute();
                    new connectAsyncTaskEvento(dataSnapshot).execute();

                } else {
                    Toast.makeText(TelaEventosActivity.this, "Nenhum evento encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addValueEventListener(firebaseListener);

    }

    public void criarEvento(View view) {
        Intent i = new Intent(TelaEventosActivity.this, CriarEventoActivity.class);
//                i.putExtra("convidado" , convidados.get(position));
        startActivity(i);
        finish();

    }

    public void filtarEventos(View view2) {
        //todo criar filtros abrir tela com filtros


        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(TelaEventosActivity.this).inflate(R.layout.dialog_filtros, null);

        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
        final CheckBox cb_mostrarEventosAntigos = view.findViewById(R.id.cb_mostrarEventosAntigos);
        final CheckBox cb_mostrarSomenteMeusEventos = view.findViewById(R.id.cb_somenteMeusEventos);

        cb_mostrarEventosAntigos.setChecked(filtroMostrarEventosAntigos);
        cb_mostrarSomenteMeusEventos.setChecked(filtroSomenteMeusEventos);


        txt_tirulo.setText("Filtros");
        txt_mensagem.setText("Selecione quais filtros deseja aplicar");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaEventosActivity.this);
        builder.setView(view);
        //define um botão como positivo
        builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                filtroMostrarEventosAntigos = cb_mostrarEventosAntigos.isChecked();
                filtroSomenteMeusEventos = cb_mostrarSomenteMeusEventos.isChecked();

                filtrarResultados();

            }
        });

        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }

    private boolean isDataAnteriorAHoje(String dataEvento) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date enteredDate = null;
            Date currentDate = null;
            enteredDate = sdf.parse(dataEvento);
            currentDate = new Date();
            String sCurrentDate = sdf.format(currentDate);
            currentDate = sdf.parse(sCurrentDate);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(enteredDate);
            cal2.setTime(currentDate);
            boolean sameDay = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);


            return enteredDate.after(currentDate) || sameDay;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseListener != null) {
            firebaseListener = null;
        }
    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private class connectAsyncTaskEvento extends AsyncTask<Void, Void, String> {
        DataSnapshot dataSnapshot;

        connectAsyncTaskEvento(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
            eventos.clear();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {


            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                String sEvento = ds.getKey();
                Evento evento = new Evento();
                evento.setNome(sEvento);
                evento.setDataEvento(ds.child("dataEvento").getValue(String.class));
                evento.setDescEvento(ds.child("descEvento").getValue(String.class));
                evento.setDataCriacao(ds.child("dataCriacao").getValue(String.class));
                evento.setCriadoPor(ds.child("criadoPor").getValue(String.class));

                try {
                    evento.setImage(ds.child("img").getValue(String.class));
                } catch (Exception e) {
                    evento.setImage("1");
                }

                Log.e("AgropeuLog", sEvento);

                // verificar filtros

                //adiciona todos os eventos antigos de qualquer usuario
                if (filtroMostrarEventosAntigos && !filtroSomenteMeusEventos)
                    eventos.add(evento);
                else if (filtroMostrarEventosAntigos && filtroSomenteMeusEventos) {
                    if (evento.getCriadoPor().equals(preferencias.getName())) {
                        eventos.add(evento);
                    }
                } else if (!filtroMostrarEventosAntigos && filtroSomenteMeusEventos) {
                    if (evento.getCriadoPor().equals(preferencias.getName()) && isDataAnteriorAHoje(evento.getDataEvento())) {
                        eventos.add(evento);
                    }
                } else if (!filtroMostrarEventosAntigos && !filtroSomenteMeusEventos) {
                    if (isDataAnteriorAHoje(evento.getDataEvento())) {
                        eventos.add(evento);
                    }
                }


            }

            return "ok";


        }

        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (result.equals("ok")) {

                adapter.notifyDataSetChanged();
            }
        }


    }


}
