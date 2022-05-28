package com.francocorrea.agropeuapp.activity;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GerenciadorActivity extends AppCompatActivity {

    BancoDadosSQL banco;
    ConstraintLayout mProgressBar;
    Convidado convidado;
    TextView txt_path;
    Preferencias preferencias;
    String nomeEvento;
    private ValueEventListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciador);

        convidado = new Convidado();

        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });
        preferencias = new Preferencias(this);
        nomeEvento = preferencias.getEventoBaseDados();
        TextView txtNomeEvento = findViewById(R.id.txt_nome_evento);
        txtNomeEvento.setText(nomeEvento);

        //Construção do banco
        banco = new BancoDadosSQL(this, "Banco2", 1);

        Button bt_clear = findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alerta;
                View view;
                view = LayoutInflater.from(GerenciadorActivity.this).inflate(R.layout.dialog_alerta, null);

                TextView txt_mensagem = view.findViewById(R.id.txt_mensagem_alerta);
                TextView txt_tirulo = view.findViewById(R.id.txt_titulo_alerta);
                txt_tirulo.setText("Excluir todos os Convidados");
                txt_mensagem.setText("Deseja realmente Excluir todos os Convidados?");

                //Cria o gerador do AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(GerenciadorActivity.this);
                builder.setView(view);
                //define um botão como positivo
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        convidado.removerTODOSConvidados();

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
        });


    }

    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }


