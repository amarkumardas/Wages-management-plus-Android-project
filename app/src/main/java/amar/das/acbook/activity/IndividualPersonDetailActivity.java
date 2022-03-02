package amar.das.acbook.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.adapters.SearchAdapter;
import amar.das.acbook.adapters.WagesDetailsAdapter;
import amar.das.acbook.databinding.ActivityIndividualPersonDetailBinding;
import amar.das.acbook.model.SearchModel;
import amar.das.acbook.model.WagesDetailsModel;

public class IndividualPersonDetailActivity extends AppCompatActivity {
 ActivityIndividualPersonDetailBinding binding;

//for recording variable declaration
    MediaRecorder mediaRecorder;
    long mstartingTimeMillis=0;
    long mElapsedMillis=0;
    File file;
    String fileName;
    MediaPlayer mediaPlayer;

    PersonRecordDatabase db;
    private String fromIntentPersonId;
    Boolean mStartRecording =false;
    int arr[]=new int[7];

    ArrayList<WagesDetailsModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0); //we have used overridePendingTransition(), it is used to remove activity create animation while re-creating activity.
        binding = ActivityIndividualPersonDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("ID")) {//every operation will be perform based on id
            db = new PersonRecordDatabase(this);//on start only database should be create
            fromIntentPersonId = getIntent().getStringExtra("ID");//getting data from intent
            //db.updateTable3("UPDATE "+db.TABLE_NAME3+" SET R1='10',R2='20',R3='30',R4='40' WHERE ID='"+fromIntentPersonId+"'");

            //***********setting skill top of layout**********************************************
            Cursor defaultSkillCursor=db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
            defaultSkillCursor.moveToFirst();
            binding.hardcodedM.setText(defaultSkillCursor.getString(0));
            binding.p1TextTv.setText(defaultSkillCursor.getString(0)+"  =");//default calculation skill
            defaultSkillCursor.close();


             Cursor sumCursor=db.getData("SELECT SUM(WAGES),SUM(P1),SUM(P2),SUM(P3),SUM(P4),SUM(DEPOSIT) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
             sumCursor.moveToFirst();

             binding.blueTotalWagesTv.setText(sumCursor.getString(0));
             binding.blueTotalp1Tv.setText(sumCursor.getString(1));
             binding.totalP1CountTv.setText(sumCursor.getString(1));//default skill
                    //sum deposit
             if(sumCursor.getString(5) != null) {//if there is deposit then set visibility visible or else layout visibility GONE
                 binding.totalDepositAmountTv.setText("= " + sumCursor.getString(5));
             }else
                 binding.totalDepositAmountLayout.setVisibility(View.GONE);

            Cursor skillCursor=db.getData("SELECT SKILL1,SKILL2,SKILL3,R1,R2,R3,R4 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            if(skillCursor != null) {
                skillCursor.moveToFirst();
                int indicate = get_indicator(fromIntentPersonId);
                                //R1
                if(skillCursor.getInt(3) != 0) {
                                                    //R1
                    binding.p1RateTv.setText(skillCursor.getString(3));//default skill
                                                                       //    R1 * p1
                    binding.totalP1AmountTv.setText("= "+skillCursor.getInt(3)*sumCursor.getInt(1));//default skill
                }else
                    binding.totalP1AmountTv.setText("= NO RATE PROVIDED");//default skill
                               //total wages
                if(sumCursor.getString(0) !=null) {//if total wages is not null then set total wages
                    binding.wagesTotalAmountTv.setText(sumCursor.getString(0));//total wages set
                }
                     if(indicate==1) {
                         Toast.makeText(this, "indicator: "+indicate, Toast.LENGTH_SHORT).show();

                            initializeValues(sumCursor,skillCursor);
                     }



                binding.p2Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                binding.p3Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                binding.p4Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize

                if (indicate == 2) {
                    binding.hardcodedL1.setText(skillCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.p2Layout.setVisibility(View.VISIBLE);

                } else if (indicate == 3) {
                    binding.hardcodedL1.setText(skillCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.hardcodedL2.setText(skillCursor.getString(1));
                    binding.blueTotalp3Tv.setText(sumCursor.getString(3));
                    binding.p2Layout.setVisibility(View.VISIBLE);
                    binding.p3Layout.setVisibility(View.VISIBLE);

                } else if (indicate == 4) {
                    binding.hardcodedL1.setText(skillCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.hardcodedL2.setText(skillCursor.getString(1));
                    binding.blueTotalp3Tv.setText(sumCursor.getString(3));
                    binding.hardcodedL3.setText(skillCursor.getString(2));
                    binding.blueTotalp4Tv.setText(sumCursor.getString(4));
                    binding.p2Layout.setVisibility(View.VISIBLE);
                    binding.p3Layout.setVisibility(View.VISIBLE);
                    binding.p4Layout.setVisibility(View.VISIBLE);

                }
            }
            skillCursor.close();
            sumCursor.close();
            //***********Done setting skill**********************************************
            //*******************Recycler view********************************************
              Cursor allDataCursor=db.getData("SELECT DATE,MICPATH,DESCRIPTION,WAGES,DEPOSIT,P1,P2,P3,P4,ID FROM "+db.TABLE_NAME2+" WHERE ID='"+fromIntentPersonId+"'");
              dataList=new ArrayList<>();
            while(allDataCursor.moveToNext()){
                WagesDetailsModel model=new WagesDetailsModel();
                model.setDate(allDataCursor.getString(0));
                model.setMicPath(allDataCursor.getString(1));
                model.setDescription(allDataCursor.getString(2));
                model.setWages(allDataCursor.getInt(3));
                model.setDeposit(allDataCursor.getInt(4));
                model.setP1(allDataCursor.getInt(5));
                model.setP2(allDataCursor.getInt(6));
                model.setP3(allDataCursor.getInt(7));
                model.setP4(allDataCursor.getInt(8));
                model.setId(allDataCursor.getString(9));
                dataList.add(model);
            }
            allDataCursor.close();
            WagesDetailsAdapter wagesDetailsAdapter=new WagesDetailsAdapter(this,dataList);

            binding.singleRecordRecy.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            binding.singleRecordRecy.setAdapter(wagesDetailsAdapter);
            //*******************done Recycler view********************************************
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
                Toast.makeText(this, "NO DATA IN CURSOR", Toast.LENGTH_SHORT).show();
            }

            //to open dialpaid
            binding.callTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (cursor.getString(5).length() == 10) {
                        Intent callingIntent = new Intent(Intent.ACTION_DIAL);
                        callingIntent.setData(Uri.parse("tel:+91" + cursor.getString(5)));
                        startActivity(callingIntent);
                    } else
                        Toast.makeText(IndividualPersonDetailActivity.this, "NO PHONE NUMBER ADDED", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            binding.editTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(getBaseContext(), InsertDataActivity.class);
                    intent.putExtra("ID", fromIntentPersonId);
                    startActivity(intent);
                    finish();//while going to other activity so destroy  this current activity so that while coming back we will see refresh activity
                    return false;
                }
            });
            // cursor.close(); it should not be close because of call action to perform then we need cursor
        } else
            Toast.makeText(this, "NO ID FROM OTHER INTENT", Toast.LENGTH_SHORT).show();

        //to insert data in recyclerview
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arr=new int[7];//so that when again enter data fresh array will be created
                 insertDataToRecyclerView_ALertDialogBox(get_indicator(fromIntentPersonId) );
            }
        });
    }
    private void initializeValues(Cursor sumCursor,Cursor skillCursor){
        Toast.makeText(this, "inside", Toast.LENGTH_SHORT).show();
        //deposit                              rate                                wages
        if(sumCursor.getString(5) != null && skillCursor.getInt(3) != 0 && sumCursor.getInt(0) !=0) {
            Toast.makeText(this, "deposit and rate and wages", Toast.LENGTH_SHORT).show();
            //                                                       deposit+                       (R1*SUMP1)
            binding.workTotalAmountTv.setText("- " + (sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1))));
            //                   deposit+                       (R1*SUMP1)                     total wages
            if ((sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1))) < sumCursor.getInt(0)) {
                binding.advanceOrBalanceTv.setTextColor(Color.RED);
                //                                                   total wages -                  deposit+                       (R1*SUMP1)
                binding.advanceOrBalanceTv.setText("= " + (sumCursor.getInt(0) - (sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1)))));
                //deposit+                      (R1*SUMP1)                     total wages
            } else if ((sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1))) >= sumCursor.getInt(0)) {//>= is given because of green color
                binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
                //                                                    deposit+                     (R1*SUMP1)                     -total wages
                binding.advanceOrBalanceTv.setText("= " + ((sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1))) - sumCursor.getInt(0)));
            }
            //no deposit                              rate                                   wages
        } else if (sumCursor.getString(5) == null && skillCursor.getInt(3) != 0 && sumCursor.getInt(0) !=0 ) {
            Toast.makeText(this, "no deposit and rate and wages", Toast.LENGTH_SHORT).show();
            //                                                             (R1*SUMP1)
            binding.workTotalAmountTv.setText("- " + (skillCursor.getInt(3) * sumCursor.getInt(1)));
            //             (R1*SUMP1)                                     total wages
            if ( ((skillCursor.getInt(3) * sumCursor.getInt(1))) < sumCursor.getInt(0)) {
                binding.advanceOrBalanceTv.setTextColor(Color.RED);
                //                                                   total wages -                        (R1*SUMP1)
                binding.advanceOrBalanceTv.setText("= " + (sumCursor.getInt(0) - ((skillCursor.getInt(3) * sumCursor.getInt(1)))));
                //                            (R1*SUMP1)                     total wages
            } else if (((skillCursor.getInt(3) * sumCursor.getInt(1))) >= sumCursor.getInt(0)) {//equal is given because of green color
                binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
                //                                                                (R1*SUMP1)                    -total wages
                binding.advanceOrBalanceTv.setText("= " + (((skillCursor.getInt(3) * sumCursor.getInt(1))) - sumCursor.getInt(0)));

           }

            //deposit                            no  R1                                   no wage
        }else if(sumCursor.getString(5) != null && skillCursor.getInt(3) == 0 && sumCursor.getInt(0)== 0){
            Toast.makeText(this, "deposit and no rate no wages", Toast.LENGTH_SHORT).show();
            //                                           deposit
            binding.workTotalAmountTv.setText(" - "+sumCursor.getString(5));
            //                                               deposit
            binding.advanceOrBalanceTv.setText("= " +sumCursor.getString(5));
            binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
            //deposit                            no  R1                                     wages
        }else if(sumCursor.getString(5) != null && skillCursor.getInt(3) == 0 && sumCursor.getInt(0)!= 0){
            Toast.makeText(this, "deposit and no rate and wages", Toast.LENGTH_SHORT).show();

            binding.workTotalAmountTv.setText(" - "+sumCursor.getInt(5));
            //             deposit              total wages
            if ( (sumCursor.getInt(5) ) < sumCursor.getInt(0)) {
                binding.advanceOrBalanceTv.setTextColor(Color.RED);
                //                                                   total wages -    deposit
                binding.advanceOrBalanceTv.setText("= " + (sumCursor.getInt(0) - ( sumCursor.getInt(5))));
                //             deposit              total wages
            } else if ( (sumCursor.getInt(5) ) > sumCursor.getInt(0)) {
                binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
                //                                                   deposit      -total wages
                binding.advanceOrBalanceTv.setText("= " + (sumCursor.getInt(5) - sumCursor.getInt(0)));
                //             deposit              total wages
            }else if((sumCursor.getInt(5) ) == sumCursor.getInt(0)){
                binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
                binding.advanceOrBalanceTv.setText("= 0");
            }
            //no deposit                            no  R1                                     wages
        }else if(sumCursor.getString(5) == null && skillCursor.getInt(3) == 0 && sumCursor.getInt(0)!= 0){
            Toast.makeText(this, "no deposit and no rate and wages", Toast.LENGTH_SHORT).show();
                binding.advanceOrBalanceTv.setTextColor(Color.RED);
                binding.advanceOrBalanceTv.setText("= " + sumCursor.getInt(0));
               //no deposit                             R1                                    no wages
        }else if(sumCursor.getString(5) == null && skillCursor.getInt(3) != 0 && sumCursor.getInt(0)== 0){
            Toast.makeText(this, "no deposit and  rate and no wages", Toast.LENGTH_SHORT).show();
                                                           //      (SUMP1)*R1
            binding.workTotalAmountTv.setText(" - "+(sumCursor.getInt(1)*skillCursor.getInt(3)));
                                                                       //  R1*(SUMP1)
            binding.advanceOrBalanceTv.setText("= "+(skillCursor.getInt(3)*sumCursor.getInt(1)));
            binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
                   //deposit                              rate                               no wages
        }else if(sumCursor.getString(5) != null && skillCursor.getInt(3) != 0 && sumCursor.getInt(0) ==0){
            Toast.makeText(this, "  deposit and  rate and no wages", Toast.LENGTH_SHORT).show();
            //                                                       deposit+                       (R1*SUMP1)
            binding.workTotalAmountTv.setText(" - "+((sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1)))));
            binding.advanceOrBalanceTv.setText("= "+((sumCursor.getInt(5) + (skillCursor.getInt(3) * sumCursor.getInt(1)))));
            binding.advanceOrBalanceTv.setTextColor(Color.GREEN);
        }
    }
    private int get_indicator(String PersonId) {
         Cursor cursor=db.getData("SELECT INDICATOR FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
         String type="";
         if(cursor != null){
             cursor.moveToFirst();
                if(cursor.getString(0) == null) {
                     return 1;
                } else
                     return Integer.parseInt(cursor.getString(0));
         }else
             Toast.makeText(this, "No indicator: "+type, Toast.LENGTH_SHORT).show();

        return 1;//by default 1
    }
    private void insertDataToRecyclerView_ALertDialogBox(int indicator) {
        AlertDialog.Builder mycustomDialog=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
        LayoutInflater inflater=LayoutInflater.from(IndividualPersonDetailActivity.this);

        View myView=inflater.inflate(R.layout.input_data_to_recycler,null);//myView contain all layout view ids
        mycustomDialog.setView(myView);//set custom layout to alert dialog
        mycustomDialog.setCancelable(false);//if user touch to other place then dialog will not be close

        final AlertDialog dialog=mycustomDialog.create();//mycustomDialog varialble cannot be use in inner class so creating another final varialbe  to use in inner class

        TextView deposit_btn_tv=myView.findViewById(R.id.to_deposit_tv);
        TextView hardcodedP1=myView.findViewById(R.id.hardcoded_p1_tv);
        TextView hardcodedP2=myView.findViewById(R.id.hardcoded_p2_tv);
        TextView hardcodedP3=myView.findViewById(R.id.hardcoded_p3_tv);
        TextView hardcodedP4=myView.findViewById(R.id.hardcoded_p4_tv);
        TextView micIcon=myView.findViewById(R.id.mic_tv);
        TextView dateIcon=myView.findViewById(R.id.date_icon_tv);
        TextView inputDate=myView.findViewById(R.id.input_date_tv);
        TextView saveAudio=myView.findViewById(R.id.save_audio_tv);

        Chronometer playAudioChronometer =myView.findViewById(R.id.chronometer);

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
                dialog.dismiss();//while going to other activity dismiss dialog otherwise window leak
                finish();//while going to other activity so destroy  this current activity so that while coming back we will see refresh activity
                startActivity(intent);
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

        //initially every field will be invisible based on indicator others fields will be visible
        hardcodedP2.setVisibility(View.GONE);
        inputP2.setVisibility(View.GONE);
        hardcodedP3.setVisibility(View.GONE);
        inputP3.setVisibility(View.GONE);
        hardcodedP4.setVisibility(View.GONE);
        inputP4.setVisibility(View.GONE);

        //**************************************Setting skills*******************************************
        //CUSTOMIZATION: initially in person skill or type is M,L or G then according to that layout will be customised
        //hardcodedP1,inputP1 by default visible so no need to mention if(indicator == 1) {
        Cursor cursordefault=db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
        cursordefault.moveToFirst();//no need to check  cursordefault !=null because for sure TYPE data is present
        hardcodedP1.setText(cursordefault.getString(0));
        cursordefault.close();

        Cursor skillsCursor=db.getData("SELECT SKILL1,SKILL2,SKILL3 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
        if(skillsCursor != null) {
            skillsCursor.moveToFirst();
            if (indicator == 2) {//two person
                // hardcodedP1,inputP1 by default visible so no need to mention
                hardcodedP2.setVisibility(View.VISIBLE);
                inputP2.setVisibility(View.VISIBLE);
                hardcodedP2.setText(skillsCursor.getString(0));
            } else if (indicator == 3) {//three person
                //hardcodedP1,inputP1 by default visible so no need to mention
                hardcodedP2.setVisibility(View.VISIBLE);
                inputP2.setVisibility(View.VISIBLE);
                hardcodedP2.setText(skillsCursor.getString(0));

                hardcodedP3.setVisibility(View.VISIBLE);
                hardcodedP3.setText(skillsCursor.getString(1));
                inputP3.setVisibility(View.VISIBLE);
            } else if (indicator == 4) {////two person
                //hardcodedP1,inputP1 by default visible so no need to mention
                hardcodedP2.setVisibility(View.VISIBLE);
                inputP2.setVisibility(View.VISIBLE);
                hardcodedP2.setText(skillsCursor.getString(0));

                hardcodedP3.setVisibility(View.VISIBLE);
                hardcodedP3.setText(skillsCursor.getString(1));
                inputP3.setVisibility(View.VISIBLE);

                hardcodedP4.setVisibility(View.VISIBLE);
                hardcodedP4.setText(skillsCursor.getString(2));
                inputP4.setVisibility(View.VISIBLE);
            }
        }
        skillsCursor.close();
        //**************************************done setting skills*******************************************
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //*********************************common to all indicator 1,2,3,4*******************
                int p1,p2,p3,p4;//this default value is taken when user do enter date to fileds
                p1=p2=p3=p4=0;
                int wages=0;
                String remarks=null;
                String micPath=null;
                String date=inputDate.getText().toString();//date will be inserted automatically

                if(file !=null){//if file is not null then only it execute otherwise nothing will be inserted
                    micPath=file.getAbsolutePath();
                    arr[5]=1;
                    file=null;//after path is saved then file=null so that next time while entering data it should not take default value ie micPath=null
                }
                else
                    arr[5]=0;

                if(description.getText().toString().length() >=1){//to prevent nullpointer exception
                    remarks=description.getText().toString().trim();
                    arr[6]=1;
                }
                else
                    arr[6]=0;

                Boolean isWrongData= isEnterDataIsWrong(arr);
                Boolean isDataPresent= isDataPresent(arr);
                if(isDataPresent==true && isWrongData==false ) {//means if data is present then check is it right data or not .if condition is false then default value will be taken
                    if (toGive_Amount.getText().toString().length() >= 1) {//to prevent nullpointer exception
                        wages = Integer.parseInt(toGive_Amount.getText().toString().trim());
                    }
                    //>= if user enter only one digit then >= is important otherwise default value will be set
                    if(inputP1.getText().toString().length() >=1) {//to prevent nullpointer exception
                        p1 = Integer.parseInt(inputP1.getText().toString().trim());//converted to float and stored
                    }
                }
                //*********************************  all the upper code are common to all indicator 1,2,3,4*******************

                if(indicator==1) {
                    if (isDataPresent == true && isWrongData == false) {//it is important means if data is present then check is it right data or not.if condition is false then this message will be displayed "Correct the Data or Cancel and Enter again"
                        //insert to database
                        Boolean success = db.insert_1_Person_WithWagesTable2(fromIntentPersonId, date, micPath, remarks, wages, p1, "0");
                        if (success == true) {
                            displResult(wages + "          " + p1, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks + "\n\nMICPATH: " + micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "Failed to Inserted", Toast.LENGTH_LONG).show();
                    } else//once user enter wrong data and left blank then user wound be able to save because array value would not be change it will be 2 so  user have to "Cancel and enter again" if use dont leave blank then it will save successfully
                        Toast.makeText(IndividualPersonDetailActivity.this, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();

                } else if(indicator==2){
                    //p1 is automatically added
                    if(isDataPresent==true && isWrongData==false ) {
                        if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                        }
                        //insert to database
                        Boolean success = db.insert_2_Person_WithWagesTable2(fromIntentPersonId, date, micPath, remarks, wages, p1, p2, "0");
                        if (success == true) {
                            displResult(wages+"          "+p1+"     "+p2,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "Failed to Inserted", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();

                }else if(indicator==3){
                    if(isDataPresent==true && isWrongData==false ) {
                        if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                        }
                        if (inputP3.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p3 = Integer.parseInt(inputP3.getText().toString().trim());//converted to float and stored
                        }
                        //insert to database
                        Boolean success = db.insert_3_Person_WithWagesTable2(fromIntentPersonId, date, micPath, remarks, wages, p1, p2, p3, "0");
                        if (success == true) {
                            displResult(wages+"          "+p1+"     "+p2+"     "+p3,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "Failed to Inserted", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();


                }else if(indicator==4) {
                    if (isDataPresent == true && isWrongData == false) {
                        if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception.If user do not enter any data then that time it will save from crashing app.So due to this condition if field is empty then default value will be taken
                            p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                        }
                        if (inputP3.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p3 = Integer.parseInt(inputP3.getText().toString().trim());//converted to float and stored
                        }
                        if (inputP4.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p4 = Integer.parseInt(inputP4.getText().toString().trim());//converted to float and stored
                        }
                        //insert to database
                        Boolean success = db.insert_4_Person_WithWagesTable2(fromIntentPersonId, date, micPath, remarks, wages, p1, p2, p3, p4, "0");
                        if (success == true) {
                            displResult(wages+"          "+p1+"     "+p2+"     "+p3+"     "+p4,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "Failed to Inserted", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();
                }
            }
        });
        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking for permission
                if(checkPermission()==true){
                    if (mStartRecording) {//initially false
                        //while recording user should not perform other task like entering date while recording because app will crash so set all field to setEnabled(false);
                        inputP1.setEnabled(false);
                        inputP2.setEnabled(false);
                        inputP3.setEnabled(false);
                        inputP4.setEnabled(false);
                        toGive_Amount.setEnabled(false);
                        description.setEnabled(false);
                        dateIcon.setEnabled(false);
                        save.setEnabled(false);
                        cancel.setEnabled(false);

                        playAudioChronometer.setBase(SystemClock.elapsedRealtime());//In Android, Chronometer is a class that implements a simple timer. Chronometer is a subclass of TextView. This class helps us to add a timer in our app.
                        playAudioChronometer.start();
                        playAudioChronometer.setEnabled(false);//when user press save button then set to true playAudioChronometer.setEnabled(true);
                        saveAudio.setBackgroundResource(R.drawable.ic_green_sharp_done_sharp_tick_20);//changing tick color to green so that user can feel to press to save
                        micIcon.setEnabled(false);
                        micIcon.setBackgroundResource(R.drawable.black_sharp_mic_24);//change color when user click


                        Toast.makeText(IndividualPersonDetailActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();

                        //be carefull take only getExternalFilesDir( null ) https://stackoverflow.com/questions/59017202/mediarecorder-stop-failed
                        File folder = new File(getExternalFilesDir(null) + "/acBookMicRecording");//Creating File directory in phone

                        if (!folder.exists()) {//if folder not exist
                            Toast.makeText(IndividualPersonDetailActivity.this, "Creating acBookMicRecording folder to store audios", Toast.LENGTH_LONG).show();
                            folder.mkdir();//create folder
                        }

                        startRecordingVoice();
                        IndividualPersonDetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //while the user is recording screen should be on. it should not close

                    } else {//if recording is not started then stop
                        Toast.makeText(IndividualPersonDetailActivity.this, "Again Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
                    }
                    mStartRecording = !mStartRecording;//so that user should click 2 times to start recording

                }else//request for permission
                    ActivityCompat.requestPermissions(IndividualPersonDetailActivity.this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},21);
            }
        });
        playAudioChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file != null) {//checking for null pointer Exception
                    Toast.makeText(IndividualPersonDetailActivity.this, "Audio Playing", Toast.LENGTH_SHORT).show();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file.getAbsolutePath());//passing the path where this audio is saved
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(IndividualPersonDetailActivity.this, "Audio Playing", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }else
                    Toast.makeText(IndividualPersonDetailActivity.this, "Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
            }
        });
        saveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaRecorder !=null){
                    //after clicking save audion then setEnabled to true so that user can enter data to fields
                    inputP1.setEnabled(true);
                    inputP2.setEnabled(true);
                    inputP3.setEnabled(true);
                    inputP4.setEnabled(true);
                    toGive_Amount.setEnabled(true);
                    description.setEnabled(true);
                    dateIcon.setEnabled(true);
                    save.setEnabled(true);
                    cancel.setEnabled(true);

                    playAudioChronometer.setTextColor(getResources().getColor(R.color.green));//changind text color to green to give feel that is saved
                    micIcon.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);//set background image to cancel
                    stopAndSaveRecordingPathToDB();
                    playAudioChronometer.stop();//stopping chronometer
                    micIcon.setEnabled(false);//so that user cannot press again this button
                    saveAudio.setEnabled(false);//even this button user should not click again
                    playAudioChronometer.setEnabled(true);//when audio is save then user will be able to play
                }else
                    Toast.makeText(IndividualPersonDetailActivity.this, "Tab on MIC to Start Recording", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        toGive_Amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String amount=toGive_Amount.getText().toString().trim();
                toGive_Amount.setTextColor(Color.BLACK);
                save.setEnabled(true);
                arr[4]=1;//means data is inserted
                if(!amount.matches("[0-9]+")){//no space or . or ,
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                    toGive_Amount.setTextColor(Color.RED);
                    save.setEnabled(false);
                    arr[4]=2;//means wrong data
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        inputP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP1.getText().toString().trim();
                inputP1.setTextColor(Color.BLACK);
                save.setEnabled(true);
                arr[0]=1;//means data is inserted
                if(!p11.matches("[0-9]+")){//"[.]?[0-9]+[.]?[0-9]*" for float
                    inputP1.setTextColor(Color.RED);
                    save.setEnabled(false);
                    arr[0]=2;//means wrong data
                     Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        inputP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP2.getText().toString().trim();
                inputP2.setTextColor(Color.BLACK);
                save.setEnabled(true);
                arr[1]=1;//means data is inserted
                if(!p11.matches("[0-9]+")){// "[.]?[0-9]+[.]?[0-9]*"
                    inputP2.setTextColor(Color.RED);
                    save.setEnabled(false);
                    arr[1]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        inputP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP3.getText().toString().trim();
                inputP3.setTextColor(Color.BLACK);
                save.setEnabled(true);
                arr[2]=1;//means data is inserted
                if(!p11.matches("[0-9]+")){//space or , or - is restricted
                    inputP3.setTextColor(Color.RED);
                    save.setEnabled(false);
                    arr[2]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        inputP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP4.getText().toString().trim();
                inputP4.setTextColor(Color.BLACK);
                save.setEnabled(true);
                arr[3]=1;//means data is inserted
                if(!p11.matches("[0-9]+")){//space or , or - is restricted
                    inputP4.setTextColor(Color.RED);
                    save.setEnabled(false);
                    arr[3]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
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
        if( (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
            return true;
        }else {
            return false;
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
        Toast.makeText(IndividualPersonDetailActivity.this, "RECORDING", Toast.LENGTH_SHORT).show();
    }
    private  void stopAndSaveRecordingPathToDB(){
        mediaRecorder.stop();
        mElapsedMillis=(System.currentTimeMillis()-mstartingTimeMillis);
        mediaRecorder.release();
        mediaRecorder=null;
        // Toast.makeText(this, "Recording SAVED "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
    private void displResult(String title,String message) {
        AlertDialog.Builder showDataFromDataBase=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
        showDataFromDataBase.setCancelable(false);
        showDataFromDataBase.setTitle(title);
        showDataFromDataBase.setMessage(message);
        showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();//close current dialog
                Intent intent=new Intent(IndividualPersonDetailActivity.this,IndividualPersonDetailActivity.class);
                intent.putExtra("ID",fromIntentPersonId);
                finish();//while going to other activity so destroy  this current activity so that while coming back we will see refresh activity
                startActivity(intent);
            }
        });
        showDataFromDataBase.create().show();
    }
}

