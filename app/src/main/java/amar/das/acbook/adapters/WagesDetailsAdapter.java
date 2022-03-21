package amar.das.acbook.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.activity.CustomizeLayoutOrDepositAmount;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.WagesDetailsModel;

public class WagesDetailsAdapter extends RecyclerView.Adapter<WagesDetailsAdapter.ViewHolder> {
    Context context;
    ArrayList<WagesDetailsModel> arrayList;
    PersonRecordDatabase db;
    int indicator;
    boolean bool;
    int arr[]=new int[6];
    String previousDataHold[]=new String[8];
    String fromIntentPersonId;

    //for recording variable declaration
    MediaRecorder mediaRecorder;
    long mstartingTimeMillis=0;
    long mElapsedMillis=0;
    File file;
    String fileName;
    boolean mStartRecording =false;

    final Calendar current=Calendar.getInstance();//to get current date
    String currentDate =current.get(Calendar.DAY_OF_MONTH)+"-"+(current.get(Calendar.MONTH)+1)+"-"+current.get(Calendar.YEAR);

    //OR to get current date
//    final Calendar currentDate=Calendar.getInstance();//to get current date
//    SimpleDateFormat dateFormat1=new SimpleDateFormat("dd-MM-yyyy");//formatting in like 19-03-2022
//    String currentDate=dateFormat1.format(current.getTimeInMillis());
    public WagesDetailsAdapter(Context context, ArrayList<WagesDetailsModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_record_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       WagesDetailsModel data=arrayList.get(position);
       holder.date.setText(data.getDate());
       if(data.getWages() !=0 && data.getDeposit() == 0) {//if wages is there and deposit not there then set wages
           holder.wages.setText(""+data.getWages());
           holder.wages.setTextColor(Color.BLACK);
                 //if we put data.getIsdeposited().equals("1") then unwanted 0 will be set if not data is present so not using it.
       } else if( data.getDeposit() != 0 && data.getWages() == 0  && data.getP1() == 0){//if wages is not there and deposit there then set wages and color to green
           holder.wages.setText(""+data.getDeposit());//while entering deposit then there will be no p1 or p2p3p4 so checking data.getP1() == 0
           holder.wages.setTextColor(context.getResources().getColor(R.color.green));

       }else/**if we dont put else statement then default value will be set*/
           holder.wages.setText("");

       //*************************************Audio and mic*********************************************************
        if((data.getDescription() != null) || data.getMicPath() !=null) {//if audio or description is present then set min icon to green
           holder.spinnerdescAudioIcon.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);
            holder.spinnerdescAudioIcon.setEnabled(true);//if there is data then enable
           bool=true;//means data is present
       }else {
            holder.spinnerdescAudioIcon.setBackgroundResource(R.drawable.black_sharp_mic_24);
            holder.spinnerdescAudioIcon.setEnabled(false);//if there is no data then disable
            bool=false;//means data is not present
        }

