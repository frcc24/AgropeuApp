package com.francocorrea.agropeuapp.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.config.ConfiguracaoFirebase;
import com.francocorrea.agropeuapp.helper.BancoDadosSQL;
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

public class ConferirConviteActivity extends AppCompatActivity {

    private final boolean convite_sendo_lido_em_outro_celular = false;
    ConstraintLayout lo_dadosConvidado;
    EditText editText_numConvite;
    TextView textView_nomeConvidado;
    TextView textView_parentesco;
    TextView textView_nascimento;
    TextView textView_observacao;
    TextView textView_rg;
    TextView textView_idade;
    TextView textView_origem;
    String numConvite;
    Convidado convidado;
    boolean encontrouConvite = false;
    boolean limparQuemLeu = true;
    Preferencias preferencias;
    ConstraintLayout mProgressBar;
    String userName;
    private ValueEventListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferir_convite);
        preferencias = new Preferencias(this);
        userName = preferencias.getUserName();

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        convidado = new Convidado();

        ImageButton bt_validaCodigo = findViewById(R.id.bt_validaCodigoConvite);
        ImageButton bt_limpar = findViewById(R.id.bt_limpar);
        ImageButton bt_confirmarEntrada = findViewById(R.id.bt_confirmaEntrada);

        lo_dadosConvidado = findViewById(R.id.lo_dadosConvidado);
        editText_numConvite = findViewById(R.id.editText_numConvite);
        textView_nomeConvidado = findViewById(R.id.txt_nome_convidado_convite);
        textView_parentesco = findViewById(R.id.txt_parentesco);
        textView_origem = findViewById(R.id.txt_origem);
        textView_nascimento = findViewById(R.id.txt_nascimento);
        textView_observacao = findViewById(R.id.txt_obs);
        textView_rg = findViewById(R.id.txt_rg);
        textView_idade = findViewById(R.id.txt_idade);
        TextView txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        limpaCampos();

        bt_validaCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                numConvite = editText_numConvite.getText().toString();
                if (numConvite.length() > 0) {

                    exibirProgresso(true);
                    // Check if no view has focus: Esconde o teclado apos apertar a lupa
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    lo_dadosConvidado.setVisibility(View.GONE);

                    if (preferencias.getModoAcesso().equals("offline")) {
                        buscarDadosDoConviteOFFLINE();
                    } else
                        buscarDadosDoConvite(1);
                } else {
                    Toast.makeText(ConferirConviteActivity.this, "Digite o numero do convite", Toast.LENGTH_LONG).show();
                }
            }
        });

        bt_limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaCampos();
            }
        });

        bt_confirmarEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numConvite = editText_numConvite.getText().toString();
                if (numConvite.equals(convidado.getNumeroConvite())) {
                    confirmarEntradaConvidado();
                } else {
                    Toast.makeText(ConferirConviteActivity.this, "O número do convite mudou, clique na lupa para atualizar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void buscarDadosDoConviteOFFLINE() {

        int intervalo_inicial = Integer.parseInt(preferencias.getIntervaloInicial());
        int intervalo_final = Integer.parseInt(preferencias.getIntervaloFinal());
        int iNumConvite = Integer.parseInt(numConvite);

        if ((intervalo_inicial <= iNumConvite) && (iNumConvite <= intervalo_final)) {

            BancoDadosSQL banco = new BancoDadosSQL(this, "Banco2", 1);

            Cursor c = banco.getWritableDatabase().rawQuery("SELECT id_num_convite, nome, nascimento, observacao, origem, documento, idade, status, horarioChegada " +
                    "FROM convidados_tbl where id_num_convite = " + numConvite, null);

            while (c.moveToNext()) {

                if (!c.getString(7).equals("presente")) { // se o convidado não esta presente
                    convidado.setNumeroConvite(numConvite);
                    convidado.setNome(c.getString(1));
                    convidado.setNascimento(c.getString(2));
                    convidado.setObservacao(c.getString(3));
                    convidado.setOrigem(c.getString(4));
                    convidado.setDocumento(c.getString(5));

                    String idade = getIdade(c.getString(2));
                    convidado.setIdade(idade);
                    convidado.setStatus(c.getString(7));

                    textView_nomeConvidado.setText(convidado.getNome());
                    textView_nascimento.setText(convidado.getNascimento());
                    textView_observacao.setText(convidado.getObservacao());
                    textView_parentesco.setText("Origem: " + convidado.getOrigem());
                    textView_origem.setText("Resp: " + convidado.getFuncionario());
                    textView_rg.setText("Doc: " + convidado.getDocumento());
                    textView_idade.setText(convidado.getIdade() + " anos");

                    lo_dadosConvidado.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog alerta;

                    View view;
                    view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_alerta, null);

                    TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                    TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                    txt_tirulo.setText("Alerta!");
                    txt_mensagem.setText("Convidado:" +
                            "\n\n" + c.getString(1) + // nome do convidado
                            "\n\n Presente desde: " + c.getString(8)); // horario de chegada
                    txt_mensagem.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    //Cria o gerador do AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(view);

                    builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
                }
            }

            c.close();
        } else {
            Toast.makeText(this, "Número do convite fora do intervalo selecionado para esse telefone.", Toast.LENGTH_LONG).show();
        }

        exibirProgresso(false);

    }

    private void confirmarEntradaConvidado() {

        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_alerta, null);

        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
        txt_tirulo.setText("Confirma a entrada de:");
        txt_mensagem.setText(convidado.getNome() +
                "\n\nConvite número: " + convidado.getNumeroConvite() +
                "\nDocumento: " + convidado.getDocumento() +
                "\nIDADE: " + convidado.getIdade() + " anos");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);
        builder.setView(view);

        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                exibirProgresso(true);


                if (preferencias.getModoAcesso().equals("offline")) {
                    convidado.setStatus("presente");
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                    Date currentTime = Calendar.getInstance().getTime();
                    String todayString = formatter.format(currentTime);
                    convidado.setHorarioChegada(todayString);

                    BancoDadosSQL banco = new BancoDadosSQL(ConferirConviteActivity.this, "Banco2", 1);
                    ContentValues contentValues = new ContentValues();
//                    contentValues.put("id_num_convite", convidado.getNumeroConvite());
//                    contentValues.put("nome", convidado.getNome());
//                    contentValues.put("documento", convidado.getDocumento());
//                    contentValues.put("origem", convidado.getOrigem());
//                    contentValues.put("nascimento", convidado.getNascimento());
//                    contentValues.put("idade", convidado.getIdade());
//                    contentValues.put("funcionario", convidado.getFuncionario());
//                    contentValues.put("observacao", convidado.getObservacao());
                    contentValues.put("status", convidado.getStatus());
                    contentValues.put("horarioChegada", todayString);
                    contentValues.put("conviteSendoLidoPor", "-");
                    long id = banco.getWritableDatabase().update("convidados_tbl", contentValues, "id_num_convite = " + numConvite, null);

                    if (id != -1) {
                        final AlertDialog alerta;
                        View view;
                        view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_entrada_ok, null);
                        Button btn_ok = view.findViewById(R.id.bt_ok_alerta);
                        //Cria o gerador do AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);
                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(view);
                        //define um botão como positivo
                        //cria o AlertDialog
                        alerta = builder.create();
                        //Exibe
                        alerta.show();
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alerta.dismiss();
                            }
                        });
                    } else {
                        AlertDialog alerta;

                        View view;
                        view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_alerta, null);

                        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                        txt_tirulo.setText("Alerta!");
                        txt_mensagem.setText("Erro ao salvar dados no banco");
                        txt_mensagem.setGravity(View.TEXT_ALIGNMENT_CENTER);

                        //Cria o gerador do AlertDialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        builder.setView(view);

                        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        //cria o AlertDialog
                        alerta = builder.create();
                        //Exibe
                        alerta.show();
                    }


                    limpaCampos();

                } else {
                    buscarDadosDoConvite(2);
                }
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseListener != null) {
            firebaseListener = null;
        }
    }

    private void limpaCampos() {
        lo_dadosConvidado.setVisibility(View.GONE);
        editText_numConvite.setText("");
        exibirProgresso(false);
    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void buscarDadosDoConvite(final int tipo) {

        String basededados = preferencias.getEventoBaseDados();
        //    Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).orderByKey().equalTo(numConvite);
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).orderByKey();
        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    if (tipo == 1)
                        new connectAsyncTaskBuscarDadosConvite(dataSnapshot, numConvite).execute();
                    else if (tipo == 2)
                        new connectAsyncTaskVerificaLeitura(dataSnapshot, numConvite).execute();
                } else {
                    Toast.makeText(ConferirConviteActivity.this, "Erro ao procurar convite ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        firebase.addListenerForSingleValueEvent(firebaseListener);
    }

    private void marcarLeituraConvite() {
        convidado.setConviteSendoLidoPor(userName);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();

        String todayString = formatter.format(currentTime);

        convidado.setHorarioChegada(todayString);
        //salva no firebase que o usuario está lendo o convite
        convidado.salvarConvidadoFirebase(this);
    }

    @Override
    public void onBackPressed() {
        confirmaSaidaTela();
    }

    private void confirmaSaidaTela() {
        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(this).inflate(R.layout.dialog_alerta, null);

        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
        txt_tirulo.setText("Alerta!");
        txt_mensagem.setText("Deseja voltar para menu principal?");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);


        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();

    }

    @NonNull
    private String getIdade(String nascConvidado) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataNasc = null;
        int idade = -1;


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
                return "-1";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (String.valueOf(idade));


    }

    private class connectAsyncTaskVerificaLeitura extends AsyncTask<Void, Void, String> {
        DataSnapshot dataSnapshot;
        String numeroConvite;

        connectAsyncTaskVerificaLeitura(DataSnapshot dataSnapshot, String numeroConvite) {
            this.dataSnapshot = dataSnapshot;
            this.numeroConvite = numeroConvite;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            for (DataSnapshot ds : dataSnapshot.child("CONVIDADOS").getChildren()) {

                Convidado convidadoAux = ds.getValue(Convidado.class);

                if (numeroConvite.equals(convidadoAux.getNumeroConvite())) {
                    convidado = convidadoAux;

                    //verifica se o convidado ainda nao está presente
                    if (!convidado.getStatus().equals("presente")) {
                        if (convidado.getConviteSendoLidoPor().equals(userName)) {
                            convidado.setStatus("presente");
                            convidado.salvarConvidadoFirebase(ConferirConviteActivity.this);
                            return "ok";
                        } else {
                            return "Convite sendo lido por " + convidado.getConviteSendoLidoPor();
                        }
                    } else if (convidado.getStatus().equals("presente")) {
                        return convidado.getNome() +
                                "\n\nPresente desde: " +
                                convidado.getHorarioChegada() +
                                "\n\nLido por: " + convidado.getConviteSendoLidoPor();
                    }
                }
            }
            return "Não encontrou convite";
        }

        @Override
        protected void onPostExecute(String result) {
            limpaCampos();
            if (!result.equals("ok")) {
                AlertDialog alerta;
                View view;
                view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_alerta, null);

                TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                txt_tirulo.setText("Alerta!");
                txt_mensagem.setText(result);
                txt_mensagem.setGravity(View.TEXT_ALIGNMENT_CENTER);

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view);

                builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            } else {
                final AlertDialog alerta;
                View view;
                view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_entrada_ok, null);
                Button btn_ok = view.findViewById(R.id.bt_ok_alerta);
                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view);
                //define um botão como positivo
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alerta.dismiss();
                    }
                });
            }
        }
    }

    private class connectAsyncTaskBuscarDadosConvite extends AsyncTask<Void, Void, String> {
        DataSnapshot dataSnapshot;
        String numeroConvite;

        connectAsyncTaskBuscarDadosConvite(DataSnapshot dataSnapshot, String numeroConvite) {
            this.dataSnapshot = dataSnapshot;
            this.numeroConvite = numeroConvite;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            for (DataSnapshot ds : dataSnapshot.child("CONVIDADOS").getChildren()) {

                Convidado convidadoAux = ds.getValue(Convidado.class);

                if (numeroConvite.equals(convidadoAux.getNumeroConvite())) {
                    convidado = convidadoAux;

                    //verifica se o convidado ainda nao está presente
                    if (!convidado.getStatus().equals("presente") && !convidado.getStatus().equalsIgnoreCase("inativo")) {

                        if (!convidado.getConviteSendoLidoPor().equals(userName) && !convidado.getConviteSendoLidoPor().equals("-") && !convidado.getConviteSendoLidoPor().equals("")) {

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                            String dateInString = convidado.getHorarioChegada();

                            try {
                                if (dateInString != null && dateInString.length() > 0) {
                                    Date currentTime = (Calendar.getInstance().getTime());
                                    String dataAtual = formatter.format(currentTime);
                                    currentTime = formatter.parse(dataAtual);

                                    Date date = formatter.parse(dateInString);
                                    long dataNova = currentTime.getTime();
                                    long dataConvite = date.getTime();

                                    long comparacaoDasDatas = dataNova - dataConvite;

                                    if (comparacaoDasDatas < 180000) //3 min = 3 x 60000 milisegungos
                                    {
                                        return "Convite sendo lido por " + convidado.getConviteSendoLidoPor();
                                    } else {
                                        marcarLeituraConvite();
                                        return "ok";
                                    }
                                } else {
                                    marcarLeituraConvite();
                                    return "ok";
                                }

                            } catch (Exception e) {
                                return "Erro: " + e.getMessage();
                            }
                        } else {
                            marcarLeituraConvite();
                            return "ok";
                        }
                    } else if (convidado.getStatus().equals("presente")) {
                        return convidado.getNome() +
                                "\n\nPresente desde: " +
                                convidado.getHorarioChegada() +
                                "\n\nLido por: " + convidado.getConviteSendoLidoPor();
                    } else if (convidado.getStatus().equalsIgnoreCase("inativo")) {
                        return "Convite está inativo";
                    }
                }
            }
            return "Não encontrou convite";
        }

        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);

            if (!result.equals("ok")) {
                AlertDialog alerta;

                View view;
                view = LayoutInflater.from(ConferirConviteActivity.this).inflate(R.layout.dialog_alerta, null);

                TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                txt_tirulo.setText("Alerta!");
                txt_mensagem.setText(result);
                txt_mensagem.setGravity(View.TEXT_ALIGNMENT_CENTER);

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ConferirConviteActivity.this);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view);

                builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            } else {
                textView_nomeConvidado.setText(convidado.getNome());
                textView_nascimento.setText(convidado.getNascimento());
                textView_observacao.setText(convidado.getObservacao());
                textView_parentesco.setText("Origem: " + convidado.getOrigem());
                textView_origem.setText("Resp: " + convidado.getFuncionario());
                textView_rg.setText("Doc: " + convidado.getDocumento());

                String idade = getIdade(convidado.getNascimento());
                convidado.setIdade(idade);

                textView_idade.setText(convidado.getIdade() + " anos");

                lo_dadosConvidado.setVisibility(View.VISIBLE);
            }


        }


    }


}