//    public void LeituraArquivoExcel(View v){
//        exibirProgresso(true);
//        verifyStoragePermissions(this);
//    }
//    /**
//     * Checks if the app has permission to write to device storage
//     *
//     * If the app does not has permission then the user will be prompted to grant permissions
//     *
//     * @param activity
//     */
//    @TargetApi(Build.VERSION_CODES.M)
//    public void verifyStoragePermissions(Activity activity) {
//
//        int permissionCheck = ContextCompat.checkSelfPermission(
//                this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        int permissionCheck2 = ContextCompat.checkSelfPermission(
//                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED ||
//                permissionCheck2 != PackageManager.PERMISSION_GRANTED ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            {
//                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_Access_STORAGE);
//
//            } else {
//                requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_Access_STORAGE);
//            }
//        } else {
//            Toast.makeText(GerenciadorActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
//
//            new asyncTaskLerExcel("").execute();
//        }
//    }
//
//    private void requestPermission(String permissionName, int permissionRequestCode) {
//        ActivityCompat.requestPermissions(this,
//                new String[]{permissionName, Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCode);
//    }
//
//    private void showExplanation(String title,
//                                 String message,
//                                 final String permission,
//                                 final int permissionRequestCode) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(title)
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.M)
//                    public void onClick(DialogInterface dialog, int id) {
//                        requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_Access_STORAGE);
//
//                    }
//                });
//        AlertDialog d = builder.create();
//        d.show();
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(
//            int requestCode,
//            String permissions[],
//            int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_PERMISSION_Access_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(GerenciadorActivity.this, "Permission READ Granted!", Toast.LENGTH_SHORT).show();
//              //todo descomentar
//                    //      new asyncTaskLerExcel("").execute();
//
//                } else {
//                    Toast.makeText(GerenciadorActivity.this, "Permission READ Denied!", Toast.LENGTH_SHORT).show();
//                }
//
//
//                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(GerenciadorActivity.this, "Permission WRITE Granted!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(GerenciadorActivity.this, "Permission WRITE Denied!", Toast.LENGTH_SHORT).show();
//                }
//
//
//                break;
//
//        }
//    }


//    public void lerExcel( ){
//
//
//        try {
//            // Creating a Workbook from an Excel file (.xls or .xlsx)
//            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ){
//                Log.e("AgropeuLog", "(Environment.MEDIA_MOUNTED() = ok");
//
//            }
//            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY) ){
//                Log.e("AgropeuLog", "(Environment.MEDIA_MOUNTED_READ_ONLY() = ok");
//
//            }
//
//            File extStore = Environment.getExternalStorageDirectory();
////            File fileName = new File(extStore.getAbsolutePath() + "/agropeu.xls");
//            String pathname = txt_path.getText().toString();
//            String pathCerto = pathname.replaceFirst("/document/raw:", "");
//            //File myFile = new File("/storage/8523-1C08/backup/agropeu3.xls");
//            File myFile = new File(pathCerto);
//
//
//
//            if(myFile.exists()){
//                Log.e("AgropeuLog", "file.exists " + myFile.getPath());
//            }
//            else {
//                String folder_main = "agropeu";
//
//                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//                if (!f.exists()) {
//                    f.mkdirs();
//                }
//
//                Log.e("AgropeuLog", "nao exists " + myFile.getAbsolutePath());
//            }
//
//
//            InputStream inputFS = new FileInputStream(myFile);
//            Workbook workbook = WorkbookFactory.create(inputFS);
//
//
//            // Getting the Sheet at index zero
//            Sheet sheet = workbook.getSheetAt(0);
//
//            // Create a DataFormatter to format and get each cell's value as String
//            DataFormatter dataFormatter = new DataFormatter();
//
//            // 1. You can obtain a rowIterator and columnIterator and iterate over them
//            Log.e("AgropeuLog","\n\nIterating over Rows and Columns using Iterator\n");
//
//            Iterator<Row> rowIterator = sheet.rowIterator();
//
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//
//                // Now let's iterate over the columns of the current row
//                Iterator<Cell> cellIterator = row.cellIterator();
//
//                while (cellIterator.hasNext()) {
//
//                    try {
////                    Cell cell = cellIterator.next(); //data
////                    String cellValueData = dataFormatter.formatCellValue(cell);
//
////                    Cell cell = cellIterator.next(); //dchapa
////                    String cellValueChapa = dataFormatter.formatCellValue(cell);
//
////                    Cell cell = cellIterator.next(); //origem
////                    String cellValueOrigem = dataFormatter.formatCellValue(cell);
//
//                        Cell cell = cellIterator.next(); //numero convite
//                        String cellValueNumConvite = dataFormatter.formatCellValue(cell);
//
//                        cell = cellIterator.next(); //nome Convidado
//                        String cellValueNome = dataFormatter.formatCellValue(cell);
//
////                    cell = cellIterator.next(); //nasc
////                    String cellValueNasc = dataFormatter.formatCellValue(cell);
////
////                    Date date1=new SimpleDateFormat("MM/dd/yy").parse(cellValueNasc);
////                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
////                    cellValueNasc = df.format(date1);
////
////                    cell = cellIterator.next(); //Idade
////                    String cellValueIdade = dataFormatter.formatCellValue(cell);
////
//                        cell = cellIterator.next(); //obs
//                        String cellValueObs = dataFormatter.formatCellValue(cell);
////
////                    cell = cellIterator.next(); //doc
////                    String cellValuedoc = dataFormatter.formatCellValue(cell);
////
////                    cell = cellIterator.next(); //responsavel
////                    String cellValueRespon = dataFormatter.formatCellValue(cell);
////
////                    cell = cellIterator.next(); //status
////                    String cellValueStatus = dataFormatter.formatCellValue(cell);
////                    if(!cellValueStatus.equalsIgnoreCase("OK"))
////                        cellValueStatus = "inativo";
//
//                        //todo descomentar esse para planilha quadrilha agropeu
////                    Convidado convidado = new Convidado();
////                    convidado.setOrigem(cellValueOrigem);
////                    convidado.setNumeroConvite(cellValueNumConvite);
////                    convidado.setNome(cellValueNome);
////                    convidado.setNascimento(cellValueNasc);
////                    convidado.setIdade(cellValueIdade);
////                    convidado.setObservacao(cellValueObs);
////                    convidado.setDocumento(cellValuedoc);
////                    convidado.setFuncionario(cellValueRespon);
////                    convidado.setConviteSendoLidoPor("-");
////                    convidado.setStatus(cellValueStatus);
//
//                        //todo esse serve para a festa de fim de ano onde so precisa do nome e numero do convite
//                        Convidado convidado = new Convidado( );
//                        convidado.setOrigem("Festa Fim de Ano");
//                        convidado.setNumeroConvite(cellValueNumConvite);
//                        convidado.setNome(cellValueNome);
//                        convidado.setNascimento("14/12/2018");
//                        convidado.setIdade("1");
//                        convidado.setObservacao(cellValueObs);
//                        convidado.setDocumento("-");
//                        convidado.setFuncionario("-");
//                        convidado.setConviteSendoLidoPor("-");
//                        convidado.setStatus("ok");
//
//                        convidado.salvarConvidadoFirebase(GerenciadorActivity.this);
//
//                        Log.e("AgropeuLog", convidado.getNumeroConvite() + " " + convidado.getNome() + " " + convidado.getObservacao());
//
//                    }catch (Exception e){
//                        Log.e("AgropeuLog", e.getMessage());
//
//                    }
//
//
//
//                }
//
//            }
//
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("AgropeuLog", e.getMessage());
//
//        }
//
//
//
//    }
//
//    private class asyncTaskLerExcel extends AsyncTask<Void, Void, String> {
//
//        asyncTaskLerExcel(String urlPass) { }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            lerExcel();
//
//
//            return "";
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Toast.makeText(GerenciadorActivity.this,"Fim da leitura do excel" , Toast.LENGTH_LONG).show();
//            exibirProgresso(false);
//        }
//    }


    public void buscarDados(View view) {
        exibirProgresso(true);
        Query firebase = ConfiguracaoFirebase.getFirebase().child("CONVIDADOS").orderByKey();
        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new BuscarDadosAsync(dataSnapshot).execute();
                } else {
                    Toast.makeText(GerenciadorActivity.this, "Nenhum Convidado encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addListenerForSingleValueEvent(firebaseListener);

    }

    public void gerarRelatorioConvidados(View view) {
        exibirProgresso(true);

        String basededados = preferencias.getEventoBaseDados();
        Query firebase = ConfiguracaoFirebase.getFirebase().child("EVENTO").child(basededados).child("CONVIDADOS").orderByKey();
        firebaseListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new GerarRelatorioDeDadosAsync(dataSnapshot).execute();
                } else {
                    Toast.makeText(GerenciadorActivity.this, "Nenhum Convidado encontrado!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        firebase.addListenerForSingleValueEvent(firebaseListener);


    }

    private class BuscarDadosAsync extends AsyncTask<Void, Integer, String> {
        DataSnapshot dataSnapshot;


        BuscarDadosAsync(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Convidado convidado = ds.getValue(Convidado.class);

                ContentValues contentValues = new ContentValues();
                contentValues.put("id_num_convite", convidado.getNumeroConvite());
                contentValues.put("nome", convidado.getNome());
                contentValues.put("documento", convidado.getDocumento());
                contentValues.put("origem", convidado.getOrigem());
                contentValues.put("nascimento", convidado.getNascimento());
                contentValues.put("idade", convidado.getIdade());
                contentValues.put("funcionario", convidado.getFuncionario());
                contentValues.put("observacao", convidado.getObservacao());
                contentValues.put("status", convidado.getStatus());
                contentValues.put("horarioChegada", "");
                contentValues.put("conviteSendoLidoPor", "-");
                long id = banco.getWritableDatabase().insert("convidados_tbl", null, contentValues);
                if (id == -1)
                    return "erro ao inserir no bd";

            }
            return "ok";
        }


        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (result.equals("ok")) {

                Toast.makeText(GerenciadorActivity.this, "Sincronização OK!", Toast.LENGTH_SHORT).show();

//                Cursor c = banco.getWritableDatabase().rawQuery("drop TABLE convidados_tbl ", null);
                Cursor c = banco.getWritableDatabase().rawQuery("SELECT id, id_num_convite, nome FROM convidados_tbl ", null);
                while (c.moveToNext()) {
                    System.out.println(c.getString(0) + " " + c.getString(1) + " " + c.getString(2));
                }
                c.close();

            } else {
                Toast.makeText(GerenciadorActivity.this, result, Toast.LENGTH_SHORT).show();
            }


        }
    }

    private class GerarRelatorioDeDadosAsync extends AsyncTask<Void, Integer, String> {
        DataSnapshot dataSnapshot;
        //        File fileName;
        File folder, file;
        FileOutputStream out;
        String fileName;


        GerarRelatorioDeDadosAsync(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
//            fileName = new File("/storage/8523-1C08/backup/agropeu_final.xls");
//
//            if(fileName.exists()){
//                Log.e("AgropeuLog", "file.exists " + fileName.getPath());
//
//                if(fileName.canRead()){
//                    Log.e("AgropeuLog", "fileName.canRead() " + fileName.getPath());
//                }else{
//                    Log.e("AgropeuLog", "fileName.canRead() NAO!!!" + fileName.getPath());
//                }
//
//                if(fileName.canWrite()){
//                    Log.e("AgropeuLog", "fileName.canWrite() " + fileName.getPath());
//                }else{
//                    Log.e("AgropeuLog", "fileName.canWrite() NAO!!!" + fileName.getPath());
//                }
//            }
//            else {
//                String folder_main = "agropeu";
//
//                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//                if (!f.exists()) {
//                    f.mkdirs();
//
//                }
//
//                Log.e("AgropeuLog", "nao exists " + fileName.getAbsolutePath());

            fileName = "Relatorio_Convidados_" + nomeEvento + ".xls";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            folder = new File(extStorageDirectory, "Eventos Agropeu");
            folder.mkdir();
            file = new File(folder, fileName);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e("AgropeuLog", "IOException " + e1.getMessage());
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheetConvidados = workbook.createSheet("Convidados");

            int rownum = 0;
            Row row = sheetConvidados.createRow(rownum++);
            int cellnum = 0;

            Cell cellNome = row.createCell(cellnum++);
            cellNome.setCellValue("NOME CONVIDADO");

            Cell cellNumConvite = row.createCell(cellnum++);
            cellNumConvite.setCellValue("NUM CONVITE");

            Cell cellStatus = row.createCell(cellnum++);
            cellStatus.setCellValue("STATUS CONVITE");

            Cell cellResponsavel = row.createCell(cellnum++);
            cellResponsavel.setCellValue("RESPONSAVEL");

            Cell cellOrigem = row.createCell(cellnum++);
            cellOrigem.setCellValue("ORIGEM");

            Cell cellHorario = row.createCell(cellnum++);
            cellHorario.setCellValue("HORARIO CHEGADA");

            Cell cellNascimento = row.createCell(cellnum++);
            cellNascimento.setCellValue("NASCIMENTO");

            Cell cellDoc = row.createCell(cellnum++);
            cellDoc.setCellValue("DOCUMENTO");

            Cell cellObs = row.createCell(cellnum++);
            cellObs.setCellValue("OBSERVAÇÃO");

            Cell cellSeguranca = row.createCell(cellnum++);
            cellSeguranca.setCellValue("SEGURANCA");


            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                Convidado convidado = ds.getValue(Convidado.class);

                row = sheetConvidados.createRow(rownum++);
                cellnum = 0;

                cellNome = row.createCell(cellnum++);
                cellNome.setCellValue(convidado.getNome());

                cellNumConvite = row.createCell(cellnum++);
                cellNumConvite.setCellValue(convidado.getNumeroConvite());

                cellStatus = row.createCell(cellnum++);
                cellStatus.setCellValue(convidado.getStatus());

                cellResponsavel = row.createCell(cellnum++);
                cellResponsavel.setCellValue(convidado.getFuncionario());

                cellOrigem = row.createCell(cellnum++);
                cellOrigem.setCellValue(convidado.getOrigem());

                cellHorario = row.createCell(cellnum++);
                cellHorario.setCellValue(convidado.getHorarioChegada());

                cellNascimento = row.createCell(cellnum++);
                cellNascimento.setCellValue(convidado.getNascimento());

                cellDoc = row.createCell(cellnum++);
                cellDoc.setCellValue(convidado.getDocumento());

                cellObs = row.createCell(cellnum++);
                cellObs.setCellValue(convidado.getObservacao());

                cellSeguranca = row.createCell(cellnum++);
                cellSeguranca.setCellValue(convidado.getConviteSendoLidoPor());


            }

            try {
                out = new FileOutputStream(file);
                workbook.write(out);
                out.close();
                return ("ok");


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return ("Arquivo não encontrado!");
            } catch (IOException e) {
                e.printStackTrace();
                return ("Erro na edição do arquivo!");
            }


        }


        @Override
        protected void onPostExecute(String result) {
            exibirProgresso(false);
            if (result.equals("ok")) {

                Toast.makeText(GerenciadorActivity.this, "Relatorio: " + fileName + " gerado em Pasta Eventos Agropeu do telefone", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(GerenciadorActivity.this, result, Toast.LENGTH_SHORT).show();
            }


        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 10:
//                if (resultCode == RESULT_OK) {
//                    String path = data.getData().getPath();
//                    txt_path.setText(path);
//                }
//
//                break;
//
//        }
//
//    }
}
