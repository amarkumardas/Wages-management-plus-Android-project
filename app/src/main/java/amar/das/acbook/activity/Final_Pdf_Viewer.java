package amar.das.acbook.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Final_Pdf_Viewer extends AppCompatActivity {
    PDFView pdfView;
    private String fromIntentPersonId;
    Button pdf1, pdf2, downloadPdf, sharePdf,currentPdf;
    File absolutePathPdf;
    byte whichPdfIndicator;
    byte [] bytepdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0); //we have used overridePendingTransition(), it is used to remove activity create animation while re-creating activity.
        setContentView(R.layout.activity_pdf_viewer);
        initializeIds();
        whichPdfIndicator = getIntent().getByteExtra("pdf1orpdf2",(byte) 0);
        fromIntentPersonId = getIntent().getStringExtra("ID");
        onlyViewPdf((byte) whichPdfIndicator);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();//StrictMode is a developer tool which detects things you might be doing by accident and brings them to your attention so you can fix them.
        StrictMode.setVmPolicy(builder.build());

        downloadPdf.setOnClickListener(view -> {
            if(whichPdfIndicator ==(byte)1){

            } else if(whichPdfIndicator == (byte)2){

            }else if(whichPdfIndicator ==(byte) 3){

            } else
                Toast.makeText(this, "WRONG PDF INDICATOR", Toast.LENGTH_LONG).show();
        });
        sharePdf.setOnClickListener(view -> {
            if (whichPdfIndicator == (byte)1) {

            }else if (whichPdfIndicator ==(byte) 2) {
                     if(convertBytesToFile(bytepdf)){
                         Intent intentShare = new Intent(Intent.ACTION_SEND);
                         intentShare.setType("application/pdf");
                         intentShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(absolutePathPdf));
                         startActivity(Intent.createChooser(intentShare, "Share the file ..."));
                     }else {
                         Toast.makeText(this, "byte", Toast.LENGTH_SHORT).show();
                     }
            }
            else if (whichPdfIndicator == (byte)3) {

            }else
                Toast.makeText(this, "WRONG PDF INDICATOR", Toast.LENGTH_LONG).show();
        });
        pdf1.setOnClickListener(view -> {
            whichPdfIndicator = (byte)1;//this will be updated when user click pdf1 for share and download
            onlyViewPdf(whichPdfIndicator);
        });
        pdf2.setOnClickListener(view -> {
            whichPdfIndicator =(byte)2;//this will be updated when user click pdf2 for share and download
            onlyViewPdf(whichPdfIndicator);
        });
        currentPdf.setOnClickListener(view -> {
            whichPdfIndicator = (byte)3;//this will be updated when user click pdf2 for share and download
            onlyViewPdf(whichPdfIndicator);
        });
//           File pdfFile=new File(url);
//           pdfView.fromFile(pdfFile).load();

//            try {
//                byte[] pdfBytes = Files.readAllBytes(Paths.get(url));//CONVERTED pdf file to byte array if path is not found then catch block execute
//                pdfView.fromBytes(pdfBytes).load();
//
//            } catch (IOException ex) {
//                Toast.makeText(this, "PDF File not Found Exception ", Toast.LENGTH_LONG).show();
//                ex.printStackTrace();
//            }
    }
//    private void requestPermission(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){//11
//            Toast.makeText(this, "11", Toast.LENGTH_SHORT).show();
//            try{
//                Intent intent=new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                Uri uri=Uri.fromParts("package",this.getPackageName(),null);
//                intent.setData(uri);
//                storageActivityResultLauncher.launch(intent);
//            }catch (Exception e){
//                Intent intent=new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                storageActivityResultLauncher.launch(intent);
//                e.printStackTrace();
//            }
//        }else{
//            Toast.makeText(this, "10", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this,new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},333);
//        }
//    }
//    private ActivityResultLauncher<Intent> storageActivityResultLauncher=registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
//                        if(Environment.isExternalStorageManager()){
//                           //Manage exTERNAL storage permission is granted
//                            if(convertBytesToFile(bytepdf)){
//                                Intent intentShare = new Intent(Intent.ACTION_SEND);
//                                intentShare.setType("application/pdf");
//                                intentShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(absolutePathPdf));
//                                startActivity(Intent.createChooser(intentShare, "Share the file ..."));
//                            }
//                        }else{
//                            //Manage exTERNAL storage permission is denied
//                            Toast.makeText(Final_Pdf_Viewer.this, "//EXTERNAL storage permission is granted", Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                           //android 10
//                    }
//                }
//            }
//    );

