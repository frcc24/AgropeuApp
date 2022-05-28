package com.francocorrea.agropeuapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.helper.Preferencias;
import com.francocorrea.agropeuapp.model.Convidado;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public class TelaImportacaoXmlActivity extends AppCompatActivity {

    // Storage Permissions
    private final int REQUEST_PERMISSION_Access_STORAGE = 1;
    Intent myFileIntent;
    ConstraintLayout mProgressBar, excel_lo;
    Convidado convidado;
    TextView txt_path;
    Preferencias preferencias;
    private int numConvidadosInseridos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_importacao_xml);
        mProgressBar = findViewById(R.id.progress_bar_layout);
        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });

        numConvidadosInseridos = 0;

        preferencias = new Preferencias(this);
        TextView txtNomeEvento = findViewById(R.id.txt_nome_convidado);
        txtNomeEvento.setText(preferencias.getEventoBaseDados());

        mProgressBar = findViewById(R.id.progress_bar_layout);

        exibirProgresso(false);
        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fazer nada
            }
        });


        txt_path = findViewById(R.id.txt_arq_path);
        Button bt_selecionarArquivo = findViewById(R.id.btn_selecionarArq);
        bt_selecionarArquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent, 10);

            }
        });


    }


    private void exibirProgresso(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }


    public void LeituraArquivoExcel(View v) {

        if (!txt_path.getText().toString().equals("Caminho do Arquivo Selecionado") &&
                txt_path.getText().toString().endsWith("xls")) {
            exibirProgresso(true);
            verifyStoragePermissions(this);
        } else {
            Toast.makeText(TelaImportacaoXmlActivity.this, "Selecione um arquivo do tipo .xls", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void verifyStoragePermissions(Activity activity) {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED ||
                permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_Access_STORAGE);

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_Access_STORAGE);
            }
        } else {
            // Toast.makeText(TelaImportacaoXmlActivity.this, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();

            new asyncTaskLerExcel("").execute();
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_Access_STORAGE);

                    }
                });
        AlertDialog d = builder.create();
        d.show();

    }

    public void lerExcel() {

        numConvidadosInseridos = 0;
        try {
            // Creating a Workbook from an Excel file (.xls or .xlsx)
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.e("AgropeuLog", "(Environment.MEDIA_MOUNTED() = ok");

            }
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                Log.e("AgropeuLog", "(Environment.MEDIA_MOUNTED_READ_ONLY() = ok");

            }
            //todo colocar aviso que tem q ser excel formato 97 ".xls" sem formatacao

            File extStore = Environment.getExternalStorageDirectory();
