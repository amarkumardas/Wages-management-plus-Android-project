package amar.das.acbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.ActivityIndividualpersondetailactivityBinding;


public class IndividualPersonDetailActivity extends AppCompatActivity {
ActivityIndividualpersondetailactivityBinding binding;
 PersonRecordDatabase db;
 private String fromIntentPersonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIndividualpersondetailactivityBinding.inflate(getLayoutInflater());
        setContentView (binding.getRoot());

        db=new PersonRecordDatabase(this);//on start only database should be create

        fromIntentPersonId =getIntent().getStringExtra("ID");//getting data from intent
        //retrieving data from db

        Cursor cursor=db.getData("SELECT NAME,BANKACCOUNT,IFSCCODE,BANKNAME,AADHARCARD,PHONE,TYPE,FATHERNAME,IMAGE,ACHOLDER,ID FROM "+db.TABLE_NAME+" WHERE ID='"+ fromIntentPersonId +"'");

        if(cursor != null) {
               cursor.moveToFirst();
               binding.nameTv.setText(cursor.getString(0));
               binding.accountTv.setText(Html.fromHtml( "A/C:  "+"<b>"+cursor.getString(1)+"</b>"));
               binding.ifscCodeTv.setText("IFSC:  "+cursor.getString(2));
               binding.bankNameTv.setText("Bank: "+cursor.getString(3));
               binding.aadharTv.setText(Html.fromHtml("Aadhar Card:  "+"<b>"+cursor.getString(4)+"</b>"));
               binding.phoneTv.setText("Phone:  "+cursor.getString(5));
               binding.skillTv.setText(cursor.getString(6));
               binding.fatherNameTv.setText("Father: "+cursor.getString(7));

               if(cursor.getString(5).length()==10){//if there is no phone number then show default icon color black else green icon
                   binding.callTv.setBackgroundResource(R.drawable.ic_outline_call_24);
               }

               byte[] image=cursor.getBlob(8);//getting image from db as blob
               //getting bytearray image from DB and converting  to bitmap to set in imageview
               Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
               binding.imageImg.setImageBitmap(bitmap);

               binding.acHolderTv.setText("A/C Holder: "+cursor.getString(9));
               binding.idTv.setText("ID: "+cursor.getString(10));
           }else{
            Toast.makeText(this, "No data in cursor", Toast.LENGTH_SHORT).show();
        }

        //to open dialpaid
        binding.callTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cursor.getString(5).length()==10){
                    Intent callingIntent=new Intent(Intent.ACTION_DIAL);
                    callingIntent.setData(Uri.parse("tel:+91"+cursor.getString(5)));
                    startActivity(callingIntent);
                }else
                    Toast.makeText(IndividualPersonDetailActivity.this, "No Phone number added", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(getBaseContext(),InsertDataActivity.class);
                intent.putExtra("ID", fromIntentPersonId);
                startActivity(intent);
                finish();//while going to other activity so destryo  this current activity so that while coming back we will see refresh activity
                return false;
            }
        });
        db.close();
       // cursor.close(); it should not be close because of call action to perform then we need cursor
    }
}