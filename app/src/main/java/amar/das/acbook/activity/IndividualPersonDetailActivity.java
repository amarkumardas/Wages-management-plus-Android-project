package amar.das.acbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.RecordingService;
import amar.das.acbook.databinding.ActivityIndividualPersonDetailBinding;

public class IndividualPersonDetailActivity extends AppCompatActivity {
ActivityIndividualPersonDetailBinding binding;

    PersonRecordDatabase db;
    private String fromIntentPersonId;
    Boolean mStartRecording =false;
    Boolean mPauseRecording=false;
    long timeWhenPaused=0;
    Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0); //we have used overridePendingTransition(), it is used to remove activity create animation while re-creating activity.
        binding = ActivityIndividualPersonDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("ID")) {
            db = new PersonRecordDatabase(this);//on start only database should be create

            fromIntentPersonId = getIntent().getStringExtra("ID");//getting data from intent

            //retrieving data from db
            Cursor cursor = db.getData("SELECT NAME,BANKACCOUNT,IFSCCODE,BANKNAME,AADHARCARD,PHONE,TYPE,FATHERNAME,IMAGE,ACHOLDER,ID FROM " + db.TABLE_NAME1 + " WHERE ID='" + fromIntentPersonId + "'");

            if (cursor != null) {
                cursor.moveToFirst();
                binding.nameTv.setText(cursor.getString(0));
                binding.accountTv.setText(Html.fromHtml("A/C:  " + "<b>" + cursor.getString(1) + "</b>"));
                binding.ifscCodeTv.setText("IFSC:  " + cursor.getString(2));
                binding.bankNameTv.setText("Bank: " + cursor.getString(3));
                binding.aadharTv.setText(Html.fromHtml("Aadhar Card:  " + "<b>" + cursor.getString(4) + "</b>"));
                binding.phoneTv.setText("Phone:  " + cursor.getString(5));
                binding.skillTv.setText(cursor.getString(6));
                binding.fatherNameTv.setText("Father: " + cursor.getString(7));

                if (cursor.getString(5).length() == 10) {//if there is no phone number then show default icon color black else green icon
                    binding.callTv.setBackgroundResource(R.drawable.ic_outline_call_24);
                }

                byte[] image = cursor.getBlob(8);//getting image from db as blob
                //getting bytearray image from DB and converting  to bitmap to set in imageview
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                binding.imageImg.setImageBitmap(bitmap);

                binding.acHolderTv.setText("A/C Holder: " + cursor.getString(9));
                binding.idTv.setText("ID: " + cursor.getString(10));
            } else {
                Toast.makeText(this, "No data in cursor", Toast.LENGTH_SHORT).show();
            }

            //to open dialpaid
            binding.callTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cursor.getString(5).length() == 10) {
                        Intent callingIntent = new Intent(Intent.ACTION_DIAL);
                        callingIntent.setData(Uri.parse("tel:+91" + cursor.getString(5)));
                        startActivity(callingIntent);
                    } else
                        Toast.makeText(IndividualPersonDetailActivity.this, "No Phone number added", Toast.LENGTH_SHORT).show();
                }
            });

            binding.editTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(getBaseContext(), InsertDataActivity.class);
                    intent.putExtra("ID", fromIntentPersonId);
                    startActivity(intent);
                    finish();//while going to other activity so destryo  this current activity so that while coming back we will see refresh activity
                    return false;
                }
            });

            // cursor.close(); it should not be close because of call action to perform then we need cursor
        } else
            Toast.makeText(this, "No ID from other Intent", Toast.LENGTH_SHORT).show();

        //to insert data in recyclerview
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                insertDataToRecyclerView_ALertDialogBox( get_indicator(fromIntentPersonId) );
            }
        });
    }

    private int get_indicator(String PersonId) {
         Cursor cursor=db.getData("SELECT INDICATOR FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
         String type="";
         if(cursor != null){
             cursor.moveToFirst();
             type=cursor.getString(0);
              if(type == null)
                  return 1;//first person
              else if(type.equals(1))
                  return 2;//second person
              else if(type.equals(2))
                  return 3;//third person
              else if(type.equals(3))
                  return 4;//fourth person
         }else
             Toast.makeText(this, "type else"+type, Toast.LENGTH_SHORT).show();
        return 1;
    }

    private void insertDataToRecyclerView_ALertDialogBox(int indicator) {

        AlertDialog.Builder mycustomDialog=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
        LayoutInflater inflater=LayoutInflater.from(IndividualPersonDetailActivity.this);
        View myView=inflater.inflate(R.layout.input_data_to_recycler,null);//myView contain all layout view ids
        mycustomDialog.setView(myView);//set custom layout to alert dialog
        mycustomDialog.setCancelable(false);

        final AlertDialog dialog=mycustomDialog.create();//mycustomDialog varialble cannot be use in inner class so creating another final varialbe  to use in inner class

        TextView deposit_btn_tv=myView.findViewById(R.id.to_deposit_tv);
        TextView hardcodedP1=myView.findViewById(R.id.hardcoded_p1_tv);
        TextView hardcodedP2=myView.findViewById(R.id.hardcoded_p2_tv);
        TextView hardcodedP3=myView.findViewById(R.id.hardcoded_p3_tv);
        TextView hardcodedP4=myView.findViewById(R.id.hardcoded_p4_tv);
        TextView micIcon=myView.findViewById(R.id.mic_tv);
        TextView dateIcon=myView.findViewById(R.id.date_icon_tv);
        TextView inputDate=myView.findViewById(R.id.input_date_tv);

         chronometer =myView.findViewById(R.id.chronometer);

        EditText inputP1=myView.findViewById(R.id.input_p1_et);
        EditText inputP2=myView.findViewById(R.id.input_p2_et);
        EditText inputP3=myView.findViewById(R.id.input_p3_et);
        EditText inputP4=myView.findViewById(R.id.input_p4_et);
        EditText toGive_Amount=myView.findViewById(R.id.wages_et);
        EditText description=myView.findViewById(R.id.enter_description_et);
        Button save=myView.findViewById(R.id.save_btn);
        Button cancel=myView.findViewById(R.id.cancel_btn);

        deposit_btn_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent=new Intent(IndividualPersonDetailActivity.this,CustomizeLayoutOrDepositAmount.class);
                intent.putExtra("ID",fromIntentPersonId);
                startActivity(intent);
                finish();//while going to other activity so destryo  this current activity so that while coming back we will see refresh activity
                return false;
            }
        });

        //to automatically set date to textView
        final Calendar current=Calendar.getInstance();//to get current date and time
        int cYear=current.get(Calendar.YEAR);
        int cMonth=current.get(Calendar.MONTH);
        int cDayOfMonth=current.get(Calendar.DAY_OF_MONTH);
        inputDate.setText(cDayOfMonth+"-"+(cMonth+1)+"-"+cYear);
        dateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //To show calendar dialog
                DatePickerDialog datePickerDialog=new DatePickerDialog(IndividualPersonDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        inputDate.setText(dayOfMonth+"-"+(month+1)+"-"+year);//month start from 0 so 1 is added to get right month like 12
                    }
                },cYear,cMonth,cDayOfMonth);//This variable should be ordered this variable will set date day month to calendar to datePickerDialog so passing it
                datePickerDialog.show();
            }
        });


        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 //onRecord(mstartRecording);
                Intent intent=new Intent(IndividualPersonDetailActivity.this, RecordingService.class);
                if(mStartRecording){
                    micIcon.setBackgroundResource(R.drawable.cancel_sharp_clear_24);//set background image to cancel
                    Toast.makeText(IndividualPersonDetailActivity.this, "Recording started", Toast.LENGTH_SHORT).show();

                    //Creating File directory in phone
                    File folder=new File(Environment.getExternalStorageDirectory()+"/acBookMicRecord");
                    if(!folder.exists()){//if folder not exist
                        folder.mkdir();//create folder
                    }
                    //In Android, Chronometer is a class that implements a simple timer. Chronometer is a subclass of TextView. This class helps us to add a timer in our app.
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    IndividualPersonDetailActivity.this.startService(intent);//to start service
                    IndividualPersonDetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //while the user is recording screen should be on
                    Toast.makeText(IndividualPersonDetailActivity.this, "Recording", Toast.LENGTH_SHORT).show();

                }else{//if recording is not started then stop
                    micIcon.setBackgroundResource(R.drawable.black_sharp_mic_24);
                    chronometer.stop();
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    timeWhenPaused=0;
                    Toast.makeText(IndividualPersonDetailActivity.this, "Tab the button to start Recording", Toast.LENGTH_SHORT).show();

                    stopService(intent);//so screen should not always be on so stop intent
                }

                 mStartRecording =!mStartRecording;
            }
        });

        //initially in person skill or type is M,L or G then according to that layout will be customised
        if(indicator == 1) {//only 1 person
            Cursor cursor=db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
            cursor.moveToFirst();
            hardcodedP1.setText(cursor.getString(0));

            hardcodedP2.setVisibility(View.GONE);
            inputP2.setVisibility(View.GONE);
            hardcodedP3.setVisibility(View.GONE);
            inputP3.setVisibility(View.GONE);
            hardcodedP4.setVisibility(View.GONE);
            inputP4.setVisibility(View.GONE);
        }else if(indicator == 2) {////two person
            Cursor cursor=db.getData("SELECT SKILL1 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
            cursor.moveToFirst();
            hardcodedP2.setText(cursor.getString(0));
            inputP2.setVisibility(View.VISIBLE);

            hardcodedP3.setVisibility(View.GONE);
            inputP3.setVisibility(View.GONE);


            hardcodedP4.setVisibility(View.GONE);
            inputP4.setVisibility(View.GONE);
        }
        else if(indicator == 3) {////two person
            Cursor cursor=db.getData("SELECT SKILL2 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
            cursor.moveToFirst();
            hardcodedP3.setText(cursor.getString(0));
            inputP3.setVisibility(View.VISIBLE);

            hardcodedP4.setVisibility(View.GONE);
            inputP4.setVisibility(View.GONE);

        }else if(indicator == 4) {////two person
            Cursor cursor=db.getData("SELECT SKILL3 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
            cursor.moveToFirst();
            hardcodedP4.setText(cursor.getString(0));
            inputP4.setVisibility(View.VISIBLE);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(indicator==1){
                    float p1= Float.parseFloat(inputP1.getText().toString());//converted to float and stored
                    int wages= Integer.parseInt(toGive_Amount.getText().toString());
                    String remarks=description.getText().toString();
                    String date=inputDate.getText().toString();

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    dialog.show();
    }
   public void onRecord(Boolean start){
    Intent intent=new Intent(IndividualPersonDetailActivity.this, RecordingService.class);

    if(start){

    }

   }

}