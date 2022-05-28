package com.francocorrea.agropeuapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.adapter.ListaConvidadosAdapter;
import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.interfaces.OnGetDataListener;
import com.francocorrea.agropeuapp.model.Convidado;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VerQuemChegouActivity extends AppCompatActivity implements OnGetDataListener {
    List<Convidado> convidados = new ArrayList<>();
    List<Convidado> convidadosAux = new ArrayList<>();
    ArrayAdapter adapter;
    Preferencias preferencias;
    TextView txt_lst_convidados;
    TextView txt_label_pesquisa;
    ConstraintLayout mProgressBar;
    EditText edt_filtro;
    Spinner spinner_filtros;
    private ValueEventListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_quem_chegou);

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        preferencias = new Preferencias(this);


        ListView lv_convidados = findViewById(R.id.lv_convidados_na_festa);
        txt_lst_convidados = findViewById(R.id.txt_num_convidados_chegaram);
        txt_label_pesquisa = findViewById(R.id.label_pesquisa);
        edt_filtro = findViewById(R.id.edt_filtro);
        spinner_filtros = findViewById(R.id.spinner_filtros);
        final ImageView buscar = findViewById(R.id.img_buscar);

        TextView txtNomeEvento = findViewById(R.id.txt_nome_convidado);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        ArrayAdapter adapter_spinner = ArrayAdapter.createFromResource(this, R.array.filtros, R.layout.spinner_item);
        adapter_spinner.setDropDownViewResource(R.layout.spinner_drop_item);
        spinner_filtros.setAdapter(adapter_spinner);

        txt_lst_convidados.setText("Digite algum filtro de pesquisa");

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if no view has focus: Esconde o teclado apos apertar a lupa
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                buscar();
                limpaEdtPesquisa();
            }
        });

        edt_filtro.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    buscar();
                    limpaEdtPesquisa();
                }
                return false;
            }
        });


        adapter = new ListaConvidadosAdapter(VerQuemChegouActivity.this, convidadosAux);

        lv_convidados.setAdapter(adapter);

        lv_convidados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                exibirProgresso(true);
                Intent i = new Intent(VerQuemChegouActivity.this, VizualizarDadosConvidadoActivity.class);
                i.putExtra("convidado", convidados.get(position));
                startActivity(i);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        exibirProgresso(false);
    }

    private void limpaEdtPesquisa() {
        edt_filtro.setText("");
    }


    public void buscar() {
        exibirProgresso(true);
        txt_lst_convidados.setText("Pesquisiando, Aguarde...");
        String pesquisa = edt_filtro.getText().toString().toUpperCase();
        filtrarResultados(spinner_filtros.getSelectedItem().toString(), pesquisa);
        if (pesquisa.length() > 0)
            txt_label_pesquisa.setText("Resultados para: " + pesquisa);
        else
            txt_label_pesquisa.setText("Mostrando os convidados");
    }

    private void filtrarResultados(final String filtro, final String pesquisa) {

        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS
        String basededados = preferencias.getEventoBaseDados();
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByKey();

        if (filtro.equals(getString(R.string.numConvite))) {
            firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByKey().equalTo(pesquisa);
        } else if (filtro.equals(getString(R.string.convidadosPresentes))) {
            firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByChild("status").startAt("presente");
        } else if (filtro.equals(getString(R.string.numDocumento))) {
            firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByChild("documento").equalTo(pesquisa);
        } else if (filtro.equals(getString(R.string.convidadosInativos))) {
            firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByChild("status").startAt("INATIVO");
        }
//        else if(filtro.equals(getString(R.string.nomeConvidado))){
//            firebase = ConfiguracaoFirebase.getFirebase().child("CONVIDADOS_FINAL_ANO").orderByChild("nome").startAt(pesquisa).endAt(pesquisa + "\uf8ff");
//        }

        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new connectAsyncTaskVerTodosConvidados(dataSnapshot, filtro, pesquisa).execute();
                } else {
                    Toast.makeText(VerQuemChegouActivity.this, "Nenhum Convidado encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addValueEventListener(firebaseListener);

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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess(DataSnapshot data, int convidadosPresentes, int inativos) {

        txt_lst_convidados.setText("Total: " + convidados.size() +
                " Presentes: " + convidadosPresentes);

        convidadosAux.clear();
        convidadosAux.addAll(convidados);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void onFailed(DatabaseError databaseError) {

    }


    private class connectAsyncTaskVerTodosConvidados extends AsyncTask<Void, Integer, String> {
        DataSnapshot dataSnapshot;
        String filtro;
        String pesquisa;
        int convidadosPresentes = 0;
        int inativos = 0;

        connectAsyncTaskVerTodosConvidados(DataSnapshot dataSnapshot, String filtro, String pesquisa) {
            this.dataSnapshot = dataSnapshot;
            this.filtro = filtro;
            this.pesquisa = pesquisa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            convidados.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Convidado convidado = ds.getValue(Convidado.class);

                if (filtro.equals(getString(R.string.numConvite))) {
                    if (convidado.getNumeroConvite().equals(pesquisa)) {
                        if (convidado.getStatus().equals("presente")) {
                            convidadosPresentes++;
                        }
                        convidados.add(convidado);
                    }
                } else if (filtro.equals(getString(R.string.nomeConvidado))) {
                    String nomeConvidado = convidado.getNome().toUpperCase();
                    int i = nomeConvidado.indexOf(pesquisa);
                    if (i != -1) {
                        if (convidado.getStatus().equals("presente")) {
                            convidadosPresentes++;
                        }
                        convidados.add(convidado);
                    }
                } else if (filtro.equals(getString(R.string.numDocumento))) {
                    String numDoc = convidado.getDocumento().toUpperCase();
                    int i = numDoc.indexOf(pesquisa);
                    if (i != -1) {
                        if (convidado.getStatus().equals("presente")) {
                            convidadosPresentes++;
                        }
                        convidados.add(convidado);
                    }
                } else if (filtro.equals(getString(R.string.convidadosInativos))) {
                    if (convidado.getStatus().equalsIgnoreCase("INATIVO")) {
                        inativos++;
                        convidados.add(convidado);
                    }

                } else {// caso seja todos os convidados
                    if (convidado.getStatus().equals("presente") && convidado.getNome().contains(pesquisa)) {
                        convidadosPresentes++;
                        convidados.add(convidado);
                    }


                }

            }
            return "ok";
        }


        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (result.equals("ok")) {
                onSuccess(dataSnapshot, convidadosPresentes, inativos);
            }


        }
    }


}
