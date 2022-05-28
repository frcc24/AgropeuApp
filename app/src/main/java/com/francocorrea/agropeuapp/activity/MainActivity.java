package com.francocorrea.agropeuapp.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.helper.BancoDadosSQL;
import com.francocorrea.agropeuapp.helper.CheckStatus;
import com.francocorrea.agropeuapp.helper.NivelAcesso;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Evento;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Context context;
    ConstraintLayout mProgressBar;
    Preferencias preferencias;
    TextView txtNomeEvento;
    private Evento evento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });


        context = this;
        preferencias = new Preferencias(this);

        evento = (Evento) getIntent().getSerializableExtra("evento");

        ImageButton bt_irTelaRegistroDeEntrada = findViewById(R.id.bt_irTelaRegistroDeEntrada);
        ImageButton bt_irTelaCadastroNovoConvite = findViewById(R.id.bt_irTelaCadastroNovoConvite);
        ImageButton bt_meusConvidados = findViewById(R.id.bt_meusConvidados);
        ImageButton bt_irTelaVerQuemChegou = findViewById(R.id.bt_ver_quem_chegou);
        Button bt_menu_franco = findViewById(R.id.button_franco);
        final TextView txt_offline = findViewById(R.id.txt_offline);
        txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        txtNomeEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //so permite editar evento determinados niveis de usuario
                String nivelAcesso = preferencias.getNivelAcesso();
                if (nivelAcesso.equals(NivelAcesso.NIVEL_03) ||
                        nivelAcesso.equals(NivelAcesso.NIVEL_04) ||
                        nivelAcesso.equals(NivelAcesso.NIVEL_10)) {


                    Intent i = new Intent(MainActivity.this, TelaEdicaoEventoActivity.class);
                    i.putExtra("evento", evento);
                    startActivity(i);


                } else {
                    Toast.makeText(MainActivity.this, "Acesso restrito! (E: NA:" + nivelAcesso + ")", Toast.LENGTH_LONG).show();
                }

            }
        });

        if (preferencias.getModoAcesso().equals("offline")) {
            txt_offline.setVisibility(View.VISIBLE);

            alertSelecionarIntervalo();

        }

        bt_menu_franco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GerenciadorActivity.class));
            }
        });


        if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_01) || preferencias.getModoAcesso().equals("offline")) { // acesso apenas ao checkin de convidados
            bt_irTelaCadastroNovoConvite.setVisibility(View.GONE);
            bt_irTelaVerQuemChegou.setVisibility(View.GONE);
            bt_meusConvidados.setVisibility(View.GONE);
            bt_menu_franco.setVisibility(View.GONE);

        } else if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_02)) { // acesso de seguranca que pode ver a lista de quem chegou
            bt_irTelaCadastroNovoConvite.setVisibility(View.GONE);
//            bt_irTelaVerQuemChegou.setVisibility(View.GONE);
            bt_meusConvidados.setVisibility(View.GONE);
            bt_menu_franco.setVisibility(View.GONE);
        } else if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_03)) { // acesso a tudo menos edicao de convidados
            bt_menu_franco.setVisibility(View.GONE);
            bt_irTelaRegistroDeEntrada.setVisibility(View.GONE);
        } else if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_04)) { // acesso a tudo menos os botoes do franco
            bt_menu_franco.setVisibility(View.GONE);
        } else if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_10)) { //acesso a tudo (somente franco)
            bt_irTelaCadastroNovoConvite.setVisibility(View.VISIBLE);
            bt_irTelaVerQuemChegou.setVisibility(View.VISIBLE);
            bt_meusConvidados.setVisibility(View.VISIBLE);
//            bt_lerExcel.setVisibility(View.VISIBLE);
//            button_offline.setVisibility(View.VISIBLE);
        } else if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_025)) { // acesso a criar convites e ver seus convidados
            bt_irTelaCadastroNovoConvite.setVisibility(View.VISIBLE);
            bt_meusConvidados.setVisibility(View.VISIBLE);
            bt_irTelaRegistroDeEntrada.setVisibility(View.GONE);
            bt_irTelaVerQuemChegou.setVisibility(View.GONE);
            bt_menu_franco.setVisibility(View.GONE);
        }


        bt_meusConvidados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckStatus.isInternetConnected(MainActivity.this)) {
                    startActivity(new Intent(MainActivity.this, ListaMeusConvidadosActivity.class));
                } else {
                    exibirProgresso(false);
                    Toast.makeText(MainActivity.this, "Verifique a conexão com a internet", Toast.LENGTH_LONG).show();
                }

            }
        });

        bt_irTelaRegistroDeEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataAnteriorAHoje(evento.getDataEvento())) {
                    if (preferencias.getModoAcesso().equals("offline")) {
                        startActivity(new Intent(MainActivity.this, ConferirConviteActivity.class));
                    } else if (CheckStatus.isInternetConnected(MainActivity.this)) {
                        startActivity(new Intent(MainActivity.this, ConferirConviteActivity.class));
                    } else {
                        exibirProgresso(false);
                        Toast.makeText(MainActivity.this, "Verifique a conexão com a internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Acesso não permitido! Evento já realizado.", Toast.LENGTH_LONG).show();
                }
            }
        });


        bt_irTelaCadastroNovoConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CadastrarConvidadosActivity.class));
            }
        });

        bt_irTelaVerQuemChegou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerQuemChegouActivity.class));
            }
        });

    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void alertSelecionarIntervalo() {
        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_selecao_intervalo, null);

        final RadioButton rb1 = view.findViewById(R.id.radioButton);
        final RadioButton rb2 = view.findViewById(R.id.radioButton2);
        final RadioButton rb3 = view.findViewById(R.id.radioButton3);
        final RadioButton rb4 = view.findViewById(R.id.radioButton4);
        final RadioButton rb5 = view.findViewById(R.id.radioButton5);

        BancoDadosSQL banco = new BancoDadosSQL(MainActivity.this, "Banco2", 1);


        String countQuery = "SELECT  * FROM " + "convidados_tbl";

        Cursor cursor = banco.getReadableDatabase().rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        final String r1_inicial = "1";
        final String r1_final = String.valueOf(count / 5);
        rb1.setText(r1_inicial + " a " + r1_final);

        final String r2_inicial = String.valueOf((Integer.parseInt(r1_final)) + 1);
        final String r2_final = String.valueOf((count / 5) * 2);
        rb2.setText(r2_inicial + " a " + r2_final);

        final String r3_inicial = String.valueOf((Integer.parseInt(r2_final) + 1));
        final String r3_final = String.valueOf((count / 5) * 3);
        rb3.setText(r3_inicial + " a " + r3_final);

        final String r4_inicial = String.valueOf((Integer.parseInt(r3_final) + 1));
        final String r4_final = String.valueOf((count / 5) * 4);
        rb4.setText(r4_inicial + " a " + r4_final);

        //todo corrigir erro, nao pode pegar a quantidade, tem q buscar o ultimo convite ou deixar utilizar qq numeracao
        final String r5_inicial = String.valueOf((Integer.parseInt(r4_final) + 1));
        final String r5_final = String.valueOf(count);
        rb5.setText(r5_inicial + " a " + r5_final);


        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                if (rb1.isChecked()) {
                    preferencias.salvarIntervaloInicial(r1_inicial);
                    preferencias.salvarIntervaloFinal(r1_final);
                } else if (rb2.isChecked()) {
                    preferencias.salvarIntervaloInicial(r2_inicial);
                    preferencias.salvarIntervaloFinal(r2_final);
                } else if (rb3.isChecked()) {
                    preferencias.salvarIntervaloInicial(r3_inicial);
                    preferencias.salvarIntervaloFinal(r3_final);
                } else if (rb4.isChecked()) {
                    preferencias.salvarIntervaloInicial(r4_inicial);
                    preferencias.salvarIntervaloFinal(r4_final);
                } else if (rb5.isChecked()) {
                    preferencias.salvarIntervaloInicial(r5_inicial);
                    preferencias.salvarIntervaloFinal(r5_final);
                } else {
                    Toast.makeText(MainActivity.this, "Selecione um intervalo!", Toast.LENGTH_LONG).show();
                    alertSelecionarIntervalo();
                }


            }
        });

        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }


    @Override
    public void onBackPressed() {
        confirmaSaidaApp();
    }

    private void confirmaSaidaApp() {

        AlertDialog alerta;
        View view;
        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_alerta, null);

        TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
        TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
        txt_tirulo.setText("Voltar?");
        txt_mensagem.setText("Deseja voltar para tela de seleção de eventos?");

        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        //define um botão como positivo
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent i = new Intent(MainActivity.this, TelaEventosActivity.class);
//                i.putExtra("convidado" , convidados.get(position));
                startActivity(i);
                finish();

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

    public void goToPolitics(View v) {
        String URL_GLOBAL = "https://frcc248.wixsite.com/website";
//                new connectAsyncTaskLigaMagic(URL_GLOBAL).execute();
        try {
            Intent i = new Intent("android.intent.action.MAIN");
            i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
            i.addCategory("android.intent.category.LAUNCHER");
            i.setData(Uri.parse(URL_GLOBAL));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // Toast.makeText(SugestoesActivity.this, "Nao foi possivel iniciar Activity. [SAC-04]", Toast.LENGTH_LONG).show();
            // Chrome is not installed
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_GLOBAL));
            startActivity(i);
        }
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
    protected void onStart() {
        super.onStart();
        txtNomeEvento.setText(preferencias.getEventoBaseDados());
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtNomeEvento.setText(preferencias.getEventoBaseDados());
    }
}
