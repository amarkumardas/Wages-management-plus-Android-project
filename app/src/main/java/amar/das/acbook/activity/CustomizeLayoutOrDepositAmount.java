package amar.das.acbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.ActivityCustomizeLayoutOrDepositAmountBinding;

public class CustomizeLayoutOrDepositAmount extends AppCompatActivity {
  ActivityCustomizeLayoutOrDepositAmountBinding binding;
    PersonRecordDatabase db;
    private  String fromIntentPersonId;
    //for recording variable declaration
    MediaRecorder mediaRecorder;
    long mstartingTimeMillis=0;
    long mElapsedMillis=0;
    File file;
    String fileName;
    MediaPlayer mediaPlayer;
    Boolean mStartRecording =false;
    int arr[]=new int[3];//to give information which field is empty or contain data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0); //we have used overridePendingTransition(), it is used to remove activity create animation while re-creating activity.
        binding = ActivityCustomizeLayoutOrDepositAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("ID")) {//if id present than only operation will be performed
            db = new PersonRecordDatabase(this);//on start only database should be create
            fromIntentPersonId = getIntent().getStringExtra("ID");//getting id from intent

            //setting adapter for spinner
            String[] addOrRemoveMLG = getResources().getStringArray(R.array.addOrRemoveMlG);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(CustomizeLayoutOrDepositAmount.this, android.R.layout.select_dialog_item, addOrRemoveMLG);
            binding.customSpinnerSetting.setAdapter(adapter);
            // when activity is loaded spinner item is selected automatically so to avoid this we are using customSpinnerSetting.setSelection(initialposition, false);
//            int initialposition = binding.customSpinnerSetting.getSelectedItemPosition();
//            binding.customSpinnerSetting.setSelection(initialposition, false);//clearing auto selected item
            binding.customSpinnerSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            String a = adapterView.getItemAtPosition(pos).toString();

                            Cursor cursor=db.getData("SELECT SKILL1,SKILL2,SKILL3 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
                            cursor.moveToFirst();//skill which is null there skill is updated
                            if (a.equals("ADD L")) {//adding L means p2
                                if(cursor.getString(0) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL1='L' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD L","STATUS: SUCCESS","FAILED TO ADD L","STATUS: FAILED");
//                           if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL1='L' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId)){
//                               displResult("SUCCESSFULLY  ADD L","STATUS: SUCCESS");
//                           }else
//                               displResult("FAILED TO ADD L","STATUS: FAILED");

                                }else if(cursor.getString(1) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL2='L' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY ADD L","STATUS: SUCCESS","FAILED TO ADD L","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL2='L' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY ADD L","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO ADD L","STATUS: FAILED");

                                }else if(cursor.getString(2) == null) {
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL3='L' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD L","STATUS: SUCCESS","FAILED TO  ADD L","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL3='L' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY  ADD L","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO  ADD L","STATUS: FAILED");

                                }else
                                    displResult("ONLY 4 PERSON ALLOWED TO ADDED","STATUS: CAN'T ADD MORE L");

                            } else if (a.equals("ADD M")) {//adding M p3
                                if(cursor.getString(0) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL1='M' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD M","STATUS: SUCCESS","FAILED TO ADD M","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL1='M' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY  ADD M","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO ADD M","STATUS: FAILED");

                                }else if(cursor.getString(1) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL2='M' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY ADD M","STATUS: SUCCESS","FAILED TO ADD M","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL2='M' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY ADD M","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO ADD M","STATUS: FAILED");

                                }else if(cursor.getString(2) == null) {
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL3='M' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD M","STATUS: SUCCESS","SUCCESSFULLY  ADD M","STATUS: SUCCESS");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL3='M' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY  ADD M","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO  ADD M","STATUS: FAILED");
                                }else
                                    displResult("ONLY 4 PERSON ALLOWED TO ADDED","STATUS: CAN'T ADD MORE M");
                            } else if (a.equals("ADD G")) {//adding G p4
                                if(cursor.getString(0) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL1='G' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD G","STATUS: SUCCESS","FAILED TO ADD G","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL1='G' , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY  ADD G","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO ADD G","STATUS: FAILED");

                                }else if(cursor.getString(1) == null){
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL2='G' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY ADD G","STATUS: SUCCESS","FAILED TO ADD G","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL2='G' ,INDICATOR="+3+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY ADD G","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO ADD G","STATUS: FAILED");

                                }else if(cursor.getString(2) == null) {
                                    showDialogAsMessage("UPDATE "+db.TABLE_NAME3+" SET SKILL3='G' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId,"SUCCESSFULLY  ADD G","STATUS: SUCCESS","FAILED TO  ADD G","STATUS: FAILED");
//                            if(db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET SKILL3='G' ,INDICATOR="+4+" WHERE ID= "+fromIntentPersonId)){
//                                displResult("SUCCESSFULLY  ADD G","STATUS: SUCCESS");
//                            }else
//                                displResult("FAILED TO  ADD G","STATUS: FAILED");
                                }else
                                    displResult("ONLY 4 PERSON ALLOWED TO ADDED","STATUS: CAN'T ADD MORE G");

                            } else if (a.equals("REMOVE M") || a.equals("REMOVE L") || a.equals("REMOVE G")) {//removing
                                //First getting indicator to decide whether delete or not.if indicator is null then cant delete because by default M or L or G present.If indicator is 2,3,4 then checking data is present or not if present then dont delete else delete
                                Cursor cursorIndi=db.getData("SELECT INDICATOR FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
                                if(cursorIndi != null){
                                    cursorIndi.moveToFirst();
                                    if(cursorIndi.getString(0) == null) {//person1
                                        displResult("CAN'T REMOVE DEFAULT SETTING","STATUS: FAILED");//default M or L or G

                                    }else if(cursorIndi.getString(0).equals("2")){//person2
                                        Cursor result=db.getData("SELECT SUM(P2) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
                                        result.moveToFirst();
                                        if(result.getInt(0) == 0){//Means no data IN P2
                                            db.updateTable("UPDATE "+db.TABLE_NAME3+" SET SKILL1= "+null+" , INDICATOR="+1+" WHERE ID= "+fromIntentPersonId);
                                            displResult("NO DATA PRESENT REMOVED ","STATUS: SUCCESS");
                                        }else if(result.getInt(0) >= 1){
                                            displResult("CAN'T REMOVE","BECAUSE DATA IS PRESENT.SUM = "+result.getInt(0));
                                        }

                                    }else if(cursorIndi.getString(0).equals("3")){//person3
                                        Cursor result=db.getData("SELECT SUM(P3) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
                                        result.moveToFirst();
                                        if(result.getInt(0) == 0){//Means no data IN P2
                                            db.updateTable("UPDATE "+db.TABLE_NAME3+" SET SKILL2= "+null+" , INDICATOR="+2+" WHERE ID= "+fromIntentPersonId);
                                            displResult("NO DATA PRESENT REMOVED ","STATUS: SUCCESS");
                                        }else if(result.getInt(0) >= 1){
                                            displResult("CAN'T REMOVE","BECAUSE DATA IS PRESENT.SUM= "+result.getInt(0));
                                        }
                                    }else if(cursorIndi.getString(0).equals("4")){//person4
                                        Cursor result=db.getData("SELECT SUM(P4) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
                                        result.moveToFirst();
                                        if(result.getInt(0) == 0){//Means no data IN P2
                                            db.updateTable("UPDATE "+db.TABLE_NAME3+" SET SKILL3= "+null+" , INDICATOR="+3+" WHERE ID= "+fromIntentPersonId);
                                            displResult("NO DATA PRESENT REMOVED ","STATUS: SUCCESS");
                                        }else if(result.getInt(0) >= 1){
                                            displResult("CAN'T REMOVE","BECAUSE DATA IS PRESENT.SUM= "+result.getInt(0));
                                        }
                                    }
                                }else
                                    Toast.makeText(CustomizeLayoutOrDepositAmount.this, "NO DATA IN CURSOR", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) { }
                    });


            //to automatically set date to textView
            final Calendar current=Calendar.getInstance();//to get current date and time
            int cYear=current.get(Calendar.YEAR);
            int cMonth=current.get(Calendar.MONTH);
            int cDayOfMonth=current.get(Calendar.DAY_OF_MONTH);

            //Time
            Date d=Calendar.getInstance().getTime();
            SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss a");//a stands for is AM or PM
            String onlyTime = sdf.format(d);
            binding.customTimeTv.setText(onlyTime);//setting time to take time and store in db

            binding.customDateTv.setText(cDayOfMonth+"-"+(cMonth+1)+"-"+cYear);
            binding.customDateIconTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //To show calendar dialog
                    DatePickerDialog datePickerDialog=new DatePickerDialog(CustomizeLayoutOrDepositAmount.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                            binding.customDateTv.setText(dayOfMonth+"-"+(month+1)+"-"+year);//month start from 0 so 1 is added to get right month like 12
                        }
                    },cYear,cMonth,cDayOfMonth);//This variable should be ordered this variable will set date day month to calendar to datePickerDialog so passing it
                    datePickerDialog.show();
                }
            });

            binding.customDepositEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String p11 = binding.customDepositEt.getText().toString().trim();
                    binding.customDepositEt.setTextColor(getResources().getColor(R.color.green));
                    binding.customSaveBtn.setEnabled(true);
                    arr[0]=1;//means data is inserted
                    if (!p11.matches("[0-9]+")) {//space or , or - is restricted"[.]?[0-9]+[.]?[0-9]*"
                        binding.customDepositEt.setTextColor(Color.RED);
                        binding.customSaveBtn.setEnabled(false);
                        arr[0]=2;//means data is inserted
                        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void afterTextChanged(Editable editable) { }
            });
            binding.customSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int depositAmount=0;
                    String remarks=null;
                    String micPath=null;

                    //To get exact time so write code in save button
                    Date d=Calendar.getInstance().getTime();
                    SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss a");//a stands for is AM or PM
                    String onlyTime = sdf.format(d);
                    binding.customTimeTv.setText(onlyTime);//setting time to take time and store in db

                    if(file !=null){//if file is not null then only it execute otherwise nothing will be inserted
                        micPath=file.getAbsolutePath();
                        arr[1]=1;
                    }
                    else
                        arr[1]=0;
                    if(binding.customDescriptionEt.getText().toString().length() >=1){//to prevent nullpointer exception
                        remarks="["+binding.customTimeTv.getText().toString()+"]-[ENTERED]\n\n"+binding.customDescriptionEt.getText().toString().trim();//time is set automatically to remarks if user enter any remarks
                        arr[2]=1;
                    }
                    else
                        arr[2]=0;

                    Boolean isWrongData= isEnterDataIsWrong(arr);//it should be here to get updated result
                    Boolean isDataPresent= isDataPresent(arr);
                    if(isDataPresent==true && isWrongData==false ) {  //means if data is present then check is it right data or not
                        if(binding.customDepositEt.getText().toString().trim().length() >= 1) {
                            depositAmount = Integer.parseInt(binding.customDepositEt.getText().toString().trim());
                        }

                        Boolean success = db.insert_Deposit_Table2(fromIntentPersonId,binding.customDateTv.getText().toString(),binding.customTimeTv.getText().toString(),micPath,remarks,depositAmount,"1");
                        if (success == true) {
                            displResult("DEPOSIT : "+depositAmount,"\nDATE:  "+binding.customDateTv.getText().toString()+"\n\nREMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                        } else
                            Toast.makeText(CustomizeLayoutOrDepositAmount.this, "FAILED TO INSERT", Toast.LENGTH_LONG).show();

                    }else
                        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();
                }
            });
            binding.customCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();//destroy current activity
                    Intent intent=new Intent(CustomizeLayoutOrDepositAmount.this,IndividualPersonDetailActivity.class);
                    intent.putExtra("ID",fromIntentPersonId);
                    startActivity(intent);//while cancelling we will go back to previous Activity with updated activity so passing id to get particular person detail
                }
            });
            binding.customMicIconTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //checking for permission
                    if(checkPermission()==true){
                        if (mStartRecording) {//initially false
                            //while recording user should not perform other task like entering date while recording because app will crash so set all field to setEnabled(false);
                            binding.customDescriptionEt.setEnabled(false);
                            binding.customDepositEt.setEnabled(false);
                            binding.customDateIconTv.setEnabled(false);
                            binding.customSaveBtn.setEnabled(false);
                            binding.customCancelBtn.setEnabled(false);
                            binding.customSpinnerSetting.setEnabled(false);


                            binding.customChronometer.setBase(SystemClock.elapsedRealtime());//In Android, Chronometer is a class that implements a simple timer. Chronometer is a subclass of TextView. This class helps us to add a timer in our app.
                            binding.customChronometer.start();
                            binding.customChronometer.setEnabled(false);//when user press save button then set to true playAudioChronometer.setEnabled(true);
                            binding.customSaveAudioIconTv.setBackgroundResource(R.drawable.ic_green_sharp_done_sharp_tick_20);//changing tick color to green so that user can feel to press to save
                            binding.customMicIconTv.setEnabled(false);
                            binding.customMicIconTv.setBackgroundResource(R.drawable.black_sharp_mic_24);//change color when user click

                            Toast.makeText(CustomizeLayoutOrDepositAmount.this, "RECORDING STARTED", Toast.LENGTH_SHORT).show();

                            //be carefull take only getExternalFilesDir( null ) https://stackoverflow.com/questions/59017202/mediarecorder-stop-failed
                            File folder = new File(getExternalFilesDir(null) + "/acBookMicRecording");//Creating File directory in phone

                            if (!folder.exists()) {//if folder not exist
                                Toast.makeText(CustomizeLayoutOrDepositAmount.this, "Creating acBookMicRecord folder to store audios", Toast.LENGTH_LONG).show();
                                folder.mkdir();//create folder
                            }

                            startRecordingVoice();
                            CustomizeLayoutOrDepositAmount.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //while the user is recording screen should be on. it should not close

                        } else {//if recording is not started then stop
                            Toast.makeText(CustomizeLayoutOrDepositAmount.this, "Again Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
                        }
                        mStartRecording = !mStartRecording;//so that user should click 2 times to start recording

                    }else//request for permission
                        ActivityCompat.requestPermissions(CustomizeLayoutOrDepositAmount.this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},21);
                }
            });
            binding.customChronometer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(file != null) {//checking for null pointer Exception
                        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "AUDIO PLAYING", Toast.LENGTH_SHORT).show();
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(file.getAbsolutePath());//passing the path where this audio is saved
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Toast.makeText(CustomizeLayoutOrDepositAmount.this, "AUDIO PLAYING", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else
                        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
                }
            });
            binding.customSaveAudioIconTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mediaRecorder !=null){
                        //after clicking save audion then setEnabled to true so that user can enter data to fields

                        binding.customDepositEt.setEnabled(true);
                        binding.customDescriptionEt.setEnabled(true);
                        binding.customDateIconTv.setEnabled(true);
                        binding.customSaveBtn.setEnabled(true);
                        binding.customCancelBtn.setEnabled(true);
                        binding.customSpinnerSetting.setEnabled(true);

                        binding.customChronometer.setTextColor(getResources().getColor(R.color.green));//changind text color to green to give feel that is saved
                        binding.customMicIconTv.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);//set background image to cancel
                        stopAndSaveRecordingPathToDB();
                        binding.customChronometer.stop();//stopping chronometer
                        binding.customMicIconTv.setEnabled(false);//so that user cannot press again this button
                        binding.customSaveAudioIconTv.setEnabled(false);//even this button user should not click again
                        binding.customChronometer.setEnabled(true);//when audio is save then user will be able to play
                    }else
                        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
                }
            });

        }else
            Toast.makeText(this, "No ID from other Intent", Toast.LENGTH_SHORT).show();
    }
    public void showDialogAsMessage( String query,String iftitle,String ifmessage,String elsetitle,String elsemessage){
        if(db.updateTable(query)){
            displResult(iftitle,ifmessage);
        }else{
            displResult(elsetitle, elsemessage);
        }
    }
    private void startRecordingVoice() {
        Long  tsLong=System.currentTimeMillis()/1000;//folder name should be unique so taking time as name of mic record so every record name will be different
        String ts=tsLong.toString();
        fileName="audio_"+ts;//file name
        file=new File(getExternalFilesDir( null )+"/acBookMicRecording/"+fileName+".mp3");//path of audio where it is saved in device

        //https://developer.android.com/reference/android/media/MediaRecorder
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//Sets the number of audio channels for recording.
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//Sets the format of the output file produced during recording
        mediaRecorder.setOutputFile(file.getAbsolutePath());//giving file path where fill will be stored
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioChannels(1);//Sets the number of audio channels for recording here setting to 1.

        try{//to start mediaRecorder should be in try catch block
            mediaRecorder.prepare();//first prepare then start
            mediaRecorder.start();
            mstartingTimeMillis=System.currentTimeMillis();
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(CustomizeLayoutOrDepositAmount.this, "RECORDING", Toast.LENGTH_SHORT).show();
    }
    private  void stopAndSaveRecordingPathToDB(){
        mediaRecorder.stop();
        mElapsedMillis=(System.currentTimeMillis()-mstartingTimeMillis);
        mediaRecorder.release();
        mediaRecorder=null;
       // Toast.makeText(this, "Recording SAVED "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
    private Boolean isEnterDataIsWrong(int[] arr) {
        Boolean bool=true;
        int two=0;
        for(int i=0 ;i <arr.length;i++) {
            if (arr[i] == 2)
                two++;
        }
        if(two >=1)//data is wrong
            bool=true;
        else
            bool=false;//data is right
        return bool;
    }
    private void displResult(String title,String message) {
        AlertDialog.Builder showDataFromDataBase=new AlertDialog.Builder(CustomizeLayoutOrDepositAmount.this);
        showDataFromDataBase.setCancelable(false);
        showDataFromDataBase.setTitle(title);
        showDataFromDataBase.setMessage(message);
        showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //after data entered successfully
                finish();//destroy current activity
                Intent intent=new Intent(CustomizeLayoutOrDepositAmount.this,IndividualPersonDetailActivity.class);
                intent.putExtra("ID",fromIntentPersonId);
                startActivity(intent);//while cancelling we will go back to previous Activity with updated activity so passing id to get particular person detail
            }
        });
        showDataFromDataBase.create().show();
    }
    private Boolean isDataPresent(int[] arr){
        Boolean bool=true;
        int sum,one;
        sum=one=0;

        for(int i=0 ;i <arr.length;i++){

            if(arr[i]== 1)
                one++;

            sum=sum+arr[i];
        }
        if(sum == 0)//data is not present
            bool= false;
        else if((one >= 1))//data is present
            bool= true;
        return bool;
    }
    private boolean checkPermission() {//checking for permission of mic and external storage
        if( (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
            return true;
        }else {
            return false;
        }
    }
}