//    public boolean checkPermission(){
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){//11
//            Toast.makeText(this, "11", Toast.LENGTH_SHORT).show();
//              return Environment.isExternalStorageManager();//Returns whether the calling app has All Files Access at the given pathDeclaring the permission Manifest.permission.MANAGE_EXTERNAL_STORAGE isn't enough to gain the access.To request access, use Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION.
//        }else{
//            Toast.makeText(this, "10", Toast.LENGTH_SHORT).show();
//            int write= ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
//            int read= ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
//            return write==PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
//        }
//    }

    //handle permission request results
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode==333){
//            if(grantResults.length >0){
//                //check each permission if granted or not
//                boolean write=grantResults[0]==PackageManager.PERMISSION_GRANTED;
//                boolean read=grantResults[1]==PackageManager.PERMISSION_GRANTED;
//                if(write&& read){
//                    //external storage permission granted
//                    if(convertBytesToFile(bytepdf)){
//                        Intent intentShare = new Intent(Intent.ACTION_SEND);
//                        intentShare.setType("application/pdf");
//                        intentShare.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(absolutePathPdf));
//                        startActivity(Intent.createChooser(intentShare, "Share the file ..."));
//                    }
//
//                }else{
//                    //not granted
//                    Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    private void onlyViewPdf(byte whichPdfIndicator) {
        if(whichPdfIndicator == (byte) 2 || whichPdfIndicator == (byte) 1 || whichPdfIndicator == (byte) 3) {//if indicator is other then 1 or 2 then show wrong pdf indicator
           if(whichPdfIndicator == (byte) 3){
               Toast.makeText(this, "create pdf and display it", Toast.LENGTH_SHORT).show();
               //take row and display it as pdf
           }else {
               String whichPdf = "";
               if (whichPdfIndicator == (byte) 2) {
                   whichPdf = "PDF2";
               }else if (whichPdfIndicator == (byte) 1) {
                   whichPdf = "PDF1";
               }
               try (PersonRecordDatabase db = new PersonRecordDatabase(getApplicationContext());
                    Cursor cursor = db.getData("SELECT " + whichPdf + " FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId + "'")) {
                   cursor.moveToFirst();
                   if (cursor.getBlob(0) != null) {
                       bytepdf =cursor.getBlob(0);
                       pdfView.fromBytes(cursor.getBlob(0)).load();
                   } else {
                       displDialogMessage("NO PREVIOUS PDF", "IT WILL BE AVAILABLE WHEN AGAIN CALCULATION IS DONE");
                   }
               }catch (Exception se){
                   Toast.makeText(this, "CANT VIEW PDF", Toast.LENGTH_SHORT).show();
                   se.printStackTrace();
               }
           }
        }else
            Toast.makeText(this, "WRONG PDF INDICATOR", Toast.LENGTH_LONG).show();
}
    private void displDialogMessage(String title, String message) {
        AlertDialog.Builder showDataFromDataBase=new AlertDialog.Builder(Final_Pdf_Viewer.this);
        showDataFromDataBase.setCancelable(false);
        showDataFromDataBase.setTitle(title);
        showDataFromDataBase.setMessage(message);
        showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {//REFRESHING
                dialogInterface.dismiss();
            }
        });
        showDataFromDataBase.create().show();
    }
    private boolean convertBytesToFile(byte [] pdfByte){// Converts the array of bytes into a File
        File folder = new File(getExternalFilesDir(null) + "/acBookPDF");
        if (!folder.exists()) {//if folder not exist then create folder
            folder.mkdir();//File createNewFile() method returns true if new file is created and false if file already exists.
        }
       File filePath=new File(getExternalFilesDir(null) + "/acBookPDF/" +generateFileName(fromIntentPersonId)+ ".pdf");//path of pdf where it is saved in device

        try{
            FileOutputStream fileOutputStream = new FileOutputStream(filePath.getAbsolutePath());
            fileOutputStream.write(pdfByte);
            fileOutputStream.close();
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
            return false;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

        absolutePathPdf=filePath.getAbsoluteFile();//pdfFile is global file variable
        return true;
    }
    private String generateFileName(String ID) {
        final Calendar current=Calendar.getInstance();//to get current date and time
        Date d=Calendar.getInstance().getTime();//To get time
        SimpleDateFormat sdf=new SimpleDateFormat("hhmma");//a stands for is AM or PM
        return "id"+ID+"date"+current.get(Calendar.DAY_OF_MONTH)+"_"+(current.get(Calendar.MONTH)+1)+"_"+current.get(Calendar.YEAR)+"at"+sdf.format(d);
    }
    private void initializeIds() {
        pdfView=findViewById(R.id.pdfView);
        pdf1=findViewById(R.id.pdf1_btn);
        pdf2=findViewById(R.id.pdf2_btn);
        downloadPdf=findViewById(R.id.download_pdf_btn);
        sharePdf=findViewById(R.id.share_pdf_btn);
        currentPdf=findViewById(R.id.current_pdf_btn);
    }
}