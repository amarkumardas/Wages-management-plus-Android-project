package amar.das.acbook.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import amar.das.acbook.ImageResizer;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.ui.search.SearchFragment;


public class InsertDataActivity extends AppCompatActivity {
    Button add;
  EditText name,account, acholdername,ifsccode,aadharcard,phone,fathername;
  AutoCompleteTextView bankname_autocomptextview;
  PersonRecordDatabase personDb;
  RadioGroup radioGroup;
  RadioButton  laberRadio,womenRadio,mestreRadio;
  String skill,fromIntentPersonId;
  String[] indianBank;//to store array

    //********************for camera and galary***********************
    private static final int GalleryPick = 1;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String cameraPermission[];
    String storagePermission[];
    ImageView imageView;
    //****************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);


        //database created
        personDb=new PersonRecordDatabase(this);
        //set ids
        add=findViewById(R.id.add_button);
        name=findViewById(R.id.name_et);
        account=findViewById(R.id.accountno_et);
        acholdername =findViewById(R.id.acholder_et);
        ifsccode=findViewById(R.id.ifsccode_et);
        bankname_autocomptextview=findViewById(R.id.bankname_autocomplte_tv);
        aadharcard=findViewById(R.id.aadharcard_et);
        phone=findViewById(R.id.phonenumber_et);
        fathername =findViewById(R.id.fathername_et);

        //radio button
        radioGroup=findViewById(R.id.skill_radiogp);
        laberRadio =findViewById(R.id.laber);//required when updating
        womenRadio=findViewById(R.id.women_laber);//required when updating
        mestreRadio=findViewById(R.id.mestre);//required when updating
        laberRadio.setChecked(true);//by default laber will be checked other wise person wont be able to find.But this default will not work while updating because manually setting checked radio
        skill="L";//skill default value otherwise null will be set as default so its important
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedidOfRadioBtn) {
                switch(checkedidOfRadioBtn){
                    case R.id.mestre:{
                        skill="M";//updating skill variable
                        break;
                    }
                    case R.id.laber:{
                        skill="L";
                        break;
                    }
                    case R.id.women_laber:{
                        skill="G";
                        break;
                    }
                }
            }
        });
//        int skillid=radioGroup.getCheckedRadioButtonId();
//        skill_radioBtn=findViewById(skillid);
        //spinner=findViewById(R.id.spinner);