       if(bool != false) {//means data is present so it will be clickable so we will set adapter otherwise not
           String[] audioAndDescription = context.getResources().getStringArray(R.array.audioAndDescription);
           ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, audioAndDescription);
           holder.spinnerdescAudioIcon.setAdapter(adapter);//adapter set
       }
        holder.spinnerdescAudioIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//this will only execute when there is data
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String a = adapterView.getItemAtPosition(pos).toString();//get adapter position

                    if (a.equals("AUDIO")) {
                        if (data.getMicPath() != null) {//checking audi is present or not
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(data.getMicPath());//passing the path where this audio is saved
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                Toast.makeText(view.getContext(), "AUDIO PLAYING", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            Toast.makeText(view.getContext(), "NO AUDIO", Toast.LENGTH_SHORT).show();
                    } else if (a.equals("REMARKS")) {
                        if (data.getDescription() != null) {//checking remarks is present or not
                            displResult("REMARKS", data.getDescription());
                        } else
                            Toast.makeText(view.getContext(), "NO REMARKS", Toast.LENGTH_SHORT).show();
                    }
                    //after selecting second time remarks data is not shown so 0 is set so that when second time click it will show data
              //  int initialposition = holder.spinnerdescAudioIcon.getSelectedItemPosition();
                holder.spinnerdescAudioIcon.setSelection(0, false);//clearing auto selected or if we remove this line then only one time we would be able to select audio and remarks
                }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
       //*************************************done audio and mic*********************************************************
        indicator=get_indicator(data.getId());  //showing data of p1 p2p3p4 if data is present
        holder.p1.setText("");//initially all will be blank
        holder.p2.setText("");
        holder.p3.setText("");
        holder.p4.setText("");
        if(data.getP1() != 0) {//default skill will be common to all
            holder.p1.setText("" + data.getP1());
        }
         if(indicator==2){
            if(data.getP2() != 0) {
                holder.p2.setText("" + data.getP2());
            }
            
        }else if(indicator==3){
            if(data.getP2() != 0) {
                holder.p2.setText("" + data.getP2());
            }

            if(data.getP3() != 0) {
                holder.p3.setText("" + data.getP3());
            }

        }else if(indicator==4){
             if(data.getP2() != 0) {
                 holder.p2.setText("" + data.getP2());
             }

             if(data.getP3() != 0) {
                 holder.p3.setText("" + data.getP3());
             }

            if(data.getP4() != 0) {
                holder.p4.setText("" + data.getP4());
            }
        }

         //************************SETTING BACKGROUND COLOR ACCORDING TO PREVIOUS AND TODAYS DATE*******************************
   if (data.getDate().equals(getPreviousDate())) //if data has enterded yesterday then set background to gray
       holder.singleRecordLayout.setBackgroundColor(context.getResources().getColor(R.color.background));
   else if(currentDate.equals(data.getDate()))//if data has enterded today then set background to yellow
       holder.singleRecordLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
    else//if we dont put else statement then other layout also color get change so else is important
       holder.singleRecordLayout.setBackgroundColor(Color.WHITE);
        //************************ DONE SETTING BACKGROUND COLOR ACCORDING TO PREVIOUS AND TODAYS DATE*******************************

         holder.singleRecordLayout.setOnLongClickListener(new View.OnLongClickListener() {//for updating
             @Override
             public boolean onLongClick(View view) {
                 AlertDialog.Builder updateCustomDialog = new AlertDialog.Builder(view.getContext());
                 LayoutInflater inflater = LayoutInflater.from(view.getContext());

                 View myView = inflater.inflate(R.layout.input_data_to_recycler, null);//myView contain all layout view ids
                 updateCustomDialog.setView(myView);//set custom layout to alert dialog
                 updateCustomDialog.setCancelable(false);//if user touch to other place then dialog will be close

                 final AlertDialog dialog = updateCustomDialog.create();//mycustomDialog varialble cannot be use in inner class so creating another final varialbe  to use in inner class

                 TextView deposit_btn_tv = myView.findViewById(R.id.to_deposit_tv);
                 deposit_btn_tv.setVisibility(View.GONE);//initially no deposit button because we are updating only wages p1,p2...etc
                 TextView hardcodedP1 = myView.findViewById(R.id.hardcoded_p1_tv);
                 TextView hardcodedP2 = myView.findViewById(R.id.hardcoded_p2_tv);
                 TextView hardcodedP3 = myView.findViewById(R.id.hardcoded_p3_tv);
                 TextView hardcodedP4 = myView.findViewById(R.id.hardcoded_p4_tv);

                 TextView micIcon = myView.findViewById(R.id.mic_tv);

                 TextView dateIcon = myView.findViewById(R.id.date_icon_tv);
                 TextView inputDate = myView.findViewById(R.id.input_date_tv);
                 TextView inputTime = myView.findViewById(R.id.input_time_tv);
                 TextView saveAudio = myView.findViewById(R.id.save_audio_tv);

                 Chronometer playAudioChronometer = myView.findViewById(R.id.chronometer);

                 EditText inputP1 = myView.findViewById(R.id.input_p1_et);
                 TextView runtimeSuggestionAmountToGive = myView.findViewById(R.id.work_amount_tv);
                 EditText inputP2 = myView.findViewById(R.id.input_p2_et);
                 EditText inputP3 = myView.findViewById(R.id.input_p3_et);
                 EditText inputP4 = myView.findViewById(R.id.input_p4_et);
                 EditText toGive_Amount = myView.findViewById(R.id.wages_et);
                 EditText description = myView.findViewById(R.id.enter_description_et);
                 TextView message_tv = myView.findViewById(R.id.only_used_while_updating);
                 Button save = myView.findViewById(R.id.save_btn);
                 Button cancel = myView.findViewById(R.id.cancel_btn);
                 cancel.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         dialog.dismiss();
                     }
                 });

                 if (data.getIsdeposited().equals("0")) {// 0 means not deposited it is important because it will open window to enter deposit or wages.wrong window should not be opened.
                 message_tv.setText("You are updating");
                 save.setText("LONG PRESS TO UPDATE");

                 db = new PersonRecordDatabase(holder.wages.getContext());//we can take any field context
                 Cursor cursorData = db.getData("SELECT ID,DATE,TIME,DESCRIPTION,WAGES,P1,P2,P3,P4,MICPATH FROM " + db.TABLE_NAME2 + " WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");
                 cursorData.moveToFirst();//this cursor is not closed

//********************************CUSTOMIZATION*******************************************************************************************
                 //initially every field will be invisible based on indicator others fields will be visible
                 hardcodedP2.setVisibility(View.GONE);
                 inputP2.setVisibility(View.GONE);
                 hardcodedP3.setVisibility(View.GONE);
                 inputP3.setVisibility(View.GONE);
                 hardcodedP4.setVisibility(View.GONE);
                 inputP4.setVisibility(View.GONE);

                 //CUSTOMIZATION: initially in person skill or type is M,L or G then according to that layout will be customised
                 //hardcodedP1,inputP1 by default visible so no need to mention if(indicator == 1) {
                 Cursor cursordefault = db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + data.getId() + "'");//for sure it will return type or skill
                 cursordefault.moveToFirst();//no need to check  cursordefault !=null because for sure TYPE data is present
                 hardcodedP1.setText(cursordefault.getString(0));
                 previousDataHold[0] = cursordefault.getString(0) + ": " + cursorData.getString(5);//to write previous record in description
                 toGive_Amount.setText("" + cursorData.getInt(4));//setting wages
                 inputP1.setText("" + cursorData.getInt(5));//setting same data to p1
                 cursordefault.close();

                 Cursor skillsCursor = db.getData("SELECT SKILL1,SKILL2,SKILL3 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + data.getId() + "'");
                 if (skillsCursor != null) {
                     skillsCursor.moveToFirst();
                     if (indicator == 2) {//two person
                         // hardcodedP1,inputP1 by default visible so no need to mention
                         hardcodedP2.setVisibility(View.VISIBLE);
                         inputP2.setVisibility(View.VISIBLE);
                         hardcodedP2.setText(skillsCursor.getString(0));
                         previousDataHold[1] = skillsCursor.getString(0) + ": " + cursorData.getString(6);//to write previous record in description
                         inputP2.setText("" + cursorData.getInt(6));//setting same data to p2
                     } else if (indicator == 3) {//three person
                         //hardcodedP1,inputP1 by default visible so no need to mention
                         hardcodedP2.setVisibility(View.VISIBLE);
                         inputP2.setVisibility(View.VISIBLE);
                         hardcodedP2.setText(skillsCursor.getString(0));
                         previousDataHold[1] = skillsCursor.getString(0) + ": " + cursorData.getString(6);//to write previous record in description
                         inputP2.setText("" + cursorData.getInt(6));//setting same data to p2

                         hardcodedP3.setVisibility(View.VISIBLE);
                         hardcodedP3.setText(skillsCursor.getString(1));
                         previousDataHold[2] = skillsCursor.getString(1) + ": " + cursorData.getString(7);//to write previous record in description
                         inputP3.setText("" + cursorData.getInt(7));//setting same data to p3
                         inputP3.setVisibility(View.VISIBLE);
                     } else if (indicator == 4) {////two person
                         //hardcodedP1,inputP1 by default visible so no need to mention
                         hardcodedP2.setVisibility(View.VISIBLE);
                         inputP2.setVisibility(View.VISIBLE);
                         hardcodedP2.setText(skillsCursor.getString(0));
                         previousDataHold[1] = skillsCursor.getString(0) + ": " + cursorData.getString(6);//to write previous record in description
                         inputP2.setText("" + cursorData.getInt(6));//setting same data to p2

                         hardcodedP3.setVisibility(View.VISIBLE);
                         hardcodedP3.setText(skillsCursor.getString(1));
                         previousDataHold[2] = skillsCursor.getString(1) + ": " + cursorData.getString(7);//to write previous record in description
                         inputP3.setVisibility(View.VISIBLE);
                         inputP3.setText("" + cursorData.getInt(7));//setting same data to p3

                         hardcodedP4.setVisibility(View.VISIBLE);
                         hardcodedP4.setText(skillsCursor.getString(2));
                         previousDataHold[3] = skillsCursor.getString(2) + ": " + cursorData.getString(8);//to write previous record in description
                         inputP4.setText("" + cursorData.getInt(8));//setting same data to p3
                         inputP4.setVisibility(View.VISIBLE);
                     }
                 } else
                     Toast.makeText(context, "NO DATA IN CURSOR", Toast.LENGTH_LONG).show();
                 skillsCursor.close();
//********************************Done CUSTOMIZATION*******************************************************************************************

                 previousDataHold[4] = "WAGES: " + cursorData.getString(4);//wages to write previous record in description
                 previousDataHold[5] = "DATE: " + cursorData.getString(1);//date to write previous record in description
                 previousDataHold[6] = "TIME: " + cursorData.getString(2);//time to write previous record in description
                 previousDataHold[7] = "REMARKS: " + cursorData.getString(3);//descriprion or remarks

                 int cDayOfMonth = extractData(0, cursorData.getString(1));
                 int cMonth = extractData(1, cursorData.getString(1));
                 int cYear = extractData(2, cursorData.getString(1));

                 inputDate.setText(cDayOfMonth + "-" + (cMonth + 1) + "-" + cYear);
                 dateIcon.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         //To show calendar dialog
                         DatePickerDialog datePickerDialog = new DatePickerDialog(myView.getContext(), new DatePickerDialog.OnDateSetListener() {
                             @Override
                             public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                 inputDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);//month start from 0 so 1 is added to get right month like 12
                             }
                         }, cYear, cMonth, cDayOfMonth);//This variable should be ordered this variable will set date day month to calendar to datePickerDialog so passing it
                         datePickerDialog.show();
                     }
                 });


                 //description.setText(cursorData.getString(3));//dont set previous data because if the data is more then update button or cancel button will hide due to large data
                 dialog.show();
                 //*************************************SAVING*****************************************************************************************************
                 save.setOnLongClickListener(new View.OnLongClickListener() {
                     @Override
                     public boolean onLongClick(View view) {
                         //*********************************common to all indicator 1,2,3,4*******************
                         int p1, p2, p3, p4;//this default value is taken when user do enter date to fileds
                         p1 = p2 = p3 = p4 = 0;
                         int wages = 0;

                         String micPath=data.getMicPath();//default value if we dont fetch previous data then null will be inserted and previous voice will be deleted when we try to update only wages so it is important

                          //To get exact time so write code in save button
                         Date d = Calendar.getInstance().getTime();
                         SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");//a stands for is AM or PM
                         String onlyTime = sdf.format(d);
                         inputTime.setText(onlyTime);//setting time to take time and store in db
                         String time = inputTime.getText().toString();//time will be inserted automatically

                         //if user dont enter remarks or description then it is sure that previous data will be entered so no need to check null pointer exception
                         String remarks = "[" + time + "]-[EDITED]\n\n*****PREVIOUS DATA WAS*****\n" + previousDataHold[5] + "  " + previousDataHold[6] + "\n" + previousDataHold[0] + " " + previousDataHold[1] + " " + previousDataHold[2] + " " + previousDataHold[3] + "\n" + previousDataHold[4] + "\n" + previousDataHold[7] + "\n\n" +
                                 "*****NEW EDITED DATA REMARKS*****" + "\n" + description.getText().toString().trim();//time is set automatically to remarks if user enter any remarks;
                         arr[5] = 1;//this is important because when user do not enter any data while updating then atleast 1 field should be filled with data so this field will sure be filled automatically so this is important.

                         String date = inputDate.getText().toString();//date will be inserted automatically

                         if(file !=null){//if file is not null then only it execute otherwise nothing will be inserted
                             micPath=file.getAbsolutePath();
                             file=null;//after path is saved then file=null so that next time while entering data it should not take default value ie micPath=null
                         }


                         boolean success, isWrongData, isDataPresent;
                         isWrongData = isEnterDataIsWrong(arr);
                         isDataPresent = isDataPresent(arr);
                         if (isDataPresent == true && isWrongData == false) {//means if data is present then check is it right data or not .if condition is false then default value will be taken
                             if (toGive_Amount.getText().toString().length() >= 1) {//to prevent nullpointer exception
                                 wages = Integer.parseInt(toGive_Amount.getText().toString().trim());
                             }
                             //>= if user enter only one digit then >= is important otherwise default value will be set
                             if (inputP1.getText().toString().length() >= 1) {//to prevent nullpointer exception
                                 p1 = Integer.parseInt(inputP1.getText().toString().trim());//converted to float and stored
                             }
                         }
                         //*********************************  all the upper code are common to all indicator 1,2,3,4*******************

                         if (indicator == 1) {
                             if (isDataPresent == true && isWrongData == false) {//it is important means if data is present then check is it right data or not.if condition is false then this message will be displayed "Correct the Data or Cancel and Enter again"
                                 //UPDATE to database
                                  if(micPath != null){//if it is not null then update micpath
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',MICPATH='"+micPath+ "',WAGES='" + wages + "',P1='" + p1 + "'" + " WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");
                                 }else //if micPath == null then we are not updating because null in text will be set to micpath and give wroing result like it will indicate that audio is present but actually audio is not present
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',WAGES='" + wages + "',P1='" + p1 + "'" + " WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");


                                 if (success == true) {
                                     displResultAndRefresh(wages + "          " + p1, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks+"\n"+"MICPATH: "+micPath);
                                     fromIntentPersonId = data.getId();//update to send to other intent for refresh
                                     dialog.dismiss();//dialog will be dismiss after saved automatically

                                 } else
                                     Toast.makeText(context, "FAILED TO UPDATE", Toast.LENGTH_LONG).show();
                             } else//once user enter wrong data and left blank then user wound be able to save because array value would not be change it will be 2 so  user have to "Cancel and enter again" if use dont leave blank then it will save successfully
                                 Toast.makeText(context, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();

                         } else if (indicator == 2) {
                             //p1 is automatically added
                             if (isDataPresent == true && isWrongData == false) {
                                 if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                                     p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                                 }
                                 //UPDATE to database
                                 if(micPath != null){//if it is not null then update micpath
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks + "',MICPATH='"+micPath+"',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'  WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");
                                 }else//if micPath == null then we are not updating because null in text will be set to micpath and give wroing result like it will indicate that audio is present but actually audio is not present
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'  WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");

                                 if (success == true) {
                                     displResultAndRefresh(wages + "          " + p1 + "     " + p2, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks+"\n"+"MICPATH: "+micPath);
                                     fromIntentPersonId = data.getId();//update to send to other intent for refresh
                                     dialog.dismiss();//dialog will be dismiss after saved automatically
                                 } else
                                     Toast.makeText(context, "FAILED TO UPDATE", Toast.LENGTH_LONG).show();
                             } else
                                 Toast.makeText(context, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();

                         } else if (indicator == 3) {
                             if (isDataPresent == true && isWrongData == false) {
                                 if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                                     p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                                 }
                                 if (inputP3.getText().toString().length() >= 1) {//to prevent nullpointer exception
                                     p3 = Integer.parseInt(inputP3.getText().toString().trim());//converted to float and stored
                                 }
                                 //UPDATE to database
                                 if(micPath != null){//if it is not null then update micpath
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',MICPATH='"+micPath+ "',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'" + ",P3='" + p3 + "' WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");
                                 }else//if micPath == null then we are not updating because null in text will be set to micpath and give wroing result like it will indicate that audio is present but actually audio is not present
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'" + ",P3='" + p3 + "' WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");


                                 if (success == true) {
                                     displResultAndRefresh(wages + "          " + p1 + "     " + p2 + "     " + p3, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks+"\n"+"MICPATH: "+micPath);
                                     fromIntentPersonId = data.getId();//update to send to other intent for refresh
                                     dialog.dismiss();//dialog will be dismiss after saved automatically
                                 } else
                                     Toast.makeText(context, "FAILED TO UPDATE", Toast.LENGTH_LONG).show();
                             } else
                                 Toast.makeText(context, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();


                         } else if (indicator == 4) {
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
                                 //UPDATE to database
                                 if(micPath != null){//if it is not null then update micpath
                                     success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks +"',MICPATH='"+micPath+ "',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'" + ",P3='" + p3 + "',P4='" + p4 + "' WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");
                                 }else//if micPath == null then we are not updating because null in text will be set to micpath and give wroing result like it will indicate that audio is present but actually audio is not present
                                 success = db.updateTable("UPDATE " + db.TABLE_NAME2 + " SET DATE='" + date + "',TIME='" + time + "',DESCRIPTION='" + remarks + "',WAGES='" + wages + "',P1='" + p1 + "'" + ",P2='" + p2 + "'" + ",P3='" + p3 + "',P4='" + p4 + "' WHERE ID= '" + data.getId() + "'" + " AND DATE= '" + data.getDate() + "'" + " AND TIME='" + data.getTime() + "'");

                                 if (success == true) {
                                     if (success == true) {
                                         displResultAndRefresh(wages + "          " + p1 + "     " + p2 + "     " + p3 + "     " + p4, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks+"\n"+"MICPATH: "+micPath);
                                         fromIntentPersonId = data.getId();//update to send to other intent for refresh
                                         dialog.dismiss();//dialog will be dismiss after saved automatically
                                     } else
                                         Toast.makeText(context, "FAILED TO UPDATE", Toast.LENGTH_LONG).show();
                                 } else
                                     Toast.makeText(context, "Correct the Data or Cancel and Enter again", Toast.LENGTH_LONG).show();
                             }
                         }

                         return false;
                     }
                 });
                 //*************************************DONE SAVING*****************************************************************************************************
                 toGive_Amount.addTextChangedListener(new TextWatcher() {
                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         String amount = toGive_Amount.getText().toString().trim();
                         toGive_Amount.setTextColor(Color.BLACK);
                         save.setEnabled(true);
                         arr[4] = 1;//means data is inserted
                         if (!amount.matches("[0-9]+")) {//no space or . or ,
                             Toast.makeText(context, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                             toGive_Amount.setTextColor(Color.RED);
                             save.setEnabled(false);
                             arr[4] = 2;//means wrong data
                         }
                     }

                     @Override
                     public void afterTextChanged(Editable editable) {
                     }
                 });
                 inputP1.addTextChangedListener(new TextWatcher() {
                     Cursor result = db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + data.getId() + "'");

                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         String p11 = inputP1.getText().toString().trim();
                         inputP1.setTextColor(Color.BLACK);
                         save.setEnabled(true);
                         arr[0] = 1;//means data is inserted
                         if (!p11.matches("[0-9]+")) {//"[.]?[0-9]+[.]?[0-9]*" for float
                             inputP1.setTextColor(Color.RED);
                             save.setEnabled(false);
                             arr[0] = 2;//means wrong data
                             Toast.makeText(context, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void afterTextChanged(Editable editable) {//after text changed for suggestion calculate based on prevoius rate
                         result.moveToFirst();
                         p1_p2_p3_p4_Change_Tracker(result, inputP1, inputP2, inputP3, inputP4, runtimeSuggestionAmountToGive);
                     }
                 });
                 inputP2.addTextChangedListener(new TextWatcher() {
                     Cursor result = db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + data.getId() + "'");

                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         String p11 = inputP2.getText().toString().trim();
                         inputP2.setTextColor(Color.BLACK);
                         save.setEnabled(true);
                         arr[1] = 1;//means data is inserted
                         if (!p11.matches("[0-9]+")) {// "[.]?[0-9]+[.]?[0-9]*"
                             inputP2.setTextColor(Color.RED);
                             save.setEnabled(false);
                             arr[1] = 2;//means wrong data
                             Toast.makeText(context, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void afterTextChanged(Editable editable) {
                         result.moveToFirst();
                         p1_p2_p3_p4_Change_Tracker(result, inputP1, inputP2, inputP3, inputP4, runtimeSuggestionAmountToGive);
                     }
                 });
                 inputP3.addTextChangedListener(new TextWatcher() {
                     Cursor result = db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + data.getId() + "'");

                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         String p11 = inputP3.getText().toString().trim();
                         inputP3.setTextColor(Color.BLACK);
                         save.setEnabled(true);
                         arr[2] = 1;//means data is inserted
                         if (!p11.matches("[0-9]+")) {//space or , or - is restricted
                             inputP3.setTextColor(Color.RED);
                             save.setEnabled(false);
                             arr[2] = 2;//means wrong data
                             Toast.makeText(context, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void afterTextChanged(Editable editable) {
                         result.moveToFirst();
                         p1_p2_p3_p4_Change_Tracker(result, inputP1, inputP2, inputP3, inputP4, runtimeSuggestionAmountToGive);
                     }
                 });
                 inputP4.addTextChangedListener(new TextWatcher() {
                     Cursor result = db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + data.getId() + "'");

                     @Override
                     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     }

                     @Override
                     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         String p11 = inputP4.getText().toString().trim();
                         inputP4.setTextColor(Color.BLACK);
                         save.setEnabled(true);
                         arr[3] = 1;//means data is inserted
                         if (!p11.matches("[0-9]+")) {//space or , or - is restricted
                             inputP4.setTextColor(Color.RED);
                             save.setEnabled(false);
                             arr[3] = 2;//means wrong data
                             Toast.makeText(context, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                         }
                     }

                     @Override
                     public void afterTextChanged(Editable editable) {
                         result.moveToFirst();
                         p1_p2_p3_p4_Change_Tracker(result, inputP1, inputP2, inputP3, inputP4, runtimeSuggestionAmountToGive);
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

                                     Toast.makeText(context, "RECORDING STARTED", Toast.LENGTH_SHORT).show();

                                     //be carefull take only getExternalFilesDir( null ) https://stackoverflow.com/questions/59017202/mediarecorder-stop-failed
                                     File folder = new File(context.getExternalFilesDir(null) + "/acBookMicRecording");//Creating File directory in phone

                                     if (!folder.exists()) {//if folder not exist
                                         Toast.makeText(context, "Creating acBookMicRecording folder to store audios", Toast.LENGTH_LONG).show();
                                         folder.mkdir();//create folder
                                     }

                                     startRecordingVoice();
                                     ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //while the user is recording screen should be on. it should not close

                                 } else {//if recording is not started then stop
                                     Toast.makeText(context, "AGAIN TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
                                 }
                                 mStartRecording = !mStartRecording;//so that user should click 2 times to start recording

                             }else//request for permission
                                 ActivityCompat.requestPermissions((Activity)context,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},21);
                         }
                     });

                     if(data.getMicPath() != null) {//if there is audio then set to color  green
                         micIcon.setVisibility(View.GONE);//user wound be able to save voice for second time if there is already voice because we want to keep previous voice save we dont want to delete previous voice
                         saveAudio.setVisibility(View.GONE);

                         playAudioChronometer.setTextColor(context.getColor(R.color.green));
                         playAudioChronometer.setText("PLAY");
                         playAudioChronometer.setGravity(Gravity.RIGHT);//setting text to right
                         playAudioChronometer.setTypeface(null,Typeface.BOLD);//changing text to bold
                     }
                     playAudioChronometer.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                             if (data.getMicPath() != null || file != null) {//checking audio is present or not
                                 MediaPlayer mediaPlayer = new MediaPlayer();
                                 try {

                                     if(file != null){//if new audio is set then file will contain audio and data.getMicPath() will contain null
                                          mediaPlayer.setDataSource(file.getAbsolutePath());
                                     }else
                                          mediaPlayer.setDataSource(data.getMicPath());//passing the path where this audio is saved

                                     mediaPlayer.prepare();
                                     mediaPlayer.start();
                                     Toast.makeText(view.getContext(), "AUDIO PLAYING", Toast.LENGTH_LONG).show();
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                             }else
                                 Toast.makeText(context, "TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
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

                                 playAudioChronometer.setTextColor(context.getResources().getColor(R.color.green));//changind text color to green to give feel that is saved
                                 micIcon.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);//set background image to cancel
                                 stopAndSaveRecordingPathToDB();
                                 playAudioChronometer.stop();//stopping chronometer
                                 micIcon.setEnabled(false);//so that user cannot press again this button
                                 saveAudio.setEnabled(false);//even this button user should not click again
                                 playAudioChronometer.setEnabled(true);//when audio is save then user will be able to play
                             }else
                                 Toast.makeText(context, "TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
                         }
                     });

             }else if(data.getIsdeposited().equals("1")){//this will execute only user want to update only deposit
                     description.setVisibility(View.GONE);
                     save.setVisibility(View.GONE);
                     LinearLayout l1=myView.findViewById(R.id.hardcode_layout);
                     LinearLayout l2=myView.findViewById(R.id.input_layout);
                     LinearLayout l3=myView.findViewById(R.id.suggestion_layout);
                     LinearLayout l4=myView.findViewById(R.id.this_week_work_amount_layout);
                     LinearLayout l5=myView.findViewById(R.id.date_mic_chronometer_layout);
                     l1.setVisibility(View.GONE);
                     l2.setVisibility(View.GONE);
                     l3.setVisibility(View.GONE);
                     l4.setVisibility(View.GONE);
                     l5.setVisibility(View.GONE);

                      deposit_btn_tv.setVisibility(View.VISIBLE);
                      deposit_btn_tv.setText("CLICK TO UPDATE DEPOSIT");
                      deposit_btn_tv.setOnClickListener(new View.OnClickListener() {//not opening directly because user sometime click on wages instead of deposit so to let user think he has click on right filed then only he can update
                         @Override
                         public void onClick(View view) {//sending date,time and id to update deposit
                             Intent intent=new Intent(context,CustomizeLayoutOrDepositAmount.class);
                             intent.putExtra("ID",data.getId());
                             intent.putExtra("DATE",data.getDate());
                             intent.putExtra("TIME",data.getTime());
                             dialog.dismiss();//while going to other activity dismiss dialog otherwise window leak
                             ((Activity)context).finish();//while going to other activity so destroy  this current activity so that while coming back we will see refresh activity
                            context.startActivity(intent);
                         }
                     });
                     dialog.show();
                 }
                 return false;
             }
         });
    }
    private void p1_p2_p3_p4_Change_Tracker(Cursor result, EditText inputP1, EditText inputP2, EditText inputP3, EditText inputP4, TextView runtimeSuggestionAmountToGive) {
        String p1,p2,p3,p4;
        p1 = inputP1.getText().toString().trim();
        //all 15 combination
        //only p1
        if (arr[0] == 1 && arr[1] != 1 && arr[2] != 1 && arr[3] != 1) {
            runtimeSuggestionAmountToGive.setText("" + (result.getInt(0) * Integer.parseInt(p1)));
        }
        //only p1 p2
        else if (arr[0] == 1 && arr[1] == 1 && arr[2] != 1 && arr[3] != 1) {
            p2 = inputP2.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(1) * Integer.parseInt(p2))));
        }
        //only p1 p2,p3
        else if (arr[0] == 1 && arr[1] == 1 && arr[2] == 1 && arr[3] != 1) {
            p2 = inputP2.getText().toString().trim();
            p3 = inputP3.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(2) * Integer.parseInt(p3))));
        }
        //only p1 p2,p3,p4
        else if (arr[0] == 1 && arr[1] == 1 && arr[2] == 1 && arr[3] == 1) {
            p2 = inputP2.getText().toString().trim();
            p3 = inputP3.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(2) * Integer.parseInt(p3)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //only p1 p3 p4
        else if (arr[0] == 1 && arr[1] != 1 && arr[2] == 1 && arr[3] == 1) {
            p3 = inputP3.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(2) * Integer.parseInt(p3)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //only p1 p2 p4
        else if (arr[0] == 1 && arr[1] == 1 && arr[2] != 1 && arr[3] == 1) {
            p2 = inputP2.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //only p2 p3 p4
        else if (arr[0] != 1 && arr[1] == 1 && arr[2] == 1 && arr[3] == 1) {
            p2 = inputP2.getText().toString().trim();
            p3 = inputP3.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(2) * Integer.parseInt(p3)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //only p1 P4
        else if (arr[0] == 1 && arr[1] != 1 && arr[2] != 1 && arr[3] == 1) {
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //only p1 P3
        else if (arr[0] == 1 && arr[1] != 1 && arr[2] == 1 && arr[3] != 1) {
            p3 = inputP3.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(0) * Integer.parseInt(p1)) + (result.getInt(2) * Integer.parseInt(p3))));
        }
        //Only p3,p4
        else if (arr[0] != 1 && arr[1] != 1 && arr[2] == 1 && arr[3] == 1) {
            p3 = inputP3.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(2) * Integer.parseInt(p3)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //Only p2,p4
        else if (arr[0] != 1 && arr[1] == 1 && arr[2] != 1 && arr[3] == 1) {
            p2 = inputP2.getText().toString().trim();
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(3) * Integer.parseInt(p4))));
        }
        //Only p2,p3
        else if (arr[0] != 1 && arr[1] == 1 && arr[2] == 1 && arr[3] != 1) {
            p2 = inputP2.getText().toString().trim();
            p3 = inputP3.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(1) * Integer.parseInt(p2)) + (result.getInt(2) * Integer.parseInt(p3))));
        }
        //only p2
        else if (arr[0] != 1 && arr[1] == 1 && arr[2] != 1 && arr[3] != 1) {
            p2 = inputP2.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(1) * Integer.parseInt(p2)) ));
        }
        //only p3
        else if (arr[0] != 1 && arr[1] != 1 && arr[2] == 1 && arr[3] != 1) {
            p3 = inputP3.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(2) * Integer.parseInt(p3)) ));
        }
        //only p4
        else if (arr[0] != 1 && arr[1] != 1 && arr[2] != 1 && arr[3] == 1) {
            p4 = inputP4.getText().toString().trim();
            runtimeSuggestionAmountToGive.setText("" + ((result.getInt(3) * Integer.parseInt(p4)) ));
        }
        //if any wrong data then this will execute
        if(arr[0] == 2 || arr[1] == 2 || arr[2] == 2 || arr[3] == 2) {
            runtimeSuggestionAmountToGive.setText("0");
            // Toast.makeText(this, "ENTER 0 DON'T LEFT M L G EMPTY", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isDataPresent(int[] arr){
        boolean bool=true;
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
    private boolean isEnterDataIsWrong(int[] arr) {
        boolean bool=true;
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

    private int extractData(int i,String date) {
        int data=0;
        String[] str=date.split("-");//converting using String.split() method with "-" as a delimiter
        if(i==0){//0 is for day of month
         return Integer.parseInt(str[0]);
        }else if(i==1){//1 is for month
            return (Integer.parseInt(str[1])-1);//month-1 to get right result
        }else if(i==2){//2 is for year
            return Integer.parseInt(str[2]);
        }
        return data;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,wages,p1,p2,p3,p4;
        Spinner spinnerdescAudioIcon;
        LinearLayout singleRecordLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date_in_recycler_tv);
            wages=itemView.findViewById(R.id.wages_in_recycler_tv);
            p1=itemView.findViewById(R.id.p1_in_recycler_tv);
            p2=itemView.findViewById(R.id.p2_in_recycler_tv);
            p3=itemView.findViewById(R.id.p3_in_recycler_tv);
            p4=itemView.findViewById(R.id.p4_in_recycler_tv);
            spinnerdescAudioIcon =itemView.findViewById(R.id.spinner_in_recycler_tv);
            singleRecordLayout=itemView.findViewById(R.id.single_record_layout);

        }
    }
    public void displResult(String title,String message) {
        AlertDialog.Builder showDataFromDataBase = new AlertDialog.Builder(context);
        showDataFromDataBase.setCancelable(false);
        showDataFromDataBase.setTitle(title);
        showDataFromDataBase.setMessage(message);
        showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //while here refreshing getting error cursoroutofboundexception if we put code of refresh then when user want to see remarks then also it will refresh so no needed
            }
        });
        showDataFromDataBase.create().show();
    }
    public void displResultAndRefresh(String title,String message) {
        AlertDialog.Builder showDataFromDataBase = new AlertDialog.Builder(context);
        showDataFromDataBase.setCancelable(false);
        showDataFromDataBase.setTitle(title);
        showDataFromDataBase.setMessage(message);
        showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent1=new Intent(context,IndividualPersonDetailActivity.class);
                intent1.putExtra("ID",fromIntentPersonId);
                ((Activity)context).finish();//destroying this current activity typecast to activity
                context.startActivity(intent1);
            }
        });
        showDataFromDataBase.create().show();
    }
    private int get_indicator(String PersonId) {
        db=new PersonRecordDatabase(context);
        Cursor cursor=db.getData("SELECT INDICATOR FROM " + db.TABLE_NAME3 + " WHERE ID= '" + PersonId +"'");//for sure it will return type or skill
        if(cursor != null){
            cursor.moveToFirst();
            if(cursor.getString(0) == null) {
                return 1;
            } else
                return Integer.parseInt(cursor.getString(0));
        }else
            Toast.makeText(context.getApplicationContext(), "No indicator: ", Toast.LENGTH_SHORT).show();

        return 1;//by default 1
    }
    private boolean checkPermission() {//checking for permission of mic and external storage
        if( (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
            return true;
        }else {
            return false;
        }
    }
    private void startRecordingVoice() {
        Long  tsLong=System.currentTimeMillis()/1000;//folder name should be unique so taking time as name of mic record so every record name will be different
        String ts=tsLong.toString();
        fileName="audio_"+ts;//file name
        file=new File(context.getExternalFilesDir( null )+"/acBookMicRecording/"+fileName+".mp3");//path of audio where it is saved in device

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
        Toast.makeText(context, "RECORDING", Toast.LENGTH_SHORT).show();
    }
    private  void stopAndSaveRecordingPathToDB(){
        mediaRecorder.stop();
        mElapsedMillis=(System.currentTimeMillis()-mstartingTimeMillis);
        mediaRecorder.release();
        mediaRecorder=null;
        // Toast.makeText(this, "Recording SAVED "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
    public String getPreviousDate(){
        //to get previous date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);//-1 to get previous date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");//dd-M-yyyy to get date as 19-3-2022 by lefting 0. 19-03-2022
         return dateFormat.format(calendar.getTimeInMillis());
    }
}
