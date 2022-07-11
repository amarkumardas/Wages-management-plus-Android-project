package amar.das.acbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;

public class Final_Pdf_Viewer extends AppCompatActivity {
    PDFView pdfView;
    private String fromIntentPersonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

           String url=getIntent().getStringExtra("pdfurl");
           fromIntentPersonId=getIntent().getStringExtra("ID");
           pdfView=findViewById(R.id.pdfView);
//           File pdfFile=new File(url);
//           pdfView.fromFile(pdfFile).load();

            try {
                byte[] pdfBytes = Files.readAllBytes(Paths.get(url));//CONVERTED pdf file to byte array if path is not found then catch block execute
                pdfView.fromBytes(pdfBytes).load();

            } catch (IOException ex) {
                Toast.makeText(this, "PDF File not Found Exception ", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }

    }


}