package com.francocorrea.agropeuapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.CheckStatus;
import com.francocorrea.agropeuapp.helper.MaskEditUtil;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Convidado;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CadastrarConvidadosActivity extends AppCompatActivity {
    EditText edt_nomeConvidado_cadastroNovoConvite;
    EditText edt_rg_cadastroNovoConvite;
    EditText edt_nasc_cadastro_novoConvite;
    EditText edt_obs_cadastroNovoConvite;
    Convidado convidado;
    ConstraintLayout mProgressBar;
    Preferencias preferencias;
    private String erro;
    private ValueEventListener firebaseListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_convidados);
        preferencias = new Preferencias(this);

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        convidado = new Convidado();

        edt_nomeConvidado_cadastroNovoConvite = findViewById(R.id.edt_nomeConvidado_cadastroNovoConvite);
        edt_rg_cadastroNovoConvite = findViewById(R.id.edt_rg_cadastroNovoConvite);
        edt_nasc_cadastro_novoConvite = findViewById(R.id.edt_nasc_cadastro_novoConvite);
        edt_obs_cadastroNovoConvite = findViewById(R.id.edt_obs_cadastroNovoConvite);
        TextView txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        Button bt_incluirConvite = findViewById(R.id.bt_incluirConvite);


        edt_nasc_cadastro_novoConvite.addTextChangedListener(MaskEditUtil.mask(edt_nasc_cadastro_novoConvite, MaskEditUtil.FORMAT_DATE));


        bt_incluirConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erro = "";

                if (CheckStatus.isInternetConnected(CadastrarConvidadosActivity.this)) {

                    String nomeConvidado = edt_nomeConvidado_cadastroNovoConvite.getText().toString();
                    String rgConvidado = edt_rg_cadastroNovoConvite.getText().toString();
                    String nascConvidado = edt_nasc_cadastro_novoConvite.getText().toString();
                    String obsConvidado = edt_obs_cadastroNovoConvite.getText().toString();
                    String idade = getIdade(nascConvidado);

                    if (!idade.equals("-1") && rgConvidado.length() > 0 && nomeConvidado.length() > 0 && nascConvidado.length() > 0) {

                        convidado.setNome(nomeConvidado);
                        convidado.setDocumento(rgConvidado);
                        convidado.setNascimento(nascConvidado);
                        convidado.setIdade(idade);
                        convidado.setObservacao(obsConvidado);
                        //gera numero convite unico
                        SimpleDateFormat dateFormat = new SimpleDateFormat("ddHHmmss");
                        Date data = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(data);
                        Date data_atual = cal.getTime();
                        String data_completa = dateFormat.format(data_atual);
                        convidado.setNumeroConvite(data_completa);

                        alertaSimples("Adicionar convite para: ",
                                "\n" + nomeConvidado +
                                        "\n Número do Convite: " + data_completa +
                                        "\n RG: " + rgConvidado +
                                        "\n Nascimento: " + nascConvidado +
                                        "\n Idade: " + idade + " anos",
                                "OK",
                                "Voltar");
                    } else if (idade.equals("-1")) {
                        Toast.makeText(CadastrarConvidadosActivity.this, "Data de nascimento inválida. " + erro, Toast.LENGTH_LONG).show();
                    } else if (rgConvidado.length() == 0) {
                        Toast.makeText(CadastrarConvidadosActivity.this, "Documento do Convidado é obrigatório.", Toast.LENGTH_LONG).show();
                    } else if (nomeConvidado.length() == 0) {
                        Toast.makeText(CadastrarConvidadosActivity.this, "Nome do Convidado é obrigatório.", Toast.LENGTH_LONG).show();
                    } else if (nascConvidado.length() == 0) {
                        Toast.makeText(CadastrarConvidadosActivity.this, "Data de nascimento do Convidado é obrigatório.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CadastrarConvidadosActivity.this, "Erro no cadastro do convite: " + erro, Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(CadastrarConvidadosActivity.this, "Verifique a conexão com a internet", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @NonNull
    private String getIdade(String nascConvidado) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataNasc = null;
        int idade = -1;

        int diaNasc;
        int mesNasc;
        int anoNasc;

        if (nascConvidado.length() == 10) {

            diaNasc = Integer.parseInt(nascConvidado.substring(0, 2));
            mesNasc = Integer.parseInt(nascConvidado.substring(3, 5));


            if (mesNasc > 12) {
                erro = "Mês de nascimento não pode ser maior que 12!";
                return "-1";
            } else if (diaNasc <= 31) {
                if (diaNasc > 29 && mesNasc == 2) {
                    erro = "Dia do nascimento em fevereiro não pode ser maior que 29!";
                    return "-1";
                }
            } else {
                erro = "Dia do nascimento inválido!";
                return "-1";
            }
        } else {
            erro = "Data de nascimento inválida!";
            return "-1";
        }

        try {
            dataNasc = sdf.parse(nascConvidado);
            Calendar dataNascimento = Calendar.getInstance();
            dataNascimento.setTime(dataNasc);
            Calendar hoje = Calendar.getInstance();

            idade = hoje.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);

            if (idade >= 0) {

                if (hoje.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
                    idade--;
                } else {
                    if (hoje.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH) && hoje.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
                        idade--;
                    }
                }
            } else {
                erro = "Ano de nascimento nao pode ser maior que " + hoje.get(Calendar.YEAR);
                return "-1";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            erro = "Problema com a data de nascimento informada: " + nascConvidado + " . Erro = " + e.getMessage();
        }

        return (String.valueOf(idade));


    }


    private void alertaSimples(String titulo, String mensagem, String botaoOK, String botaoNOK) {

        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(CadastrarConvidadosActivity.this).inflate(R.layout.dialog_alerta, null);

        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
        txt_tirulo.setText(titulo);
        txt_mensagem.setText(mensagem);

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(CadastrarConvidadosActivity.this);
        builder.setView(view);
        //define um botão como positivo
        builder.setPositiveButton(botaoOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                exibirProgresso(true);
                verificaDocumento(convidado.getDocumento());
            }
        });
        //define um botão como negativo.
        if (botaoNOK != null) {
            builder.setNegativeButton(botaoNOK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
        }
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    private void salvarConvidado() {

        Preferencias preferencias = new Preferencias(this);
        convidado.setOrigem(preferencias.getUserName());
        convidado.setStatus("ok");
        convidado.setFuncionario(preferencias.getUserName());
        convidado.setConviteSendoLidoPor("-");

        convidado.salvarConvidadoFirebase(CadastrarConvidadosActivity.this);

    }


    private void verificaDocumento(final String doc) {
        //TODO QUANDO FOR ATUALIZAR VERIFICAR QUAL BASE DE DADOS VAI UTILIZAR, CONVERSAR COM A BIA COMO VAI FAZER, POR EXEMPLO SELECIONAR O EVENTO E FAZER UMA LISTA COM EVENTOS
        String basededados = preferencias.getEventoBaseDados();
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByChild("documento").equalTo(doc);

        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new connectAsyncTaskVerificaDoc(dataSnapshot, doc).execute();
                } else {
                    Toast.makeText(CadastrarConvidadosActivity.this, "Nenhum Convidado encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addListenerForSingleValueEvent(firebaseListener);

    }

    public void importarConvidados(View view) {
        Intent i = new Intent(CadastrarConvidadosActivity.this, TelaImportacaoXmlActivity.class);
//                i.putExtra("convidado" , convidados.get(position));
        startActivity(i);
        finish();
    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private class connectAsyncTaskVerificaDoc extends AsyncTask<Void, Integer, String> {
        DataSnapshot dataSnapshot;
        String doc;

        connectAsyncTaskVerificaDoc(DataSnapshot dataSnapshot, String doc) {
            this.dataSnapshot = dataSnapshot;
            this.doc = doc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Convidado convidado = ds.getValue(Convidado.class);

                if (convidado.getDocumento().equals(doc)) {
                    return "Documento já cadastrado para convite " + convidado.getNumeroConvite() + " " + convidado.getNome();

                } else
                    return "ok";
            }
            return "ok";
        }


        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (result.equals("ok")) {
                salvarConvidado();
                finish();
            } else {
                Toast.makeText(CadastrarConvidadosActivity.this, result, Toast.LENGTH_LONG).show();
            }


        }
    }


}