//        type_plus =getResources().getStringArray(R.array.skills);//getting array from string values declared there M L G
//        ArrayAdapter<String>adapter=new ArrayAdapter<>(InsertDataActivity.this,android.R.layout.simple_list_item_1, type_plus);
//        spinner.setAdapter(adapter);

         //get bank names
        indianBank =getResources().getStringArray(R.array.indian_bank_names);
        ArrayAdapter<String> bankadapter=new ArrayAdapter<>(InsertDataActivity.this, android.R.layout.simple_list_item_1, indianBank);
        bankname_autocomptextview.setAdapter(bankadapter);


        //For camera and galary*********************************************************************************
        imageView = findViewById(R.id.imageview);

        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // After clicking on text we will have
        // to choose whether to
        // select image from camera and gallery
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });

        //after getting all the ids setting all data accoring to id
        if(getIntent().hasExtra("ID")){
            fromIntentPersonId=getIntent().getStringExtra("ID");//getting id from intent
            //retrieving data from db
            Cursor cursor1=personDb.getData("SELECT NAME,BANKACCOUNT,IFSCCODE,BANKNAME,AADHARCARD,PHONE,TYPE,FATHERNAME,IMAGE,ACHOLDER FROM "+personDb.TABLE_NAME+" WHERE ID='"+fromIntentPersonId+"'");

            if(cursor1 != null) {
                cursor1.moveToFirst();
                name.setText(cursor1.getString(0));
                account.setText(cursor1.getString(1));
                ifsccode.setText(cursor1.getString(2));
                bankname_autocomptextview.setText(cursor1.getString(3));
                aadharcard.setText(cursor1.getString(4));
                phone.setText(cursor1.getString(5));
                skill=cursor1.getString(6);//skill is variable

                 //radio button should be checked according to data
                 if(skill.equals("L"))
                    laberRadio.setChecked(true);
                 else if(skill.equals("M"))
                     mestreRadio.setChecked(true);
                 else
                     womenRadio.setChecked(true);

                fathername.setText(cursor1.getString(7));

                byte[] image=cursor1.getBlob(8);//getting image from db as blob and storing in byte type Blop is large type
                //getting bytearray image from DB and converting  to bitmap to set in imageview
                Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
                imageView.setImageBitmap(bitmap);
                acholdername.setText(cursor1.getString(9));
                add.setText("UPDATE");
                cursor1.close();
            }else
                Toast.makeText(this, "cursor is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setCancelable(true);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    // checking storage permissions
    @NonNull
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);


        Toast.makeText(this, ""+result, Toast.LENGTH_SHORT).show();
        return result;
    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    @NonNull
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        Toast.makeText(this, ""+result1+result, Toast.LENGTH_SHORT).show();
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(InsertDataActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Toast.makeText(this, "CROP OUT", Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
                Toast.makeText(this, "crop if", Toast.LENGTH_SHORT).show();
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }

    //action while clicking insert button
    public void insert_click(View view) {
        add.setVisibility(View.GONE);//so that user do not enter again add buttion while data is  inserting in database beacuse if user do again then it will overload

        String personName=name.getText().toString().toUpperCase();//taking all value in uppercase
        String personAccount=account.getText().toString();
        String personAccountHolderName= acholdername.getText().toString().toUpperCase();
        String personIfsccode=ifsccode.getText().toString().toUpperCase();
        String personAadhar=aadharcard.getText().toString();
        String personPhon=phone.getText().toString();
        String personFathername= fathername.getText().toString().toUpperCase();
        String personType=skill;
        String personBankName=bankname_autocomptextview.getText().toString();//autocomplete Text view

        //to store in db we have to convert imageview to Bitmap and Bitmap to bytearray and to retrieve it from db we have to convert bytearray to Bitmap to set in imageview
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();//A Drawable that wraps a bitmap and can be tiled, stretched..
        Bitmap fullsizebitmapimage = drawable.getBitmap();//converted imageview to bitmap
        Bitmap reduceSize= ImageResizer.reduceBitmapSize(fullsizebitmapimage,46000);//resizing image to store in db
        byte[] imagestore= convertBitmapToByteArray(reduceSize);//convertImageViewToByteArray(reduceSize); reduceSize contain image so sending to convert to byte array to store in database

            AlertDialog.Builder detailsReview = new AlertDialog.Builder(this);
            detailsReview.setCancelable(false);
            detailsReview.setTitle("REVIEW DETAILS");// Html tags video- https://www.youtube.com/watch?v=98BD6IjQQkE
            detailsReview.setMessage(Html.fromHtml("Name:-" +"<b>"+ personName+"</b>"+"<br>"+"<br>"+
                    "Father Name:-" +"<b>"+ personFathername+"</b>" +"<br>"+"<br>"+
                    "Account No:-" +"<b>"+ personAccount +"</b>" +"<br>"+"<br>"+
                    "A/C Holder:-" +"<b>"+personAccountHolderName+"</b>" +"<br>"+"<br>"+
                    "Bank Name:-" +"<b>"+ personBankName+"</b>"+"<br>" +"<br>"+
                    "IFSC Code:--- " +"<b>"+ personIfsccode+"</b>"  +"<br>"+"<br>"+
                    "Phone No:---- " +"<b>"+ personPhon +"</b>" +"<br>"+"<br>"+
                    "Aadhar No:-- " +"<b>"+ personAadhar+"</b>"  +"<br>"+"<br>"+
                    "Person Type:-" +"<b>"+ personType+"</b>"  +"<br>"));


            detailsReview.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    add.setVisibility(View.VISIBLE);
                }
            });
            detailsReview.setPositiveButton("YES CORRECT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    boolean isInserted,isUpdated;
                    isUpdated=isInserted=false;

                    //update
                    if(getIntent().hasExtra("ID")){//will execute when updating
                        //get data from db
                        isUpdated=personDb.updateData(personName, personAccount, personIfsccode, personBankName, personAadhar, personPhon, personType, personFathername, imagestore, personAccountHolderName,fromIntentPersonId);
                        if(isUpdated==true){//if it is updated then show successfull message
                            Toast.makeText(InsertDataActivity.this, "ID: "+fromIntentPersonId+" Updated successfully", Toast.LENGTH_SHORT).show();

                            //after success then go to previous activity automatically and destroy current activity so that when pressing back user should not get same activity this is done by finish();
                            Intent in=new Intent(getBaseContext(),IndividualPersonDetailActivity.class);//completed then go back
                            in.putExtra("ID",fromIntentPersonId);//after going bact to this IndividualPersonDetailActivity then it require ID so putExtra is used
                            startActivity(in);
                            finish();//destroy current activity

                        }else
                            Toast.makeText(InsertDataActivity.this, "Not Updated", Toast.LENGTH_SHORT).show();

                    }else {//this will execute only when adding new person

                        for (int k = 1; k <= 20; k++) {
                            //inserting data to sqlite database
                            isInserted = personDb.insertData(personName, personAccount, personIfsccode, personBankName, personAadhar, personPhon, personType, personFathername, imagestore, personAccountHolderName);
                        }

                        if (isInserted == true) {//checking for duplicate
                            Cursor result = personDb.getId(personName, personAccount, personIfsccode, personBankName, personAadhar, personPhon, personType, personFathername, personAccountHolderName);
                            StringBuilder buffer;//because it is not synchronized and efficient then stringbuffer and no need to lock and unlock
                            String holdlastid="";
                            if(result.getCount() == 1 || result.getCount() > 1){
                                buffer=new StringBuilder( );
                                if(result.moveToFirst() && result.getCount()==1) {
                                    buffer.append("\n" + "New Person ID no: " + result.getString(0));
                                    displResult("Added Successfully", buffer.toString());
                                    add.setVisibility(View.VISIBLE);
                                }

                                if(result.getCount() > 1){//this will be true when user all details is same to others
                                    buffer.append("Matching "+result.getCount()+" Person with same Details:"+"\n");
                                    result.moveToPrevious();//it help to start from first othervise 1 item is not displayed
                                    while(result.moveToNext()){
                                        holdlastid=""+result.getString(0);//to diaplay new added person ids comes at last when loop
                                        buffer.append("\nPerson ID: "+result.getString(0));
                                    }
                                    displResult("Successfully Added New Person ID is: "+holdlastid,buffer.toString());
                                    add.setVisibility(View.VISIBLE);
                                }
                                result.close();//closing cursor
                            }
                            else
                                displResult("Data is Inserted but Query not returned any ID","\n"+"result.getCount()= "+result.getCount());

                            // eraseAllDataAfterInsertingFromLayout();//should be here because control doent not wait for its execution
                        }
                        else
                            displResult("Data FAILED to Insert","\n"+"Number of column maybe different in DataBase");
                    }
                }

                private void displResult(String title,String message) {
                    AlertDialog.Builder showDataFromDataBase = new AlertDialog.Builder(InsertDataActivity.this);
                    showDataFromDataBase.setCancelable(false);
                    showDataFromDataBase.setTitle(title);
                    showDataFromDataBase.setMessage(message);
                    showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    showDataFromDataBase.create().show();
                }

                private void eraseAllDataAfterInsertingFromLayout() {
                    name.setText("");
                    account.setText("");
                    acholdername.setText("");
                    ifsccode.setText("");
                    bankname_autocomptextview.setText("");
                    aadharcard.setText("");
                    phone.setText("");
                    fathername.setText("");
                    imageView.setImageResource(R.drawable.defaultprofileimage);
                }
            });
       detailsReview.create().show();
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();//converted bitmap to byte array
    }

    public void go_back(View view) { //go from activity to fragment

        if(getIntent().hasExtra("ID")){//when it is called from other activity intent
            Intent in=new Intent(getBaseContext(),IndividualPersonDetailActivity.class); //go back
            in.putExtra("ID",fromIntentPersonId);//after going bact to this IndividualPersonDetailActivity then it require ID so putExtra is used
            startActivity(in);
            finish();//destroy current activity
        }else {
            finish();//first destroy then go back
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.insert_detailsof_l_m_g, new SearchFragment()).commit();
        }
    }
}