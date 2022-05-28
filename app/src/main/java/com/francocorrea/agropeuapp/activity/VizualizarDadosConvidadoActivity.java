package com.francocorrea.agropeuapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.helper.NivelAcesso;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Convidado;

public class VizualizarDadosConvidadoActivity extends AppCompatActivity {

    TextView txt_num_Convite;
    TextView txt_nome_convidado;
    TextView txt_doc_convidado;
    TextView txt_responsavel;
    TextView txt_nasc;
    TextView txt_observacao;
    TextView txt_hora_entrada;
    TextView txt_status_convite;
    EditText editText_nome;
    EditText editText_doc_convidado;
    EditText editText_responsavel;
    EditText editText_nasc;
    EditText editText_observacao;
    ImageButton bt_editar;
    Button bt_salvar;
    Switch switch_inativo;
    Preferencias preferencias;
    ProgressBar mProgressBar;
    private Convidado convidado = new Convidado();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vizualizar_dados_convidado);

        convidado = (Convidado) getIntent().getSerializableExtra("convidado");
        preferencias = new Preferencias(this);

        mProgressBar = findViewById(R.id.progress_bar_vizualiza);
        exibirProgresso(false);
        TextView txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        txt_num_Convite = findViewById(R.id.txt_numConviteGrande);
        txt_nome_convidado = findViewById(R.id.txt_nome_convidado);
        txt_doc_convidado = findViewById(R.id.txt_num_doc);
        txt_responsavel = findViewById(R.id.txt_responsavel);
        txt_nasc = findViewById(R.id.txt_nasc);
        txt_observacao = findViewById(R.id.txt_obs);
        txt_hora_entrada = findViewById(R.id.txt_hora_entrada);
        txt_status_convite = findViewById(R.id.txt_status_convite);
        switch_inativo = findViewById(R.id.switch_inativo);
        txt_hora_entrada.setVisibility(View.GONE);
        switch_inativo.setVisibility(View.GONE);

        editText_nome = findViewById(R.id.editText_nome);
        editText_doc_convidado = findViewById(R.id.editText_documento);
        editText_responsavel = findViewById(R.id.editText_responsavel);
        editText_nasc = findViewById(R.id.editText_nascimento);
        editText_observacao = findViewById(R.id.editText_observacao);

        editText_nome.setVisibility(View.GONE);
        editText_doc_convidado.setVisibility(View.GONE);
        editText_responsavel.setVisibility(View.GONE);
        editText_nasc.setVisibility(View.GONE);
        editText_observacao.setVisibility(View.GONE);

        bt_editar = findViewById(R.id.button_editar);
        bt_salvar = findViewById(R.id.button_salvar);

        bt_editar.setVisibility(View.GONE);
        bt_salvar.setVisibility(View.GONE);


        bt_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editText_nome.setText(convidado.getNome());
                editText_doc_convidado.setText(convidado.getDocumento());
                editText_responsavel.setText(convidado.getFuncionario());
                editText_nasc.setText(convidado.getNascimento());
                editText_observacao.setText(convidado.getObservacao());

                editText_nome.setVisibility(View.VISIBLE);
                editText_doc_convidado.setVisibility(View.VISIBLE);
                editText_responsavel.setVisibility(View.VISIBLE);
                editText_nasc.setVisibility(View.VISIBLE);
                editText_observacao.setVisibility(View.VISIBLE);
                switch_inativo.setVisibility(View.VISIBLE);

                bt_salvar.setVisibility(View.VISIBLE);
                bt_editar.setVisibility(View.GONE);
                txt_status_convite.setVisibility(View.GONE);


            }
        });


        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                convidado.setNome(editText_nome.getText().toString());
                convidado.setDocumento(editText_doc_convidado.getText().toString());
                convidado.setFuncionario(editText_responsavel.getText().toString());
                convidado.setNascimento(editText_nasc.getText().toString());
                convidado.setObservacao(editText_observacao.getText().toString());

                if (switch_inativo.isChecked()) {
                    convidado.setStatus("inativo");
                    txt_status_convite.setText("inativo");
                } else {
                    //todo tratar o status caso volte a ser diferente de inativo
                    convidado.setStatus("ok");
                    txt_status_convite.setText("ok");
                }


                convidado.salvarConvidadoFirebase(VizualizarDadosConvidadoActivity.this);

                txt_num_Convite.setText(convidado.getNumeroConvite());

                txt_nome_convidado.setText(convidado.getNome());
                txt_doc_convidado.setText(convidado.getDocumento());
                txt_responsavel.setText(convidado.getFuncionario());
                txt_nasc.setText(convidado.getNascimento());
                txt_observacao.setText(convidado.getObservacao());

                editText_nome.setVisibility(View.GONE);
                editText_doc_convidado.setVisibility(View.GONE);
                editText_responsavel.setVisibility(View.GONE);
                editText_nasc.setVisibility(View.GONE);
                editText_observacao.setVisibility(View.GONE);
                switch_inativo.setVisibility(View.GONE);

                bt_editar.setVisibility(View.VISIBLE);
                txt_status_convite.setVisibility(View.VISIBLE);
                bt_salvar.setVisibility(View.GONE);


            }
        });
        exibirProgresso(true);

        getConvidado();
    }


    private void getConvidado() {

        if (preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_04) ||
                preferencias.getNivelAcesso().equals(NivelAcesso.NIVEL_10)) {

            bt_editar.setVisibility(View.VISIBLE);
        }

        txt_num_Convite.setText(convidado.getNumeroConvite());
        txt_nome_convidado.setText(convidado.getNome());
        txt_doc_convidado.setText(convidado.getDocumento());
        txt_responsavel.setText(convidado.getFuncionario());
        txt_nasc.setText(convidado.getNascimento());
        txt_observacao.setText(convidado.getObservacao());

        txt_status_convite.setText(convidado.getStatus());

        if (convidado.getStatus().equals("presente")) {
            txt_hora_entrada.setText("Chegou as " + convidado.getHorarioChegada() + " pelo segurança " + convidado.getConviteSendoLidoPor());
            txt_hora_entrada.setVisibility(View.VISIBLE);
            switch_inativo.setChecked(false);
        } else switch_inativo.setChecked(convidado.getStatus().equals("inativo"));

        exibirProgresso(false);
    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }


}
