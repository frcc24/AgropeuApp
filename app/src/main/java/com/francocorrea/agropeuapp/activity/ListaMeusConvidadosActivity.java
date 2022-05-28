package com.francocorrea.agropeuapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.adapter.ListaConvidadosAdapter;
import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Convidado;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaMeusConvidadosActivity extends AppCompatActivity {

    List<Convidado> convidados = new ArrayList<>();
    ArrayAdapter adapter;
    Preferencias preferencias;
    ConstraintLayout mProgressBar;
    private ValueEventListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_meus_convidados);

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        preferencias = new Preferencias(this);

        getConvidados();

        TextView txtNomeEvento = findViewById(R.id.txt_nome_convidado);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        ListView lv_convidados = findViewById(R.id.lv_convidados);
        TextView txt_lst_convidados = findViewById(R.id.txt_lst_convidados);
        txt_lst_convidados.setText("Lista de convidados de " + preferencias.getName());

        adapter = new ListaConvidadosAdapter(ListaMeusConvidadosActivity.this, convidados);

        lv_convidados.setAdapter(adapter);

        lv_convidados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ListaMeusConvidadosActivity.this, VizualizarDadosConvidadoActivity.class);
                i.putExtra("convidado", convidados.get(position));
                startActivity(i);
            }
        });

    }

    private void getConvidados() {
        exibirProgresso(true);
        final String userAdmin = preferencias.getUserName();

        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS
        String basededados = preferencias.getEventoBaseDados();
        //Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).orderByChild("funcionario").equalTo(userAdmin);
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByKey();

        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                convidados.clear();
                if (dataSnapshot != null) {
                    long dataSize = dataSnapshot.getChildrenCount();
                    new connectAsyncTaskVerTodosConvidados(dataSnapshot, userAdmin).execute();
                } else {
                    Toast.makeText(ListaMeusConvidadosActivity.this, "Nenhum Convidado encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addValueEventListener(firebaseListener);

    }

    @Override
    public void onBackPressed() {
        finish();
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


    private class connectAsyncTaskVerTodosConvidados extends AsyncTask<Void, Void, String> {
        DataSnapshot dataSnapshot;
        String userAdmin;

        connectAsyncTaskVerTodosConvidados(DataSnapshot dataSnapshot, String userAdmin) {
            this.dataSnapshot = dataSnapshot;
            this.userAdmin = userAdmin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Convidado convidado = ds.getValue(Convidado.class);

                if (convidado.getFuncionario().equals(userAdmin)) {
                    convidados.add(convidado);
                }
            }

            if (convidados.size() > 0)
                return "ok";
            else
                return "Não existem convidados para esse Usuário.";


        }

        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (!result.equals("ok")) {
                Toast.makeText(ListaMeusConvidadosActivity.this, result, Toast.LENGTH_LONG).show();
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }


}
