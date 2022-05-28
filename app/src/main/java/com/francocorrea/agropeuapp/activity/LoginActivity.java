package com.francocorrea.agropeuapp.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.francocorrea.agropeuapp.helper.Base64Custom;
import com.francocorrea.agropeuapp.helper.CheckStatus;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout mProgressBar;
    Preferencias preferencias;
    EditText editText_senha;
    EditText editText_email;
    Usuario usuario = new Usuario();
    private DatabaseReference firebase;
    private FirebaseAuth auth;
    private ValueEventListener firebaseUsuariosListener;
    private ValueEventListener firebaseEventosListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebase = ConfiguracaoFirebase.getFirebase();
        preferencias = new Preferencias(LoginActivity.this);

        Button bt_login = findViewById(R.id.bt_login);
        editText_senha = findViewById(R.id.editText_senha_login);
        editText_email = findViewById(R.id.editText_email_login);
        TextView txt_version = findViewById(R.id.txt_version);
        String version = "";
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        txt_version.setText("Versão " + version);


        editText_email.setText(preferencias.getUserName());

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        editText_senha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login();
                }
                return false;
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = editText_email.getText().toString();
        String senha = editText_senha.getText().toString();

        if (senha.length() > 0 && email.length() > 0) {
            exibirProgresso(true);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setId(Base64Custom.codificarBase64(email));

            if (CheckStatus.isInternetConnected(this)) {
                //todo checar pra ver se já nao iniciou em modo offline? talvez pensar em um modo de sincronizar o offline caso a internet voltar
                //if modo de acesso anterior = offlnine fazer uma atualizazao no bd
                preferencias.salvarModoAcesso("online");
                loginUsuario(usuario);
            } else {
                exibirProgresso(false);
                AlertDialog alerta;

                View view;
                view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_alerta, null);

                TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                txt_tirulo.setText("Alerta!");
                //todo retirar a parte de fase de testes
                txt_mensagem.setText("Sem conexão com a internet. Deseja iniciar o modo OFFLINE? (ainda em fase de testes, pode ocasionar erros)");
                txt_mensagem.setGravity(View.TEXT_ALIGNMENT_CENTER);

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(view);

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        preferencias.salvarModoAcesso("offline");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                //cria o AlertDialog
                alerta = builder.create();
                //Exibe
                alerta.show();

            }

        } else {
            Toast.makeText(LoginActivity.this, "Usuário e senha devem ser preenchidos", Toast.LENGTH_LONG).show();
        }
    }

    private void loginUsuario(final Usuario usuario) {

        auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    usuario.salvarUsuerFireBase();

                    getUserDetails(usuario.getEmail());


                } else {
                    String erro = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Senha fraca!";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Senha incorreta ou usuário ainda não possui senha cadastrada.";
                    } catch (FirebaseAuthInvalidUserException e) {
                        erro = "Não existe este usuário na base de dados.";

                    } catch (Exception e) {
                        Log.e("AgropeuAPP", e.getMessage());
                    }


                    AlertDialog alerta;
                    View view;
                    view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_alerta, null);

                    TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                    TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                    txt_tirulo.setText("Erro ao efetuar Login");
                    txt_mensagem.setText(erro);

                    //Cria o gerador do AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setView(view);

                    builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

                    //cria o AlertDialog
                    alerta = builder.create();
                    //Exibe
                    alerta.show();
                    exibirProgresso(false);
                }

            }
        });

    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }


    private void getUserDetails(final String email) {

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebase()
                .child("USUARIOS");

        firebaseUsuariosListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new connectAsyncTaskLogin(dataSnapshot, email).execute();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addListenerForSingleValueEvent(firebaseUsuariosListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseUsuariosListener != null) {
            firebaseUsuariosListener = null;
        }
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

    private class connectAsyncTaskLogin extends AsyncTask<Void, Void, String> {


        DataSnapshot dataSnapshot;
        String username;

        connectAsyncTaskLogin(DataSnapshot dataSnapshot, String username) {
            this.dataSnapshot = dataSnapshot;
            this.username = username;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {


            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Usuario usuarioAux = ds.getValue(Usuario.class);

                if (usuarioAux.getEmail().equals(username)) {
                    usuario.setNivelAcesso(usuarioAux.getNivelAcesso());
                    return "ok";
                }

            }

            return "1acesso";


        }

        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);

            if (!result.equals("ok")) {
                if (result.equals("1acesso")) {
                    usuario.setNivelAcesso("1");
                    preferencias.salvarUserName(usuario.getEmail());
                    preferencias.salvarAcesso("1");
                    usuario.salvarUsuerFireBase();

//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    startActivity(new Intent(LoginActivity.this, TelaEventosActivity.class));
                    finish();
                } else {

                    AlertDialog alerta;
                    View view;
                    view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_alerta, null);

                    TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                    TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                    txt_tirulo.setText("Problema ao efetuar Login");
                    txt_mensagem.setText(result);

                    //Cria o gerador do AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
            } else {

                preferencias.salvarUserName(usuario.getEmail());
                preferencias.salvarAcesso(usuario.getNivelAcesso());


                startActivity(new Intent(LoginActivity.this, TelaEventosActivity.class));
                finish();
            }
        }
    }

}