//            File fileName = new File(extStore.getAbsolutePath() + "/agropeu.xls");
            String pathname = txt_path.getText().toString();
            String pathCerto = "";
            if (pathname.startsWith("/document/raw:"))
                pathCerto = pathname.replaceFirst("/document/raw:", "");
            else if (pathname.startsWith("/document/primary:"))
                pathCerto = pathname.replaceFirst("/document/primary:", "");


            File myFile = new File(pathCerto);


            if (myFile.exists()) {
                Log.e("AgropeuLog", "file.exists " + myFile.getPath());
            } else {
                String folder_main = "agropeu";

                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                if (!f.exists()) {
                    f.mkdirs();
                }

                Log.e("AgropeuLog", "nao exists " + myFile.getAbsolutePath());
            }


            InputStream inputFS = new FileInputStream(myFile);
            Workbook workbook = WorkbookFactory.create(inputFS);


            // Getting the Sheet at index zero
            Sheet sheet = workbook.getSheetAt(0);

            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();

            // 1. You can obtain a rowIterator and columnIterator and iterate over them
            Log.e("AgropeuLog", "\n\nIterating over Rows and Columns using Iterator\n");

            Iterator<Row> rowIterator = sheet.rowIterator();

            String cellValueObs = "-";


            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Now let's iterate over the columns of the current row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {

                    try {
//                    Cell cell = cellIterator.next(); //data
//                    String cellValueData = dataFormatter.formatCellValue(cell);

//                    Cell cell = cellIterator.next(); //dchapa
//                    String cellValueChapa = dataFormatter.formatCellValue(cell);

//                    Cell cell = cellIterator.next(); //origem
//                    String cellValueOrigem = dataFormatter.formatCellValue(cell);

                        Cell cell = cellIterator.next(); //numero convite
                        String cellValueNumConvite = dataFormatter.formatCellValue(cell);

                        if (cellValueNumConvite.length() > 0) {// se o numero do convite nao esta em branco

                            cell = cellIterator.next(); //nome Convidado
                            String cellValueNome = dataFormatter.formatCellValue(cell);


                            String cellValueNasc = "01/01/1900";
//                            try {
//                                cell = cellIterator.next(); //obs
//                                cellValueNasc = dataFormatter.formatCellValue(cell);
//                            } catch (Exception e) {
//                                Log.e("AgropeuLog", "Campo Nascimento vazio, preenchido com 01/01/1900 ");
//
//                            }
//
//                    Date date1=new SimpleDateFormat("MM/dd/yy").parse(cellValueNasc);
//                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//                    cellValueNasc = df.format(date1);
//
//                    cell = cellIterator.next(); //Idade
//                    String cellValueIdade = dataFormatter.formatCellValue(cell);
//
                            try {
                                cell = cellIterator.next(); //obs
                                cellValueObs = dataFormatter.formatCellValue(cell);
                            } catch (Exception e) {
                                Log.e("AgropeuLog", "Campo Obs vazio, preenchido com - ");
                                cellValueObs = "-";

                            }
//
//                    cell = cellIterator.next(); //doc
//                    String cellValuedoc = dataFormatter.formatCellValue(cell);
//
//                    cell = cellIterator.next(); //responsavel
//                    String cellValueRespon = dataFormatter.formatCellValue(cell);
//
//                    cell = cellIterator.next(); //status
//                    String cellValueStatus = dataFormatter.formatCellValue(cell);
//                    if(!cellValueStatus.equalsIgnoreCase("OK"))
//                        cellValueStatus = "inativo";

                            //todo descomentar esse para planilha quadrilha agropeu
//                    Convidado convidado = new Convidado();
//                    convidado.setOrigem(cellValueOrigem);
//                    convidado.setNumeroConvite(cellValueNumConvite);
//                    convidado.setNome(cellValueNome);
//                    convidado.setNascimento(cellValueNasc);
//                    convidado.setIdade(cellValueIdade);
//                    convidado.setObservacao(cellValueObs);
//                    convidado.setDocumento(cellValuedoc);
//                    convidado.setFuncionario(cellValueRespon);
//                    convidado.setConviteSendoLidoPor("-");
//                    convidado.setStatus(cellValueStatus);

                            //todo esse serve para a festa de fim de ano onde so precisa do nome e numero do convite
                            Convidado convidado = new Convidado();
                            convidado.setOrigem("Planilha " + preferencias.getName());
                            convidado.setNumeroConvite(cellValueNumConvite);
                            convidado.setNome(cellValueNome);
                            convidado.setNascimento(cellValueNasc);
                            convidado.setIdade("Importação");
                            convidado.setObservacao(cellValueObs);
                            convidado.setDocumento("-");
                            convidado.setFuncionario("Importação");
                            convidado.setConviteSendoLidoPor("-");
                            convidado.setStatus("ok");


                            convidado.salvarConvidadoFirebase(TelaImportacaoXmlActivity.this);
                            numConvidadosInseridos++;

                            Log.e("AgropeuLog", convidado.getNumeroConvite() + " " + convidado.getNome() + " " + convidado.getObservacao());
                        }//fim se o num convite nao esta em branco

                    } catch (Exception e) {
                        Log.e("AgropeuLog", e.getMessage());

                    }


                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AgropeuLog", e.getMessage());

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    String path = data.getData().getPath();
                    txt_path.setText(path);
                }

                break;

        }


    }

    private class asyncTaskLerExcel extends AsyncTask<Void, Void, String> {

        asyncTaskLerExcel(String urlPass) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            lerExcel();

            return "";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(TelaImportacaoXmlActivity.this, "Fim da leitura do excel!\nConvidados inseridos/atualizados: " + numConvidadosInseridos, Toast.LENGTH_LONG).show();
            exibirProgresso(false);
        }
    }


    //todo codigo abaixo para pegar espacos em branco (TESTAR)
//    // import org.apache.poi.ss.usermodel.*;
//    DataFormatter formatter = new DataFormatter();
//    Sheet sheet1 = wb.getSheetAt(0);
//for (Row row : sheet1) {
//        for (Cell cell : row) {
//            CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
//            System.out.print(cellRef.formatAsString());
//            System.out.print(" - ");
//            // get the text that appears in the cell by getting the cell value and applying any data formats (Date, 0.00, 1.23e9, $1.23, etc)
//            String text = formatter.formatCellValue(cell);
//            System.out.println(text);
//            // Alternatively, get the value and format it yourself
//            switch (cell.getCellType()) {
//                case CellType.STRING:
//                    System.out.println(cell.getRichStringCellValue().getString());
//                    break;
//                case CellType.NUMERIC:
//                    if (DateUtil.isCellDateFormatted(cell)) {
//                        System.out.println(cell.getDateCellValue());
//                    } else {
//                        System.out.println(cell.getNumericCellValue());
//                    }
//                    break;
//                case CellType.BOOLEAN:
//                    System.out.println(cell.getBooleanCellValue());
//                    break;
//                case CellType.FORMULA:
//                    System.out.println(cell.getCellFormula());
//                    break;
//                case CellType.BLANK:
//                    System.out.println();
//                    break;
//                default:
//                    System.out.println();
//            }
//        }
//    }


}
