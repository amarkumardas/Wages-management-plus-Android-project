package amar.das.acbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.adapters.WagesDetailsAdapter;
import amar.das.acbook.databinding.ActivityIndividualPersonDetailBinding;
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
    boolean mStartRecording =false;

    PersonRecordDatabase db;
    private String fromIntentPersonId;
    int arr[]=new int[7];
    String active ="0";

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

            //***********setting skill top of layout**********************************************
            Cursor defaultSkillCursor=db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
               defaultSkillCursor.moveToFirst();
               binding.defaultHardcodedTv.setText(defaultSkillCursor.getString(0));
               binding.defaultSkillTextTv.setText(defaultSkillCursor.getString(0) + "  =");//default calculation skill
               defaultSkillCursor.close();

             Cursor sumCursor=db.getData("SELECT SUM(WAGES),SUM(P1),SUM(P2),SUM(P3),SUM(P4),SUM(DEPOSIT) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
             sumCursor.moveToFirst();

             if( sumCursor.getInt(0) < 0 )//if total wages amount cross the  range of int the this message will be shown
                 Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WAGES", Toast.LENGTH_LONG).show();

             binding.blueTotalWagesTv.setText(sumCursor.getString(0));
             binding.blueTotalp1Tv.setText(sumCursor.getString(1));
             binding.totalP1CountTv.setText(sumCursor.getString(1));
                    //sum deposit
             if(sumCursor.getString(5) != null) {//if there is deposit then set visibility visible or else layout visibility GONE
                 binding.totalDepositAmountTv.setText("= " + sumCursor.getString(5));
             }else
                 binding.totalDepositAmountLayout.setVisibility(View.GONE);

            Cursor skillNRateCursor=db.getData("SELECT SKILL1,SKILL2,SKILL3,R1,R2,R3,R4 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            if(skillNRateCursor != null) {
                skillNRateCursor.moveToFirst();
                int indicate = get_indicator(fromIntentPersonId);
                                //R1
                if(skillNRateCursor.getInt(3) != 0) {
                                                    //R1
                    binding.p1RateTv.setText(skillNRateCursor.getString(3));//default skill
                                                                       //    R1 * p1
                    binding.totalP1AmountTv.setText("= "+skillNRateCursor.getInt(3)*sumCursor.getInt(1));//default skill
                }else {
                    binding.totalP1AmountTv.setText("= NEW PERSON PROVIDE RATE");//default skill
                    Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                }
                               //total wages
                if(sumCursor.getString(0) !=null) {//if total wages is not null then set total wages
                    binding.wagesTotalAmountTv.setText(sumCursor.getString(0));//total wages set
                }
                   //by default= deposit,totalP2CountTv,defaultHardcodedTv,defaultSkillTextTv,p1RateTv,totalP1AmountTv is set automatically
                     if(indicate==1) {
                         indicator1234CalculateAndUpdate(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),0,0,0);
                     }

                binding.p2Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                binding.p3Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                binding.p4Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize

                if(indicate == 2) {
                    if(skillNRateCursor.getInt(4) != 0) {
                        //R1
                        binding.p2RateTv.setText(skillNRateCursor.getString(4));
                        //    R2 * p2
                        binding.totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                    }else {
                        binding.totalP2AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }

                    binding.totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                    binding.skill1TextTv.setText(skillNRateCursor.getString(0)+"  =");//setting skill 1
                    binding.hardcoded1Tv.setText(skillNRateCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.p2Layout.setVisibility(View.VISIBLE);

                    indicator1234CalculateAndUpdate(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),0,0);

                } else if (indicate == 3) {
                    if(skillNRateCursor.getInt(4) != 0) {
                        binding.p2RateTv.setText(skillNRateCursor.getString(4));
                                                                                //    R2 * p2
                        binding.totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                    }else {
                        binding.totalP2AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }

                    if(skillNRateCursor.getInt(5) != 0) {
                        binding.p3RateTv.setText(skillNRateCursor.getString(5));
                                                                                 //    R3 * p3
                        binding.totalP3AmountTv.setText("= "+skillNRateCursor.getInt(5)*sumCursor.getInt(3));
                    }else {
                        binding.totalP3AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }
                    binding.totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                    binding.totalP3CountTv.setText(sumCursor.getString(3));//total p3 count
                    binding.skill1TextTv.setText(skillNRateCursor.getString(0)+"  =");//setting skill 1
                    binding.skill2TextTv.setText(skillNRateCursor.getString(1)+"  =");//setting skill 2
                    binding.hardcoded1Tv.setText(skillNRateCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.hardcoded2Tv.setText(skillNRateCursor.getString(1));
                    binding.blueTotalp3Tv.setText(sumCursor.getString(3));
                    binding.p2Layout.setVisibility(View.VISIBLE);
                    binding.p3Layout.setVisibility(View.VISIBLE);

                    indicator1234CalculateAndUpdate(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),skillNRateCursor.getInt(5) * sumCursor.getInt(3),0);

                } else if (indicate == 4) {
                    if(skillNRateCursor.getInt(4) != 0) {
                        binding.p2RateTv.setText(skillNRateCursor.getString(4));
                        //    R2 * p2
                        binding.totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                    }else {
                        binding.totalP2AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }

                    if(skillNRateCursor.getInt(5) != 0) {
                        binding.p3RateTv.setText(skillNRateCursor.getString(5));
                        //    R3 * p3
                        binding.totalP3AmountTv.setText("= "+skillNRateCursor.getInt(5)*sumCursor.getInt(3));
                    }else {
                        binding.totalP3AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }

                    if(skillNRateCursor.getInt(6) != 0) {
                        binding.p4RateTv.setText(skillNRateCursor.getString(6));
                        //    R4 * p4
                        binding.totalP4AmountTv.setText("= "+skillNRateCursor.getInt(6)*sumCursor.getInt(4));
                    }else {
                        binding.totalP4AmountTv.setText("= NEW PERSON PROVIDE RATE");
                        Toast.makeText(this, "Long press FINAL TOTAL button to  provide rate", Toast.LENGTH_LONG).show();
                    }

                    binding.totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                    binding.totalP3CountTv.setText(sumCursor.getString(3));//total p3 count
                    binding.totalP4CountTv.setText(sumCursor.getString(4));//total p4 count
                    binding.skill1TextTv.setText(skillNRateCursor.getString(0)+"  =");//setting skill 1
                    binding.skill2TextTv.setText(skillNRateCursor.getString(1)+"  =");//setting skill 2
                    binding.skill3TextTv.setText(skillNRateCursor.getString(2)+"  =");//setting skill 3
                    binding.hardcoded1Tv.setText(skillNRateCursor.getString(0));
                    binding.blueTotalp2Tv.setText(sumCursor.getString(2));
                    binding.hardcoded2Tv.setText(skillNRateCursor.getString(1));
                    binding.blueTotalp3Tv.setText(sumCursor.getString(3));
                    binding.hardcoded3Tv.setText(skillNRateCursor.getString(2));
                    binding.blueTotalp4Tv.setText(sumCursor.getString(4));
                    binding.p2Layout.setVisibility(View.VISIBLE);
                    binding.p3Layout.setVisibility(View.VISIBLE);
                    binding.p4Layout.setVisibility(View.VISIBLE);
                    indicator1234CalculateAndUpdate(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),skillNRateCursor.getInt(5) * sumCursor.getInt(3),skillNRateCursor.getInt(6) * sumCursor.getInt(4));
                }
            }
            skillNRateCursor.close();
            sumCursor.close();
            //***********Done setting skill***********************************************
            //*******************Recycler view********************************************
              Cursor allDataCursor=db.getData("SELECT DATE,MICPATH,DESCRIPTION,WAGES,DEPOSIT,P1,P2,P3,P4,ID,TIME,ISDEPOSITED FROM "+db.TABLE_NAME2+" WHERE ID='"+fromIntentPersonId+"'");
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
                model.setTime(allDataCursor.getString(10));
                model.setIsdeposited((allDataCursor.getString(11)));
                dataList.add(model);
            }
            allDataCursor.close();
            WagesDetailsAdapter wagesDetailsAdapter=new WagesDetailsAdapter(this,dataList);

            binding.singleRecordRecy.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            binding.singleRecordRecy.setAdapter(wagesDetailsAdapter);
            binding.singleRecordRecy.scrollToPosition(dataList.size()-1);//this will scroll recycler view to last position automatically
            //*******************done Recycler view********************************************
            //retrieving data from db
            Cursor cursor = db.getData("SELECT NAME,BANKACCOUNT,IFSCCODE,BANKNAME,AADHARCARD,PHONE,FATHERNAME,IMAGE,ACHOLDER,ID FROM " + db.TABLE_NAME1 + " WHERE ID='" + fromIntentPersonId + "'");
            if (cursor != null) {
                cursor.moveToFirst();
                binding.nameTv.setText(cursor.getString(0));
                binding.accountTv.setText(HtmlCompat.fromHtml("A/C:  " + "<b>" + cursor.getString(1) + "</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
                binding.ifscCodeTv.setText("IFSC:  " + cursor.getString(2));
                binding.bankNameTv.setText("Bank: " + cursor.getString(3));
                binding.aadharTv.setText(HtmlCompat.fromHtml("Aadhar Card:  " + "<b>" + cursor.getString(4) + "</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
                binding.phoneTv.setText("Phone:  " + cursor.getString(5));
                binding.fatherNameTv.setText("Father: " + cursor.getString(6));

                if (cursor.getString(5).length() == 10) {//if there is no phone number then show default icon color black else green icon
                    binding.callTv.setBackgroundResource(R.drawable.ic_outline_call_24);
                }

                byte[] image = cursor.getBlob(7);//getting image from db as blob
                //getting bytearray image from DB and converting  to bitmap to set in imageview
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                binding.imageImg.setImageBitmap(bitmap);

                binding.acHolderTv.setText("A/C Holder: " + cursor.getString(8));
                binding.idTv.setText("ID: " + cursor.getString(9));
            } else {
                Toast.makeText(this, "NO DATA IN CURSOR", Toast.LENGTH_SHORT).show();
            }
          //setting star rating
            Cursor cursor2 = db.getData("SELECT RATING FROM " + db.TABLE_NAME3 + " WHERE ID='" + fromIntentPersonId + "'");
            cursor2.moveToFirst();
            if(cursor2.getString(0) != null)
               binding.starRatingTv.setText(cursor2.getString(0)+" *");
            else
                binding.starRatingTv.setText("0 *");//if user has never press save button on Meta data then by default 0* will be shown
            cursor2.close();

            //Meta data
            binding.starRatingTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder mycustomDialog=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
                    LayoutInflater inflater=LayoutInflater.from(IndividualPersonDetailActivity.this);

                    View myView=inflater.inflate(R.layout.meta_data,null);//myView contain all layout view ids
                    mycustomDialog.setView(myView);//set custom layout to alert dialog
                    mycustomDialog.setCancelable(false);//if user touch to other place then dialog will not be close

                    final AlertDialog dialog=mycustomDialog.create();//mycustomDialog varialble cannot be use in inner class so creating another final varialbe  to use in inner class

                    //ids
                    RadioButton activeRadio=myView.findViewById(R.id.active_metadata);
                    RadioGroup radioGroup=myView.findViewById(R.id.skill_radiogp_metadata);
                    Spinner starSpinner= myView.findViewById(R.id.starSpinner_metadata);
                    TextView dateIcon=myView.findViewById(R.id.date_icon_metadata);
                    TextView dateTv=myView.findViewById(R.id.dateTv_metadata);
                    EditText refferal=myView.findViewById(R.id.refferal_metadata);
                    Button save=myView.findViewById(R.id.save_btn_metadata);
                    Button  cancel=myView.findViewById(R.id.cancel_btn_metadata);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    Cursor cursor = db.getData("SELECT ACTIVE FROM " + db.TABLE_NAME1 + " WHERE ID='" + fromIntentPersonId + "'");
                    cursor.moveToFirst();
                    if(cursor.getString(0).equals("1"))
                        activeRadio.setVisibility(View.GONE);//when it is active then dont show to activate
                    else if(cursor.getString(0).equals("0"))
                        activeRadio.setChecked(false);
                    cursor.close();
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//this should not be use in other class class other wise it will not be called when user change radio button
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkedidOfRadioBtn) {
                            switch(checkedidOfRadioBtn){
                                case R.id.active_metadata:{
                                    active ="1";//updating active variable
                                      break;
                                }
                            }
                        }
                    });

                    Cursor cursor2 = db.getData("SELECT RATING,LEAVINGDATE,REFFERAL FROM " + db.TABLE_NAME3 + " WHERE ID='" + fromIntentPersonId + "'");
                    cursor2.moveToFirst();

                    String[] ratingStar =getResources().getStringArray(R.array.star);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(IndividualPersonDetailActivity.this, android.R.layout.simple_list_item_1,ratingStar);
                    starSpinner.setAdapter(adapter);
                    if(cursor2.getString(0) != null){//rating star
                        int spinnerPosition = adapter.getPosition(cursor2.getString(0));
                        starSpinner.setSelection(spinnerPosition);
                    }else if(cursor2.getString(0) == null){
                        int spinnerPosition = adapter.getPosition("1");//1 star by default
                        starSpinner.setSelection(spinnerPosition);
                    }

                    if(cursor2.getString(1) != null){//leaving date
                        dateTv.setText(cursor2.getString(1));
                    }else if(cursor2.getString(1) == null) {
                        dateTv.setText("");
                    }
                    final Calendar current=Calendar.getInstance();//to get current date and time
                    int cYear,cMonth,cDayOfMonth;
                    cYear=current.get(Calendar.YEAR);
                    cMonth=current.get(Calendar.MONTH);
                    cDayOfMonth=current.get(Calendar.DAY_OF_MONTH);

                    dateIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //To show calendar dialog
                            DatePickerDialog datePickerDialog=new DatePickerDialog(IndividualPersonDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                    dateTv.setText(dayOfMonth+"-"+(month+1)+"-"+year);//month start from 0 so 1 is added to get right month like 12
                                }
                            },cYear,cMonth,cDayOfMonth);//This variable should be ordered this variable will set date day month to calendar to datePickerDialog so passing it
                            datePickerDialog.show();
                        }
                    });

                    if(cursor2.getString(2) != null){//refferal
                        refferal.setText(cursor2.getString(2));
                    }
                    cursor2.close();
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean success2;
                            String rate;
                            if(active.equals("1")) {//if user has pressed radio button then only it will execute
                                //to automatically set today date so that it become active
                                final Calendar current=Calendar.getInstance();//to get current date and time
                                int cYear=current.get(Calendar.YEAR);
                                int cMonth=current.get(Calendar.MONTH);
                                int cDayOfMonth=current.get(Calendar.DAY_OF_MONTH);
                                String date=cDayOfMonth+"-"+(cMonth+1)+"-"+cYear;
                                success2=db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + active +"', LATESTDATE='"+date+"' WHERE ID='" + fromIntentPersonId + "'");
                                if(!success2)
                                    Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE LATEST DATE", Toast.LENGTH_LONG).show();
                            }

                             if(starSpinner.getSelectedItem().toString().equals("SELECT"))//if user bymistake click on SELECT then by default start set to 1
                                 rate="1";//default value
                             else
                                 rate=starSpinner.getSelectedItem().toString();

                             if(dateTv.getText().toString()=="") {//by default if user dont enter anything then editText view contain nothing so checking "" it is important otherwise it will  produce error to other code due to nothing.so if nothing then dont update leavingdate
                                  success2=db.update_Rating_TABLE_NAME3(rate,refferal.getText().toString().trim(),null,fromIntentPersonId);
                             } else { //if user dont enter anything then else will execute
                                  success2=db.update_Rating_TABLE_NAME3(rate,refferal.getText().toString().trim(),dateTv.getText().toString().trim(),fromIntentPersonId);
                             }
                             if(success2)
                                 displResult("SAVED SUCCESSFULLY", "RATING: " + rate + "\nLEAVINGDATE: " + dateTv.getText().toString().trim() + "\n\nREFFERED TO: " + refferal.getText().toString().trim());
                             else
                                 displResult("FAILED TO SAVE!!!","DATA NOT UPDATED- UPDATE QUERY FAILED- PLEASE TRY AGAIN");

                             dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
            binding.finalCalculationBtn.setOnLongClickListener(new View.OnLongClickListener() {
                TextView defaultSkillTextTv,totalP1CountTv,workTotalAmountTv,totalP1AmountTv,advanceOrBalanceTv,totalDepositAmountTv,wagesTotalAmountTv,skill1TextTv,totalP2CountTv,totalP2AmountTv,skill2TextTv,totalP3CountTv,totalP3AmountTv,skill3TextTv,totalP4CountTv,totalP4AmountTv;
                LinearLayout p2Layout,p3Layout,p4Layout,totalDepositAmountLayout;
                EditText p1RateTv,p2RateTv,p3RateTv,p4RateTv;
                Button saveAndCreatePdf,cancel;
                int innerArray[]=new int[4],totalDeposit=0,totalWages=0,p1=0,p2=0,p3=0,p4=0,r1=0,r2=0,r3=0,r4=0,indicate=0;//while saving this variable required

                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder mycustomDialog=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
                    LayoutInflater inflater=LayoutInflater.from(IndividualPersonDetailActivity.this);
                    View myView=inflater.inflate(R.layout.final_calculation_layout,null);//myView contain all layout view ids
                    mycustomDialog.setView(myView);//set custom layout to alert dialog
                    mycustomDialog.setCancelable(false);//if user touch to other place then dialog will not be close
                    final AlertDialog dialog=mycustomDialog.create();//mycustomDialog varialble cannot be use in inner class so creating another final varialbe  to use in inner class
                    initialiseIDs(myView);//ids
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    Cursor defaultSkillCursor=db.getData("SELECT TYPE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");//for sure it will return type or skill
                    defaultSkillCursor.moveToFirst();
                    defaultSkillTextTv.setText(defaultSkillCursor.getString(0)+" =");//default calculation skill
                    defaultSkillCursor.close();

                    Cursor sumCursor=db.getData("SELECT SUM(WAGES),SUM(P1),SUM(P2),SUM(P3),SUM(P4),SUM(DEPOSIT) FROM "+db.TABLE_NAME2+" WHERE ID= '"+fromIntentPersonId +"'");
                    sumCursor.moveToFirst();
                    //initializing this variable to take during saving
                    p1=sumCursor.getInt(1);
                    p2=sumCursor.getInt(2);
                    p3=sumCursor.getInt(3);
                    p4=sumCursor.getInt(4);
                    totalWages=sumCursor.getInt(0);

                    totalP1CountTv.setText(sumCursor.getString(1));//default skill
                    //sum deposit
                    if(sumCursor.getString(5) != null) {//if there is deposit then set visibility visible or else layout visibility GONE
                         totalDepositAmountTv.setText("= " + sumCursor.getString(5));
                         totalDeposit=sumCursor.getInt(5);//updating totalDeposit to take during save
                    }else
                       totalDepositAmountLayout.setVisibility(View.GONE);

                    Cursor skillNRateCursor=db.getData("SELECT SKILL1,SKILL2,SKILL3,R1,R2,R3,R4 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
                    if(skillNRateCursor != null) {
                        skillNRateCursor.moveToFirst();
                       //initializing this variables to take during saving
                        r1=skillNRateCursor.getInt(3);
                        r2=skillNRateCursor.getInt(4);
                        r3=skillNRateCursor.getInt(5);
                        r4=skillNRateCursor.getInt(6);

                        //if both wages and totalwork amount is less then 0 then both message have to show so if statement two times
                        if(sumCursor.getInt(0) < 0 ) {//if total wages amount cross the  range of int the this message will be shown.its important
                            Toast.makeText(IndividualPersonDetailActivity.this, "INCORRECT CALCULATION PLEACE CHECK TOTAL WAGES", Toast.LENGTH_LONG).show();
                            saveAndCreatePdf.setVisibility(View.GONE);
                        }
                        if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) < 0) {//user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
                            Toast.makeText(IndividualPersonDetailActivity.this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
                            saveAndCreatePdf.setVisibility(View.GONE);//its important otherwise save option will be unabled when user enter rate
                        }

                         indicate = get_indicator(fromIntentPersonId);
                        //R1
                        if (skillNRateCursor.getInt(3) != 0) {
                            //R1
                             p1RateTv.setText(skillNRateCursor.getString(3));//default skill
                            //    R1 * p1
                            totalP1AmountTv.setText("= " + skillNRateCursor.getInt(3) * sumCursor.getInt(1));//default skill
                        } else {
                            saveAndCreatePdf.setVisibility(View.GONE);
                            totalP1AmountTv.setText("= PROVIDE RATE");//default skill
                        }
                        //total wages
                        if (sumCursor.getString(0) != null) {//if total wages is not null then set total wages
                             wagesTotalAmountTv.setText(sumCursor.getString(0));//total wages set
                        }
                        //by default= deposit,totalP2CountTv,defaultHardcodedTv,defaultSkillTextTv,p1RateTv,totalP1AmountTv is set automatically
                        if (indicate == 1) {
                            indicator1234CalculateButDontUpdateToDBFinal(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),0,0,0);
                        }

                        p2Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                        p3Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize
                        p4Layout.setVisibility(View.GONE);//initiall invisible according to indicator it will customize

                        if(indicate == 2) {
                            if(skillNRateCursor.getInt(4) != 0) {
                                //R1
                                p2RateTv.setText(skillNRateCursor.getString(4));
                                //    R2 * p2
                                totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP2AmountTv.setText("= PROVIDE RATE");
                            }

                             totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                             skill1TextTv.setText(skillNRateCursor.getString(0)+" =");//setting skill 1
                             p2Layout.setVisibility(View.VISIBLE);
                             indicator1234CalculateButDontUpdateToDBFinal(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),0,0);
                        } else if (indicate == 3) {
                            if(skillNRateCursor.getInt(4) != 0) {
                                 p2RateTv.setText(skillNRateCursor.getString(4));
                                //    R2 * p2
                                 totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP2AmountTv.setText("= PROVIDE RATE");
                            }
                            if(skillNRateCursor.getInt(5) != 0) {
                                 p3RateTv.setText(skillNRateCursor.getString(5));
                                //    R3 * p3
                                totalP3AmountTv.setText("= "+skillNRateCursor.getInt(5)*sumCursor.getInt(3));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP3AmountTv.setText("= PROVIDE RATE");
                            }
                             totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                             totalP3CountTv.setText(sumCursor.getString(3));//total p3 count
                             skill1TextTv.setText(skillNRateCursor.getString(0)+" =");//setting skill 1
                             skill2TextTv.setText(skillNRateCursor.getString(1)+" =");//setting skill 2
                             p2Layout.setVisibility(View.VISIBLE);
                             p3Layout.setVisibility(View.VISIBLE);
                            indicator1234CalculateButDontUpdateToDBFinal(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),skillNRateCursor.getInt(5) * sumCursor.getInt(3),0);

                        } else if (indicate == 4) {
                            if(skillNRateCursor.getInt(4) != 0) {
                                 p2RateTv.setText(skillNRateCursor.getString(4));
                                //    R2 * p2
                                 totalP2AmountTv.setText("= "+skillNRateCursor.getInt(4)*sumCursor.getInt(2));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP2AmountTv.setText("= PROVIDE RATE");
                            }

                            if(skillNRateCursor.getInt(5) != 0) {
                                p3RateTv.setText(skillNRateCursor.getString(5));
                                //    R3 * p3
                                 totalP3AmountTv.setText("= "+skillNRateCursor.getInt(5)*sumCursor.getInt(3));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP3AmountTv.setText("= PROVIDE RATE");
                            }

                            if(skillNRateCursor.getInt(6) != 0) {
                                 p4RateTv.setText(skillNRateCursor.getString(6));
                                //    R4 * p4
                                 totalP4AmountTv.setText("= "+skillNRateCursor.getInt(6)*sumCursor.getInt(4));
                            }else {
                                saveAndCreatePdf.setVisibility(View.GONE);
                                totalP4AmountTv.setText("= PROVIDE RATE");
                            }
                             totalP2CountTv.setText(sumCursor.getString(2));//total p2 count
                             totalP3CountTv.setText(sumCursor.getString(3));//total p3 count
                             totalP4CountTv.setText(sumCursor.getString(4));//total p4 count
                             skill1TextTv.setText(skillNRateCursor.getString(0)+" =");//setting skill 1
                             skill2TextTv.setText(skillNRateCursor.getString(1)+" =");//setting skill 2
                             skill3TextTv.setText(skillNRateCursor.getString(2)+" =");//setting skill 3
                             p2Layout.setVisibility(View.VISIBLE);
                             p3Layout.setVisibility(View.VISIBLE);
                             p4Layout.setVisibility(View.VISIBLE);
                            indicator1234CalculateButDontUpdateToDBFinal(sumCursor,skillNRateCursor.getInt(3) * sumCursor.getInt(1),skillNRateCursor.getInt(4) * sumCursor.getInt(2),skillNRateCursor.getInt(5) * sumCursor.getInt(3),skillNRateCursor.getInt(6) * sumCursor.getInt(4));
                        }
                    }
                    //if rate 1 is changed
                    p1RateTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                         }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String p11 =p1RateTv.getText().toString().trim();
                            p1RateTv.setTextColor(getColor(R.color.purple_700));
                            innerArray[0]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data
                            //this will check if other data is right or wrong
                            if(!isEnterDataIsWrong(innerArray)) {//this is important if in field data is wrong then save button will not enabled until data is right.
                                saveAndCreatePdf.setVisibility(View.VISIBLE);
                            }
                            if (!p11.matches("[0-9]+")) {//space or , or - is restricted"[.]?[0-9]+[.]?[0-9]*"
                                p1RateTv.setTextColor(Color.RED);
                                saveAndCreatePdf.setVisibility(View.GONE);
                                innerArray[0]=2;//means data is inserted wrong
                                Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            try{
                                r1 = Integer.parseInt(p1RateTv.getText().toString().trim());//updating r1 variable to take during saving
                                totalP1AmountTv.setText("= " + (r1 * p1));
                                updateTotalWorkAmountAndAdvanceOrBalanceTv();

                                if(isp1p2p3p4PresentAndRateNotPresent(r1,r2,r3,r4,p1,p2,p3,p4,indicate)){
                                    saveAndCreatePdf.setVisibility(View.GONE);
                                    advanceOrBalanceTv.setText("");
                                    workTotalAmountTv.setText(" - 0");
                                }

                            }catch(Exception e){
                                totalP1AmountTv.setText("= 0");
                                advanceOrBalanceTv.setText("");
                                workTotalAmountTv.setText(" - 0");
                            }
                        }
                    });
                    //if   rate 2 is changed
                    p2RateTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String p11 =p2RateTv.getText().toString().trim();
                            p2RateTv.setTextColor(getColor(R.color.purple_700));
                            innerArray[1]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data
                            //this will check if other data is right or wrong
                            if(!isEnterDataIsWrong(innerArray)) {//this is important if in field data is wrong then save button will not enabled until data is right.
                                saveAndCreatePdf.setVisibility(View.VISIBLE);

                            }
                            if (!p11.matches("[0-9]+")) {//space or , or - is restricted"[.]?[0-9]+[.]?[0-9]*"
                                p2RateTv.setTextColor(Color.RED);
                                saveAndCreatePdf.setVisibility(View.GONE);
                                innerArray[1]=2;//means data is inserted wrong
                                Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            try{
                                r2 = Integer.parseInt(p2RateTv.getText().toString().trim());//updating r1 variable to take during saving
                                totalP2AmountTv.setText("= " + (r2 * p2));
                                updateTotalWorkAmountAndAdvanceOrBalanceTv();

                                if(isp1p2p3p4PresentAndRateNotPresent(r1,r2,r3,r4,p1,p2,p3,p4,indicate)){
                                    saveAndCreatePdf.setVisibility(View.GONE);
                                    advanceOrBalanceTv.setText("");
                                    workTotalAmountTv.setText(" - 0");
                                }
                            }catch(Exception e){
                                totalP2AmountTv.setText("= 0");
                                advanceOrBalanceTv.setText("");
                                workTotalAmountTv.setText(" - 0");
                            }
                        }
                    });
                    //if   rate 3 is changed
                    p3RateTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String p11 =p3RateTv.getText().toString().trim();
                            p3RateTv.setTextColor(getColor(R.color.purple_700));
                            innerArray[2]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data
                            //this will check if other data is right or wrong
                            if(!isEnterDataIsWrong(innerArray)) {//this is important if in field data is wrong then save button will not enabled until data is right.
                                saveAndCreatePdf.setVisibility(View.VISIBLE);
                            }
                            if (!p11.matches("[0-9]+")) {//space or , or - is restricted"[.]?[0-9]+[.]?[0-9]*"
                                p3RateTv.setTextColor(Color.RED);
                                saveAndCreatePdf.setVisibility(View.GONE);
                                innerArray[2]=2;//means data is inserted wrong
                                Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            try {
                                r3 = Integer.parseInt(p3RateTv.getText().toString().trim());//updating r1 variable to take during saving
                                totalP3AmountTv.setText("= " + (r3 * p3));
                                updateTotalWorkAmountAndAdvanceOrBalanceTv();

                                if(isp1p2p3p4PresentAndRateNotPresent(r1,r2,r3,r4,p1,p2,p3,p4,indicate)){
                                    saveAndCreatePdf.setVisibility(View.GONE);
                                    advanceOrBalanceTv.setText("");
                                    workTotalAmountTv.setText(" - 0");
                                }
                            }catch (Exception e){
                                totalP3AmountTv.setText("= 0");
                                advanceOrBalanceTv.setText("");
                                workTotalAmountTv.setText(" - 0");
                            }
                        }
                    });
                    //if   rate 4 is changed
                    p4RateTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            String p11 =p4RateTv.getText().toString().trim();
                            p4RateTv.setTextColor(getColor(R.color.purple_700));
                            innerArray[3]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data
                            //this will check if other data is right or wrong
                            if(!isEnterDataIsWrong(innerArray)) {//this is important if in field data is wrong then save button will not enabled until data is right.
                                saveAndCreatePdf.setVisibility(View.VISIBLE);
                            }
                            if (!p11.matches("[0-9]+")) {//space or , or - is restricted"[.]?[0-9]+[.]?[0-9]*"
                                p4RateTv.setTextColor(Color.RED);
                                saveAndCreatePdf.setVisibility(View.GONE);
                                innerArray[3]=2;//means data is inserted wrong
                                Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            try {
                                r4 = Integer.parseInt(p4RateTv.getText().toString().trim());//updating r1 variable to take during saving
                                totalP4AmountTv.setText("= " + (r4 * p4));
                                updateTotalWorkAmountAndAdvanceOrBalanceTv();

                                if(isp1p2p3p4PresentAndRateNotPresent(r1,r2,r3,r4,p1,p2,p3,p4,indicate)){
                                    saveAndCreatePdf.setVisibility(View.GONE);
                                    advanceOrBalanceTv.setText("");
                                    workTotalAmountTv.setText(" - 0");
                                }
                            }catch(Exception e){
                                totalP4AmountTv.setText("= 0");
                                advanceOrBalanceTv.setText("");
                                workTotalAmountTv.setText(" - 0");
                            }
                        }
                    });
                    skillNRateCursor.close();
                    sumCursor.close();
                    saveAndCreatePdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(IndividualPersonDetailActivity.this, "MB "+(checkInternalStorageAvailability()*1000), Toast.LENGTH_SHORT).show();

                            if((checkInternalStorageAvailability()*1000) >= 50){//(checkInternalStorageAvailability()*1000) converted to MB so if it is greater or equal to 50 MB then true
                                if(checkPermissionForReadAndWriteToExternalStorage()) {//Take permission

                                    if(updateTotalAdvanceOrBalanceToDatabase()) {

                                        if(generatePDF()){

                                            if(viewPDF(fileName)) {//fileName is global variable actually its pdfPathAbsolute ie.pdf created in device so path of pdf which is in device

                                               if(modifyToDB(fileName)) {//fileName is global variable actually its pdfPathAbsolute ie.ie.pdf created in device so path of pdf which is in device
                                                   Toast.makeText(IndividualPersonDetailActivity.this, "operation successfull", Toast.LENGTH_LONG).show();
                                               }else {
                                                   Toast.makeText(IndividualPersonDetailActivity.this, "operation failed", Toast.LENGTH_LONG).show();
                                               }

                                            }else{
                                                Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO VIEW PDF", Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO GENERATE PDF", Toast.LENGTH_LONG).show();
                                        }

                                    }else {
                                        Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE ADVANCE OR BALANCE TO DB", Toast.LENGTH_LONG).show();
                                    }

                                }else {//request for permission
                                    Toast.makeText(IndividualPersonDetailActivity.this, "READ,WRITE EXTERNAL STORAGE PERMISSION REQUIRED", Toast.LENGTH_LONG).show();
                                    ActivityCompat.requestPermissions(IndividualPersonDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
                                }
                            }else//we will let user know what gone wrong
                                Toast.makeText(IndividualPersonDetailActivity.this, "FAILED-NO SUFFICIENT MEMORY \nINTERNAL MEMORY AVAILABLE: "+(checkInternalStorageAvailability()*1000)+" MB", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.show();
                    return false;
                }

                private boolean modifyToDB(String pdfPath) {
                    try(PersonRecordDatabase personDb=new PersonRecordDatabase(getApplicationContext())) {//so that object close automatically
                        byte[] pdfBytes = Files.readAllBytes(Paths.get(pdfPath));//CONVERTED pdf file to byte array if path is not found then catch block execute

                        if (savePdfToDatabase(pdfBytes)){//store pdf to database
                                //this function will be written when existing from pdf viewer
//                                boolean deleted = deletePdfFromDevice(pdfPath);
//                                if (!deleted) {
//                                    Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO DELETE PDF FILE FROM DEVICE", Toast.LENGTH_SHORT).show();
//                                }

                                if(deleteWagesFromDBorRecyclerView(fromIntentPersonId)) {//delete records from recycle view this should be perform first so that update will be visible

                                    if(!updateAdvanceOrBalanceToDBorRecyclerView(fromIntentPersonId)){ //update balance or advance to db
                                        Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE RECYCLER VIEW", Toast.LENGTH_LONG).show();
                                        personDb.insert_1_Person_WithWagesTable2(fromIntentPersonId, "0-0-0", "0:0:0:0", null,   "3rd OPERATION FAILED TO UPDATE RECYCLER VIEW" , 0, 0, "0");
                                        return false;
                                    }
                                }else{
                                    Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO DELETE RECORD FROM DB", Toast.LENGTH_LONG).show();
                                    personDb.insert_1_Person_WithWagesTable2(fromIntentPersonId, "0-0-0", "0:0:0:0", null,   "2nd OPERATION FAILED TO DELETE RECORD FROM DB" , 0, 0, "0");
                                    return false;
                                }
                            }else{
                                Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO SAVE PDF IN DB", Toast.LENGTH_LONG).show();
                                personDb.insert_1_Person_WithWagesTable2(fromIntentPersonId, "0-0-0", "0:0:0:0", null,   "1st OPERATION FAILED TO SAVE PDF IN DB" , 0, 0, "0");
                                return false;
                            }
                    }catch (IOException ex) {
                        Toast.makeText(IndividualPersonDetailActivity.this, "PDF File not Found IOException", Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                        return false;
                    }
                    catch (Exception ex){
                        Toast.makeText(IndividualPersonDetailActivity.this, "PDF File not Found Exception", Toast.LENGTH_LONG).show();
                        ex.printStackTrace();
                        return false;
                    }
                      return true;
                }

                private boolean deleteWagesFromDBorRecyclerView(String fromIntentPersonId) {
                    try(PersonRecordDatabase personDb=new PersonRecordDatabase(getApplicationContext());
                        Cursor cursor = personDb.getData("SELECT ID FROM " + db.TABLE_NAME2 + " WHERE ID= '" + fromIntentPersonId + "'");
                       ){//so that object close automatically
                        cursor.moveToFirst();
                        if(cursor.getCount()==0){//if already record not presend then return true
                            return true;
                        }
                        boolean success=personDb.deleteRows(fromIntentPersonId,personDb.TABLE_NAME2);
                        if(success)
                            return true;
                        return false;
                    }
                }
                private boolean updateAdvanceOrBalanceToDBorRecyclerView(String fromIntentPersonId) {
                    PersonRecordDatabase db=null;
                    Cursor cursor=null;
                    try {
                          db = new PersonRecordDatabase(getApplicationContext());//so db close automatically
                        int amount=0;
                        boolean success;
                        final Calendar current = Calendar.getInstance();//to get current date
                        String date = current.get(Calendar.DAY_OF_MONTH) + "-" + (current.get(Calendar.MONTH) + 1) + "-" + current.get(Calendar.YEAR);

                        Date d = Calendar.getInstance().getTime(); //To get exact time so write code in save button
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");//a stands for is AM or PM
                        String time = sdf.format(d);
                        cursor = db.getData("SELECT ADVANCE,BALANCE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId + "'");
                        cursor.moveToFirst();//means only one row is returned
                        if (cursor.getInt(0) != 0 && cursor.getInt(1) == 0) {
                            amount = cursor.getInt(0);
                            //insert to database taking just first person                                                      //remarks
                            success = db.insert_1_Person_WithWagesTable2(fromIntentPersonId, date, time, null, "[" + time + "]-[ENTERED]\n\n" + "Advance after calculation: " + amount, amount, 0, "0");
                            if (!success)
                                return false;
                        }
                        if (cursor.getInt(0) == 0 && cursor.getInt(1) != 0) {
                            amount = cursor.getInt(1);
                            //insert to database taking just first person                                                      //remarks
                            success = db.insert_Deposit_Table2(fromIntentPersonId, date, time, null, "[" + time + "]-[ENTERED]\n\n" + "Balance after calculation: " + amount, amount, "1");
                            if (!success)
                                return false;
                        }
                        return true;
                    }finally {
                        try {
                            if (cursor != null)
                                cursor.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        try {
                            if (db != null)
                                db.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
                private boolean deletePdfFromDevice(String pdfPath) {
                    File filePath= new File(pdfPath);//file to be delete
                    if(filePath.exists()) {
                        return filePath.delete();
                    }
                    return false;//file not deleted or file not existed
                }
                private boolean savePdfToDatabase(byte[] newPDF) {
                    try(PersonRecordDatabase db=new PersonRecordDatabase(getApplicationContext());
                        Cursor cursor= db.getData("SELECT PDF1 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");) {//so that object close automatically
                        cursor.moveToFirst();
                        if(cursor.getBlob(0)==null){//if pdf1 is null then store in pdf1
                            Toast.makeText(IndividualPersonDetailActivity.this, "pdf not there", Toast.LENGTH_LONG).show();
//                            cursor.close();
                            return db.insertPdf(fromIntentPersonId, newPDF,1);
                        }
                        //if pdf1 is not null then store in pdf 2
                        byte[] pdf1 = cursor.getBlob(0);
                        Toast.makeText(IndividualPersonDetailActivity.this, "pdf there", Toast.LENGTH_LONG).show();
                        db.insertPdf(fromIntentPersonId, pdf1,2);
                        return db.insertPdf(fromIntentPersonId, newPDF,1);
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }
                private boolean viewPDF(String pdfPath) {
                    try {//to view pdf
                        Intent intent=new Intent(IndividualPersonDetailActivity.this, Final_Pdf_Viewer.class);
                        intent.putExtra("pdfurl",pdfPath);
                        intent.putExtra("ID",fromIntentPersonId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                        return true;
                    }catch(Exception e){
                        Toast.makeText(IndividualPersonDetailActivity.this, "CANNOT FIND PATH", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    return  false;
                }
                private boolean generatePDF() {
                    //create PDF
                            /*PdfDocument myPdfDocument=new PdfDocument();//pdf instance
                            Paint myPaint=new Paint();//it is responsible for text color

                            PdfDocument.PageInfo myPageInfo=new PdfDocument.PageInfo.Builder(250,400,1).create();//meta data of pdf
                            PdfDocument.Page mypage1=myPdfDocument.startPage(myPageInfo);
                            //to write in pdf page 1
                            Canvas canvas=mypage1.getCanvas();
                            canvas.drawText("WELCOME  AMAR KUMAR DAS 1 love you ................",10,50,myPaint);
                            myPdfDocument.finishPage(mypage1);

                            PdfDocument.PageInfo myPageInfo2=new PdfDocument.PageInfo.Builder(250,400,1).create();//meta data of pdf
                            PdfDocument.Page mypage2=myPdfDocument.startPage(myPageInfo2);
                            //to write in pdf page 2
                            Canvas canvas2=mypage2.getCanvas();
                            canvas2.drawText("WELCOME  AMAR KUMAR DAS 2 love you ................",10,50,myPaint);
                            myPdfDocument.finishPage(mypage2);*/
                  try {
                      PdfDocument myPdfDocument = new PdfDocument();//pdf instance
                      Paint myPaint = new Paint();//it is responsible for text color

                      PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250, 400, 1).create();//meta data of pdf
                      PdfDocument.Page mypage = myPdfDocument.startPage(myPageInfo);

                      //to write in pdf page 1
                      Canvas canvas = mypage.getCanvas();
                      myPaint.setTextAlign(Paint.Align.CENTER);
                      myPaint.setTextSize(12.0f);                 //so that it will be in middle
                      canvas.drawText("HR Enterprises", myPageInfo.getPageWidth() / 2, 30, myPaint);

                      myPaint.setTextSize(6.0f);
                      //myPaint.setColor(Color.rgb(122,199,199));// myPaint.setColor(getResources().getColor(R.color.green)); or
                      myPaint.setColor(ContextCompat.getColor(IndividualPersonDetailActivity.this, R.color.background));
                      canvas.drawText("Street No. 15,Bharat Nagar,Haryana", myPageInfo.getPageWidth() / 2, 40, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(9.0f);
                      myPaint.setColor(Color.rgb(122, 199, 199));//this is here because if in future upper color is changed then this link color will not change
                      canvas.drawText("Customer Information", 10, 70, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(8.0f);
                      myPaint.setColor(Color.BLACK);

                      String information[] = new String[]{"Name", "Company Name", "Address", "Phone", "Email"};
                      int startXPosition = 10;
                      int endXPosition = myPageInfo.getPageWidth() - 10;
                      int startYPosition = 100;

                      for (int i = 0; i < 5; i++) {
                          canvas.drawText(information[i], startXPosition, startYPosition, myPaint);
                          canvas.drawLine(startXPosition, startYPosition + 3, endXPosition, startYPosition + 3, myPaint);
                          startYPosition = startYPosition + 20;
                      }
                      canvas.drawLine(80, 92, 80, 190, myPaint);

                      myPaint.setStyle(Paint.Style.STROKE);
                      myPaint.setStrokeWidth(2);
                      canvas.drawRect(10, 200, myPageInfo.getPageWidth() - 10, 300, myPaint);
                      canvas.drawLine(85, 200, 85, 300, myPaint);
                      canvas.drawLine(163, 200, 163, 300, myPaint);
                      myPaint.setStrokeWidth(0);
                      myPaint.setStyle(Paint.Style.FILL);//Geometry and text drawn with this style will be filled, ignoring all stroke-related settings in the paint.

                      canvas.drawText("Photo", 35, 250, myPaint);
                      canvas.drawText("Photo", 110, 250, myPaint);
                      canvas.drawText("Photo", 190, 250, myPaint);

                      canvas.drawText("Note", 10, 320, myPaint);
                      canvas.drawLine(35, 325, myPageInfo.getPageWidth() - 10, 325, myPaint);
                      canvas.drawLine(10, 345, myPageInfo.getPageWidth() - 10, 345, myPaint);
                      canvas.drawLine(10, 365, myPageInfo.getPageWidth() - 10, 365, myPaint);
                      myPdfDocument.finishPage(mypage);
//----------------------------------------------------------------------------------------------------------------------
                      PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(250, 400, 1).create();//meta data of pdf
                      PdfDocument.Page mypage3 = myPdfDocument.startPage(myPageInfo3);
                      //to write in pdf page 1
                      Canvas canvas3 = mypage3.getCanvas();
                      myPaint.setTextAlign(Paint.Align.CENTER);
                      myPaint.setTextSize(12.0f);                 //so that it will be in middle
                      canvas3.drawText("HR Enterprises", myPageInfo3.getPageWidth() / 2, 30, myPaint);

                      myPaint.setTextSize(6.0f);
                      //myPaint.setColor(Color.rgb(122,199,199));// myPaint.setColor(getResources().getColor(R.color.green)); or
                      myPaint.setColor(ContextCompat.getColor(IndividualPersonDetailActivity.this, R.color.background));
                      canvas3.drawText("Street No. 15,Bharat Nagar,Haryana", myPageInfo3.getPageWidth() / 2, 40, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(9.0f);
                      myPaint.setColor(Color.rgb(122, 199, 199));//this is here because if in future upper color is changed then this link color will not change
                      canvas3.drawText("Customer Information", 10, 70, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(8.0f);
                      myPaint.setColor(Color.BLACK);

                      String information3[] = new String[]{"Name", "Company Name", "Address", "Phone", "Email"};
                      int startXPosition3 = 10;
                      int endXPosition3 = myPageInfo.getPageWidth() - 10;
                      int startYPosition3 = 100;

                      for (int i = 0; i < 5; i++) {
                          canvas3.drawText(information3[i], startXPosition, startYPosition3, myPaint);
                          canvas3.drawLine(startXPosition3, startYPosition3 + 3, endXPosition3, startYPosition3 + 3, myPaint);
                          startYPosition3 = startYPosition3 + 20;
                      }
                      canvas3.drawLine(80, 92, 80, 190, myPaint);

                      myPaint.setStyle(Paint.Style.STROKE);
                      myPaint.setStrokeWidth(2);
                      canvas3.drawRect(10, 200, myPageInfo3.getPageWidth() - 10, 300, myPaint);
                      canvas3.drawLine(85, 200, 85, 300, myPaint);
                      canvas3.drawLine(163, 200, 163, 300, myPaint);
                      myPaint.setStrokeWidth(0);
                      myPaint.setStyle(Paint.Style.FILL);//Geometry and text drawn with this style will be filled, ignoring all stroke-related settings in the paint.

                      canvas3.drawText("Photo", 35, 250, myPaint);
                      canvas3.drawText("Photo", 110, 250, myPaint);
                      canvas3.drawText("Photo", 190, 250, myPaint);

                      canvas3.drawText("Note", 10, 320, myPaint);
                      canvas3.drawLine(35, 325, myPageInfo3.getPageWidth() - 10, 325, myPaint);
                      canvas3.drawLine(10, 345, myPageInfo3.getPageWidth() - 10, 345, myPaint);
                      canvas3.drawLine(10, 365, myPageInfo3.getPageWidth() - 10, 365, myPaint);
                      myPdfDocument.finishPage(mypage3);
//----------------------------------------------------------------------------------
                      PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(250, 400, 1).create();//meta data of pdf
                      PdfDocument.Page mypage4 = myPdfDocument.startPage(myPageInfo4);
                      //to write in pdf page 1
                      Canvas canvas4 = mypage4.getCanvas();
                      myPaint.setTextAlign(Paint.Align.CENTER);
                      myPaint.setTextSize(12.0f);                 //so that it will be in middle
                      canvas4.drawText("HR Enterprises", myPageInfo4.getPageWidth() / 2, 30, myPaint);

                      myPaint.setTextSize(6.0f);
                      //myPaint.setColor(Color.rgb(122,199,199));// myPaint.setColor(getResources().getColor(R.color.green)); or
                      myPaint.setColor(ContextCompat.getColor(IndividualPersonDetailActivity.this, R.color.background));
                      canvas4.drawText("Street No. 15,Bharat Nagar,Haryana", myPageInfo4.getPageWidth() / 2, 40, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(9.0f);
                      myPaint.setColor(Color.rgb(122, 199, 199));//this is here because if in future upper color is changed then this link color will not change
                      canvas4.drawText("Customer Information", 10, 70, myPaint);

                      myPaint.setTextAlign(Paint.Align.LEFT);
                      myPaint.setTextSize(8.0f);
                      myPaint.setColor(Color.BLACK);

                      String information4[] = new String[]{"Name", "Company Name", "Address", "Phone", "Email"};
                      int startXPosition4 = 10;
                      int endXPosition4 = myPageInfo.getPageWidth() - 10;
                      int startYPosition4 = 100;

                      for (int i = 0; i < 5; i++) {
                          canvas4.drawText(information4[i], startXPosition, startYPosition4, myPaint);
                          canvas4.drawLine(startXPosition4, startYPosition4 + 3, endXPosition4, startYPosition4 + 3, myPaint);
                          startYPosition4 = startYPosition4 + 20;
                      }
                      canvas4.drawLine(80, 92, 80, 190, myPaint);

                      myPaint.setStyle(Paint.Style.STROKE);
                      myPaint.setStrokeWidth(2);
                      canvas4.drawRect(10, 200, myPageInfo4.getPageWidth() - 10, 300, myPaint);
                      canvas4.drawLine(85, 200, 85, 300, myPaint);
                      canvas4.drawLine(163, 200, 163, 300, myPaint);
                      myPaint.setStrokeWidth(0);
                      myPaint.setStyle(Paint.Style.FILL);//Geometry and text drawn with this style will be filled, ignoring all stroke-related settings in the paint.

                      canvas4.drawText("Photo", 35, 250, myPaint);
                      canvas4.drawText("Photo", 110, 250, myPaint);
                      canvas4.drawText("Photo", 190, 250, myPaint);

                      canvas4.drawText("Note", 10, 320, myPaint);
                      canvas4.drawLine(35, 325, myPageInfo4.getPageWidth() - 10, 325, myPaint);
                      canvas4.drawLine(10, 345, myPageInfo4.getPageWidth() - 10, 345, myPaint);
                      canvas4.drawLine(10, 365, myPageInfo4.getPageWidth() - 10, 365, myPaint);
                      myPdfDocument.finishPage(mypage4);


                      File folder = new File(getExternalFilesDir(null) + "/acBookPDF");
                      if (!folder.exists()) {//of folder not exist then create folder
                          folder.mkdir();//File createNewFile() method returns true if new file is created and false if file already exists.
                          Toast.makeText(IndividualPersonDetailActivity.this, "Creating acBookPDF folder to store PDF", Toast.LENGTH_LONG).show();
                      }

                      File filees = new File(getExternalFilesDir(null) + "/acBookPDF/" +generateFileName(fromIntentPersonId) + ".pdf");//path of pdf where it is saved in device

                      try {
                          myPdfDocument.writeTo(new FileOutputStream(filees.getAbsolutePath()));//if FileOutputStream cannot find file then it will create automatically
                      } catch (IOException e) {
                          Toast.makeText(IndividualPersonDetailActivity.this, "CREATED PDF NOT COPIED TO DEVICE PDF FILE", Toast.LENGTH_LONG).show();
                          e.printStackTrace();
                      }
                      myPdfDocument.close();
                      Toast.makeText(IndividualPersonDetailActivity.this, "created", Toast.LENGTH_SHORT).show();
                      fileName = filees.getAbsolutePath();//fileName is global variable
                  }catch (Exception i){
                      return false;
                  }
                    return true;
                }
                private String generateFileName(String ID) {
                    final Calendar current=Calendar.getInstance();//to get current date and time
                    Date d=Calendar.getInstance().getTime();//To get time
                    SimpleDateFormat sdf=new SimpleDateFormat("hhmma");//a stands for is AM or PM
                    return "id"+ID+"date"+current.get(Calendar.DAY_OF_MONTH)+"_"+(current.get(Calendar.MONTH)+1)+"_"+current.get(Calendar.YEAR)+"at"+sdf.format(d);
                }
                private void displFinalResult(String title,String message) {
                    AlertDialog.Builder showDataFromDataBase=new AlertDialog.Builder(IndividualPersonDetailActivity.this);
                    showDataFromDataBase.setCancelable(false);
                    showDataFromDataBase.setTitle(title);
                    showDataFromDataBase.setMessage(message);
                    showDataFromDataBase.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {//REFRESHING
                            dialogInterface.dismiss();//close current dialog
                            Intent intent=new Intent(IndividualPersonDetailActivity.this,IndividualPersonDetailActivity.class);
                            intent.putExtra("ID",fromIntentPersonId);
                            finish();//while going to other activity so destroy  this current activity so that while coming back we will see refresh activity
                            startActivity(intent);
                        }
                    });
                    showDataFromDataBase.create().show();
                }
                private float checkInternalStorageAvailability(){
                    File path = Environment.getDataDirectory();//Return the user data directory.return type FILE and Environment class Provides access to environment variables.
                    StatFs stat = new StatFs(path.getPath());//Construct a new StatFs for looking at the stats of the filesystem at path.
                    long blockSize = stat.getBlockSizeLong();//The size, in bytes, of a block on the file system. This corresponds to the Unix statvfs.f_frsize field.
                    long availableBlocks = stat.getAvailableBlocksLong();//The number of bytes that are free on the file system and available to applications.
                    String format = Formatter.formatFileSize(IndividualPersonDetailActivity.this, availableBlocks * blockSize);//return available internal storage memory like 9.66 GB
                    format=format.trim();//for safer side

                    StringBuffer internalStorage=new StringBuffer();
                    for(int i=0;i<format.length();i++){
                        if(format.charAt(i) == ' ' || Character.isAlphabetic(format.charAt(i)))
                            break;
                        internalStorage.append(format.charAt(i));
                    }
                    return  Float.parseFloat(internalStorage.toString());
                }
                private boolean checkPermissionForReadAndWriteToExternalStorage() {
                    if( (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
                        return true;
                    }else
                        return false;
                }
                private boolean updateTotalAdvanceOrBalanceToDatabase( ) {
                    //updating rate
                    boolean success = db.updateTable("UPDATE " + db.TABLE_NAME3 + " SET R1='"+r1+"' , R2='"+r2+"' , R3='"+r3+"' , R4='"+r4+"'"+ " WHERE ID='" + fromIntentPersonId + "'");
                    if(success){//if rate is updated then proceed
                       if (!isEnterDataIsWrong(innerArray)) {//if data is right then only change fields.This condition is already checked but checking again
                           if (!isp1p2p3p4PresentAndRateNotPresent(r1, r2, r3, r4, p1, p2, p3, p4, indicate)) {//This condition is already checked but checking again
                               //if both wages and totalwork amount is less then 0 then dont save.This condition already checked but checking again

                               if (((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) < 0) || (totalWages < 0)) {//user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
                                   Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO SAVE DUE TO WRONG DATA", Toast.LENGTH_LONG).show();
                                   return false;
                               } else if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) < totalWages) {
                                   //updating Advance to db
                                   success = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + (totalWages - (totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4)))) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                                   if (!success) {
                                       Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE ADVANCE AMOUNT TO DB", Toast.LENGTH_LONG).show();
                                       return false;
                                   }
                                   //if there is advance then balance  column should be 0
                                   success = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                                   if (!success) {
                                       Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE BALANCE AMOUNT TO DB", Toast.LENGTH_LONG).show();
                                       return false;
                                      }
                               } else if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) >= totalWages) {//>= is given because when totalWages and totalwork is same then this condition will be executed to set balance 0

                                   //updating balance to db if greater then 0
                                   success = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) - totalWages) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                                   if (!success) {
                                       Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE BALANCE AMOUNT TO DB", Toast.LENGTH_LONG).show();
                                       return false;
                                   }
                                   //if there is balance then update advance column should be 0
                                   success = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                                   if (!success) {
                                       Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE ADVANCE AMOUNT TO DB", Toast.LENGTH_LONG).show();
                                       return false;
                                   }
                               }
                           } else {
                               Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO SAVE DUE TO RATE NOT PROVIDED", Toast.LENGTH_LONG).show();
                               return false;
                           }
                       } else {
                           Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO SAVE DUE TO WRONG DATA", Toast.LENGTH_LONG).show();
                           return false;
                       }
                   }else {
                        Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO UPDATE RATE", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                }
                public boolean isp1p2p3p4PresentAndRateNotPresent(int r1,int r2,int r3,int r4,int p1,int p2,int p3,int p4,int indicator){
                    if(indicator==1 && (p1 !=0 && r1==0)){
                        return true;
                    }else if(indicator==2 && ((p1 !=0 && r1==0) || (p2 !=0 && r2==0))){
                        return true;
                    }else if(indicator==3 && ((p1 !=0 && r1==0) || (p2 !=0 && r2==0) || (p3 !=0 && r3==0))){
                        return true;
                    }else if(indicator==4 && ((p1 !=0 && r1==0) || (p2 !=0 && r2==0) || (p3 !=0 && r3==0) || (p4 !=0 && r4==0))){
                        return true;
                    }
                    return false;
                }
                private void updateTotalWorkAmountAndAdvanceOrBalanceTv() {
                    if(!isEnterDataIsWrong(innerArray)) {//if data is right then only change fields
                        workTotalAmountTv.setText(" - " + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4) + totalDeposit));

                        //if both wages and totalwork amount is less then 0 then both message have to show so if statement two times
                        if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) < 0) {//user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
                            Toast.makeText(IndividualPersonDetailActivity.this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
                            saveAndCreatePdf.setVisibility(View.GONE);//its important otherwise save option will be unabled when user enter rate
                        }
                        if(totalWages < 0){//its important otherwise save option will be unabled when user enter rate
                            Toast.makeText(IndividualPersonDetailActivity.this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WAGES", Toast.LENGTH_LONG).show();
                            saveAndCreatePdf.setVisibility(View.GONE);
                        }

                        if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) < totalWages) {
                            advanceOrBalanceTv.setTextColor(Color.RED);
                            //                                        total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
                            advanceOrBalanceTv.setText("= " + (totalWages - (totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4)))));
                            //       totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) >= totalWages
                        } else if ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) >= totalWages) {//>= is given because of green color
                            advanceOrBalanceTv.setTextColor(getColor(R.color.green));
                            //                                           totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) -total wages
                            advanceOrBalanceTv.setText("= " + ((totalDeposit + ((p1 * r1) + (p2 * r2) + (p3 * r3) + (p4 * r4))) - totalWages));
                        }
                    }else{
                        advanceOrBalanceTv.setText("= 0");
                        advanceOrBalanceTv.setTextColor(getColor(R.color.green));
                        workTotalAmountTv.setText(" - 0");
                    }

                }
                private void indicator1234CalculateButDontUpdateToDBFinal(@NonNull Cursor sumCursor, int rate1IntoSump1, int rate2IntoSump2, int rate3IntoSump3, int rate4IntoSump4) {
                    int  totalDeposit,totalWages;
                    int totalr1r2r3r4sum1sum2sum3sum4=rate1IntoSump1+rate2IntoSump2+rate3IntoSump3+rate4IntoSump4;
                    totalWages=sumCursor.getInt(0);
                    totalDeposit=sumCursor.getInt(5);

                    if(((totalDeposit + totalr1r2r3r4sum1sum2sum3sum4) < 0) || (totalr1r2r3r4sum1sum2sum3sum4 < 0) || (totalDeposit < 0)) //user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
                        Toast.makeText(IndividualPersonDetailActivity.this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();

                    workTotalAmountTv.setText(" - " + (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)));
                    //    totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
                    if ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) < totalWages) {
                        advanceOrBalanceTv.setTextColor(Color.RED);
                        //                                total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
                        advanceOrBalanceTv.setText("= " + (totalWages - (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))));

                        //totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) >= totalWages
                    }else if((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) >= totalWages) {//>= is given because of green color and when calculation is 0
                        advanceOrBalanceTv.setTextColor(getColor(R.color.green));
                        //                                   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) -total wages
                        advanceOrBalanceTv.setText("= " + ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) -totalWages));
                    }
                }
                private void initialiseIDs(View myView) {
                      defaultSkillTextTv=myView.findViewById(R.id.default_skill_text_tv_final);
                      totalP1CountTv=myView.findViewById(R.id.total_p1_count_tv_final);
                      p1RateTv=myView.findViewById(R.id.p1_rate_tv_final);
                      totalP1AmountTv=myView.findViewById(R.id.total_p1_amount_tv_final);

                      skill1TextTv=myView.findViewById(R.id.skill1_text_tv_final);
                      totalP2CountTv=myView.findViewById(R.id.total_p2_count_tv_final);
                      p2RateTv=myView.findViewById(R.id.p2_rate_tv_final);
                      totalP2AmountTv=myView.findViewById(R.id.total_p2_amount_tv_final);

                      skill2TextTv=myView.findViewById(R.id.skill2_text_tv_final);
                      totalP3CountTv=myView.findViewById(R.id.total_p3_count_tv_final);
                      p3RateTv=myView.findViewById(R.id.p3_rate_tv_final);
                      totalP3AmountTv=myView.findViewById(R.id.total_p3_amount_tv_final);

                      skill3TextTv=myView.findViewById(R.id.skill3_text_tv_final);
                      totalP4CountTv=myView.findViewById(R.id.total_p4_count_tv_final);
                      p4RateTv=myView.findViewById(R.id.p4_rate_tv_final);
                      totalP4AmountTv=myView.findViewById(R.id.total_p4_amount_tv_final);

                      totalDepositAmountTv=myView.findViewById(R.id.total_deposit_amount_tv_final);
                      wagesTotalAmountTv=myView.findViewById(R.id.wages_total_amount_tv_final);
                      workTotalAmountTv=myView.findViewById(R.id.work_total_amount_tv_final);
                      advanceOrBalanceTv=myView.findViewById(R.id.advance_or_balance_tv_final);

                      p2Layout=myView.findViewById(R.id.p2_layout_final);
                      p3Layout=myView.findViewById(R.id.p3_layout_final);
                      p4Layout=myView.findViewById(R.id.p4_layout_final);
                      totalDepositAmountLayout=myView.findViewById(R.id.total_deposit_amount_layout_final);

                      saveAndCreatePdf=myView.findViewById(R.id.save_btn_final);
                      cancel=myView.findViewById(R.id.cancel_btn_final);
                }
            });
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
    private void indicator1234CalculateAndUpdate(@NonNull Cursor sumCursor, int rate1IntoSump1, int rate2IntoSump2, int rate3IntoSump3, int rate4IntoSump4) {
        boolean bool;
        int  totalDeposit,totalWages;
        int totalr1r2r3r4sum1sum2sum3sum4=rate1IntoSump1+rate2IntoSump2+rate3IntoSump3+rate4IntoSump4;
        totalWages=sumCursor.getInt(0);
        totalDeposit=sumCursor.getInt(5);

        if(((totalDeposit + totalr1r2r3r4sum1sum2sum3sum4) < 0) || (totalr1r2r3r4sum1sum2sum3sum4 < 0) || (totalDeposit < 0)) //user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
            Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();

        binding.workTotalAmountTv.setText(" - " + (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)));
        //    totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
        if ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) < totalWages) {
            binding.advanceOrBalanceTv.setTextColor(Color.RED);
            //                                        total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
            binding.advanceOrBalanceTv.setText("= " + (totalWages - (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))));

            //updating Advance to db                                                    total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + (totalWages - (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
            if(bool==true){
                /**Situation when user first enter jama /totalDeposit amount then wages amount which is greater then jama amount then balance column should be updated otherwise advance coulmn will have amount and balance column will also have amount so when there is advance then balance should be 0.*/
                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                if (bool == false)
                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
            }
            else if (bool == false)
                Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();

            //totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) >= totalWages
        }else if((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) >= totalWages) {//>= is given because of green color and when calculation is 0
            binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
            //                                           totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) -total wages
            binding.advanceOrBalanceTv.setText("= " + ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) -totalWages));

            //updating balance to db if greater then or equal to 0
            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) -totalWages) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
            if(bool == true){
                //if there is balance then update advance column should be 0
                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
                if (bool == false)
                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
            }
            else if(bool == false)
                Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(this, "r1*p1="+rate1IntoSump1+" r2*p2="+rate2IntoSump2+"\n"+"r3*p3="+rate3IntoSump3+" r4*p4="+rate4IntoSump4+"\n"+" wages"+totalWages+" deposit"+totalDeposit, Toast.LENGTH_LONG).show();
//        int  rate1IntoSump1,rate2IntoSump2,rate3IntoSump3,rate4IntoSump4,rate2,rate3,rate4;
//        rate2=skillCursor.getInt(4);
//        rate3=skillCursor.getInt(5);
//        rate4=skillCursor.getInt(6);
//        rate1IntoSump1=skillCursor.getInt(3) * sumCursor.getInt(1);// (R1*SUMP1)
//        rate2IntoSump2=skillCursor.getInt(4) * sumCursor.getInt(2);// (R2*SUMP2)
//        rate3IntoSump3=skillCursor.getInt(5) * sumCursor.getInt(3);// (R3*SUMP3)
//        rate4IntoSump4=skillCursor.getInt(6) * sumCursor.getInt(4);// (R4*SUMP4)
    //    /** (rate2 != 0) || (rate2 != 0 && rate3 != 0 ) || (rate2 != 0 && rate3 != 0 && rate4 != 0) this condition satisfy when there is only two rate and rest 0 then this condition will be true for every operation*/

        //totalDeposit           (                                  R2R3R4 rate                                           )     wages
         //                            2                     3                                      4
//        if (totalDeposit != 0 &&( (rate2 != 0) || (rate2 != 0 && rate3 != 0 ) || (rate2 != 0 && rate3 != 0 && rate4 != 0) )&&  totalWages != 0) {
//             //                                          totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            binding.workTotalAmountTv.setText(" - " + (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)));
//            Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
//            if((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) < 0) {//user cant enter negative number so when (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//            //    totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            if ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) < totalWages) {
//                binding.advanceOrBalanceTv.setTextColor(Color.RED);
//                //                                        total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//                binding.advanceOrBalanceTv.setText("= " + (totalWages - (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))));
//
//                //updating Advance to db ****************                                   total wages -   totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + (totalWages - (totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//                /**Situation when user first enter jama /totalDeposit amount then wages amount which is greater then jama amount then balance column should be updated otherwise advance coulmn will have amount and balance column will also have amount so when there is advance then balance should be 0.*/
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//                //       totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) >= totalWages
//            } else if ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) >= totalWages) {//>= is given because of green color
//                binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
//                //                                           totalDeposit+(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) -total wages
//                binding.advanceOrBalanceTv.setText("= " + ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) -totalWages));
//
//                //updating balance to db if greater then 0
//
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + ((totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4)) -totalWages) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//                //if balance there is balance then update advance column should be 0
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            }
//        }
//        //        no totalDeposit     (                                         rate                                         )      wages
//        //                                   2                      3                                         4
//        else if (totalDeposit == 0 && ((rate2 != 0) || (rate2 != 0 && rate3 != 0 ) || (rate2 != 0 && rate3 != 0 && rate4 != 0))&& totalWages != 0) {
//             //                                       (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            binding.workTotalAmountTv.setText("  - " + (totalr1r2r3r4sum1sum2sum3sum4));
//            Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
//            if((totalr1r2r3r4sum1sum2sum3sum4) < 0) {//user cant enter negative number so when totalr1r2r3r4sum1sum2sum3sum4 is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//
//            //(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) < total wages
//            if ((totalr1r2r3r4sum1sum2sum3sum4) < totalWages) {
//                binding.advanceOrBalanceTv.setTextColor(Color.RED);
//                //                                        total wages-(R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//                binding.advanceOrBalanceTv.setText("= " + (totalWages- ((totalr1r2r3r4sum1sum2sum3sum4))));
//
//                //updating Advance to db
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + ((totalWages- ((totalr1r2r3r4sum1sum2sum3sum4))) ) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//                //if advance   is there then update balance column should be 0 because both cant have value
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//                //     (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) >= total wages
//            } else if ((totalr1r2r3r4sum1sum2sum3sum4) >= totalWages) {//>= equal is given because of green color
//                binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
//                //                             (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4) -total wages
//                binding.advanceOrBalanceTv.setText("= " + ((totalr1r2r3r4sum1sum2sum3sum4) - totalWages));
//
//                //updating balance to db
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + (((totalr1r2r3r4sum1sum2sum3sum4) - totalWages)) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//                //if balance there is balance then update advance column should be 0
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            }
//        }
//        //        totalDeposit         (                                  no  R2R3R4                                           )    no wage
//        //                                       2                 3                                    4
//        else if (totalDeposit != 0 &&( (rate2 == 0) || (rate2 == 0 && rate3 == 0 ) || (rate2 == 0 && rate3 == 0 && rate4 == 0) )&& totalWages == 0) {
//             //                                           totalDeposit
//            binding.workTotalAmountTv.setText(" - " + totalDeposit);
//            Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
//            if(totalDeposit < 0) {//user cant enter negative number so when (totalDeposit) is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//            //                                               totalDeposit
//            binding.advanceOrBalanceTv.setText("= " + totalDeposit);
//
//            //updating balance to db
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" +totalDeposit + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            //if balance there is balance then update advance column should be 0
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//            binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
//        }
//        //           totalDeposit     (                     no  R2R3R4                                                         )      wages
//        //                                   2                     3                                          4
//        else if (totalDeposit != 0 &&( (rate2 == 0) || (rate2 == 0 && rate3 == 0 ) || (rate2 == 0 && rate3 == 0 && rate4 == 0) )&& totalWages != 0) {
//                                             //        totalDeposit
//            binding.workTotalAmountTv.setText("  - " + totalDeposit);
//            Toast.makeText(this, "4", Toast.LENGTH_SHORT).show();
//            if(totalDeposit < 0) {//user cant enter negative number so when (totalDeposit) is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//            //    totalDeposit < total wages
//            if ((totalDeposit) < totalWages) {
//                binding.advanceOrBalanceTv.setTextColor(Color.RED);
//                //                                        total wages -    totalDeposit
//                binding.advanceOrBalanceTv.setText("= " + (totalWages - (totalDeposit)));
//
//                //updating Advance to db                                                            total wages    - totalDeposit
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + (totalWages - (totalDeposit)) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//                //if advance   is there then update balance column should be 0 because both cant have value
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//
//                //       totalDeposit >= total wages
//            } else if ((totalDeposit) >= totalWages) {//>= equal is given because of green color
//                binding.advanceOrBalanceTv.setTextColor(getResources().getColor(R.color.green));
//                //                                         totalDeposit -total wages
//                binding.advanceOrBalanceTv.setText("= " + (totalDeposit - totalWages));
//
//                //updating balance to db                                                     totalDeposit -total wages
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + (totalDeposit -totalWages) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//                //if balance there is balance then update advance column should be 0
//                bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//                if (bool == false)
//                    Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//            }
//        }
//        //           no totalDeposit                            no  R2R3R4                                                      TOTALwages
//        //                                    2                   3                                   4
//        else if (totalDeposit == 0 &&( (rate2 == 0) || (rate2 == 0 && rate3 == 0 ) || (rate2 == 0 && rate3 == 0 && rate4 == 0) )&& totalWages != 0) {
//             binding.advanceOrBalanceTv.setText("= " + totalWages);
//            Toast.makeText(this, "5", Toast.LENGTH_SHORT).show();
//            //updating Advance to db
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + totalWages + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            //if advance   is there then update balance column should be 0 because both cant have value
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//
//            binding.advanceOrBalanceTv.setTextColor(Color.RED);
//
//        }
//        //         no totalDeposit   (                                      R2R3R4                                             )     no wages
//        //                                     2                   3                                        4
//        else if (totalDeposit == 0 &&( (rate2 != 0) || (rate2 != 0 && rate3 != 0 ) || (rate2 != 0 && rate3 != 0 && rate4 != 0) )&& totalWages == 0) {
//             //                                       (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            binding.workTotalAmountTv.setText("  - " + (totalr1r2r3r4sum1sum2sum3sum4));
//            Toast.makeText(this, "6", Toast.LENGTH_SHORT).show();
//            if((totalr1r2r3r4sum1sum2sum3sum4) < 0) {//user cant enter negative number so when (totalr1r2r3r4sum1sum2sum3sum4) is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//
//            //                                       (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            binding.advanceOrBalanceTv.setText("= " + (totalr1r2r3r4sum1sum2sum3sum4));
//
//            //updating balance to db                                                    (R1*SUMP1)+(R2*SUMP2)+(R3*SUMP3)+(R4*SUMP4)
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + (totalr1r2r3r4sum1sum2sum3sum4) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            //if balance there is balance then update advance column should be 0
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
//        }
//        //        totalDeposit       (                                              rate                                      )     no wages
//        //                                   2                     3                                  4
//        else if (totalDeposit != 0 &&((rate2 != 0) || (rate2 != 0 && rate3 != 0 ) || (rate2 != 0 && rate3 != 0 && rate4 != 0) )&& totalWages == 0) {
//             //                                           totalDeposit+                       (R1*SUMP1)
//            binding.workTotalAmountTv.setText("  - " + ((totalDeposit +  (totalr1r2r3r4sum1sum2sum3sum4))));
//            Toast.makeText(this, "7", Toast.LENGTH_SHORT).show();
//            if(((totalDeposit +  (totalr1r2r3r4sum1sum2sum3sum4))) < 0) {//user cant enter negative number so when ((totalDeposit +  (totalr1r2r3r4sum1sum2sum3sum4))) is negative that means int range is exceeds so wrong result will be shown
//                Toast.makeText(this, "INCORRECT CALCULATION PLEASE CHECK TOTAL WORK AMOUNT", Toast.LENGTH_LONG).show();
//            }
//
//
//            binding.advanceOrBalanceTv.setText("= " + ((totalDeposit +  (totalr1r2r3r4sum1sum2sum3sum4))));
//
//            //updating balance to db
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET BALANCE='" + (( totalDeposit + (totalr1r2r3r4sum1sum2sum3sum4))) + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "BALANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            //if balance there is balance then update advance column should be 0
//            bool = db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ADVANCE='" + 0 + "'" + "WHERE ID='" + fromIntentPersonId + "'");
//            if (bool == false)
//                Toast.makeText(this, "ADVANCE AMOUNT NOT UPDATED TO DATABASE", Toast.LENGTH_LONG).show();
//            binding.advanceOrBalanceTv.setTextColor(getColor(R.color.green));
//        }
    }
    private int get_indicator(String PersonId) {
         Cursor cursor=db.getData("SELECT INDICATOR FROM " + db.TABLE_NAME3 + " WHERE ID= '" + PersonId +"'");//for sure it will return type or skill
         if(cursor != null){
             cursor.moveToFirst();
                if(cursor.getString(0) == null) {
                     return 1;
                } else
                     return Integer.parseInt(cursor.getString(0));
         }else
             Toast.makeText(this, "NO DATA IN CURSOR", Toast.LENGTH_SHORT).show();

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
        TextView advanceOrBalanceWarring=myView.findViewById(R.id.advance_or_balance_amount_warring_tv);
        TextView noOfDaysToWork=myView.findViewById(R.id.no_of_days);
        TextView inputDate=myView.findViewById(R.id.input_date_tv);
        TextView inputTime=myView.findViewById(R.id.input_time_tv);
        TextView saveAudio=myView.findViewById(R.id.save_audio_tv);

        Chronometer playAudioChronometer =myView.findViewById(R.id.chronometer);

        EditText inputP1=myView.findViewById(R.id.input_p1_et);
        //to open keyboard automatically
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        TextView runtimeSuggestionAmountToGive=myView.findViewById(R.id.work_amount_tv);
        EditText inputP2=myView.findViewById(R.id.input_p2_et);
        EditText inputP3=myView.findViewById(R.id.input_p3_et);
        EditText inputP4=myView.findViewById(R.id.input_p4_et);
        EditText toGive_Amount=myView.findViewById(R.id.wages_et);
        EditText description=myView.findViewById(R.id.enter_description_et);
        Button save=myView.findViewById(R.id.save_btn);
        save.setVisibility(View.GONE);//initially save button is disabled it is enabled when user enter any data and its important otherwise app crash
        Button cancel=myView.findViewById(R.id.cancel_btn);

        //***********************setting no of days and warningTotaladvanceamount********************************************
        Cursor  advanceAmountCursor=db.getData("SELECT ADVANCE,BALANCE FROM " + db.TABLE_NAME1 + " WHERE ID= '" + fromIntentPersonId +"'");
        advanceAmountCursor.moveToFirst();
        if(advanceAmountCursor.getInt(0) > 0) {//advance
            advanceOrBalanceWarring.setTextColor(Color.RED);
            advanceOrBalanceWarring.setText(advanceAmountCursor.getString(0));

            Cursor sum1DayAmountCursor=db.getData("SELECT  R1,R2,R3,R4 FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
                   sum1DayAmountCursor.moveToFirst();
            int howManyPerson=get_indicator(fromIntentPersonId) ;
             if(howManyPerson==1) {
                 if(sum1DayAmountCursor.getInt(0) !=0) {//to avoid arithmetic exception 1/0
                                                                   //total advance / r1
                     noOfDaysToWork.setText("" + (advanceAmountCursor.getInt(0) / sum1DayAmountCursor.getInt(0)));
                 }
             }else if(howManyPerson==2){//to avoid arithmetic exception 1/0
                 if(sum1DayAmountCursor.getInt(0)+sum1DayAmountCursor.getInt(1) != 0) {
                           //                                         total advance/(R1+R1)
                     noOfDaysToWork.setText("" + (advanceAmountCursor.getInt(0) / (sum1DayAmountCursor.getInt(0) + sum1DayAmountCursor.getInt(1))));
                 }
             }else if(howManyPerson==3){//to avoid arithmetic exception 1/0
                 if(sum1DayAmountCursor.getInt(0)+sum1DayAmountCursor.getInt(1)+sum1DayAmountCursor.getInt(2) != 0) {
                     //                                               total advance/(R1+R2+R3)
                     noOfDaysToWork.setText("" + (advanceAmountCursor.getInt(0) / (sum1DayAmountCursor.getInt(0) + sum1DayAmountCursor.getInt(1) + sum1DayAmountCursor.getInt(2))));
                 }
             }else if(howManyPerson==4){//to avoid arithmetic exception 1/0
                 if(sum1DayAmountCursor.getInt(0)+sum1DayAmountCursor.getInt(1)+sum1DayAmountCursor.getInt(2)+sum1DayAmountCursor.getInt(3) != 0) {
                     //                                                total advance/(R1+R2+R3+R4)
                     noOfDaysToWork.setText("" + (advanceAmountCursor.getInt(0) / (sum1DayAmountCursor.getInt(0) + sum1DayAmountCursor.getInt(1) + sum1DayAmountCursor.getInt(2) + sum1DayAmountCursor.getInt(3))));
                 }
             }
            sum1DayAmountCursor.close();
        }else if(advanceAmountCursor.getInt(1) >= 0 ){//balance
            advanceOrBalanceWarring.setTextColor(getColor(R.color.green));
            advanceOrBalanceWarring.setText(advanceAmountCursor.getString(1));
        }
        advanceAmountCursor.close();
        //***********************done setting no of days and warringTotaladvanceamount********************************************

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
        //int cYear=current.get(Calendar.YEAR);
       // int cMonth=current.get(Calendar.MONTH);
       // int cDayOfMonth=current.get(Calendar.DAY_OF_MONTH);
        inputDate.setText(current.get(Calendar.DAY_OF_MONTH)+"-"+(current.get(Calendar.MONTH)+1)+"-"+current.get(Calendar.YEAR));
        dateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To show calendar dialog
                DatePickerDialog datePickerDialog=new DatePickerDialog(IndividualPersonDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        inputDate.setText(dayOfMonth+"-"+(month+1)+"-"+year);//month start from 0 so 1 is added to get right month like 12
                    }
                },current.get(Calendar.YEAR),current.get(Calendar.MONTH),current.get(Calendar.DAY_OF_MONTH));//This variable should be ordered this variable will set date day month to calendar to datePickerDialog so passing it
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

                //this will store latest date in db if that date is current date because if we store directly current date then it will give incorrect information about whether data is entered today or other days
                final Calendar current=Calendar.getInstance();//to get current date
                String currentDate =current.get(Calendar.DAY_OF_MONTH)+"-"+(current.get(Calendar.MONTH)+1)+"-"+current.get(Calendar.YEAR);
                if(date.equals(currentDate)) {//if it is true then store
                    db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET  LATESTDATE='" + date + "'" +" WHERE ID='" + fromIntentPersonId + "'");
                }

                //To get exact time so write code in save button
                Date d=Calendar.getInstance().getTime();
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss a");//a stands for is AM or PM
                String onlyTime = sdf.format(d);
                inputTime.setText(onlyTime);//setting time to take time and store in db
                String time=inputTime.getText().toString();//time will be inserted automatically

                if(file !=null){//if file is not null then only it execute otherwise nothing will be inserted
                    micPath=file.getAbsolutePath();
                    arr[5]=1;
                 }
                else
                    arr[5]=0;

                if(description.getText().toString().length() >=1){//to prevent nullpointer exception
                    remarks="["+time+"]-[ENTERED]\n\n"+description.getText().toString().trim();//time is set automatically to remarks if user enter any remarks
                    arr[6]=1;
                }
                else
                    arr[6]=0;
                boolean success, isWrongData, isDataPresent;
                  isWrongData= isEnterDataIsWrong(arr);
                  isDataPresent= isDataPresent(arr);
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
                 db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + 1 + "'" +" WHERE ID='" + fromIntentPersonId + "'");//when ever user insert data then that person will become active.It will work for all
                if(indicator==1) {
                    if (isDataPresent == true && isWrongData == false) {//it is important means if data is present then check is it right data or not.if condition is false then this message will be displayed "Correct the Data or Cancel and Enter again"
                        //insert to database
                          success = db.insert_1_Person_WithWagesTable2(fromIntentPersonId, date,time, micPath, remarks, wages, p1, "0");
                        if (success) {
                            displResult(wages + "          " + p1, "\nDATE: " + date + "\n\n" + "REMARKS: " + remarks + "\n\nMICPATH: " + micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO INSERT", Toast.LENGTH_LONG).show();
                    } else//once user enter wrong data and left blank then user wound be able to save because array value would not be change it will be 2 so  user have to "Cancel and enter again" if use dont leave blank then it will save successfully
                        Toast.makeText(IndividualPersonDetailActivity.this, "CORRECT THE DATA or CANCEL AND ENTER AGAIN", Toast.LENGTH_LONG).show();

                } else if(indicator==2){
                    //p1 is automatically added
                    if(isDataPresent==true && isWrongData==false ) {
                        if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                        }
                        //insert to database
                          success = db.insert_2_Person_WithWagesTable2(fromIntentPersonId, date,time, micPath, remarks, wages, p1, p2, "0");
                         if (success) {
                            displResult(wages+"          "+p1+"     "+p2,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO INSERT", Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "CORRECT THE DATA or CANCEL AND ENTER AGAIN", Toast.LENGTH_LONG).show();

                }else if(indicator==3){
                    if(isDataPresent==true && isWrongData==false ) {
                        if (inputP2.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p2 = Integer.parseInt(inputP2.getText().toString().trim());//converted to float and stored
                        }
                        if (inputP3.getText().toString().length() >= 1) {//to prevent nullpointer exception
                            p3 = Integer.parseInt(inputP3.getText().toString().trim());//converted to float and stored
                        }
                        //insert to database
                          success = db.insert_3_Person_WithWagesTable2(fromIntentPersonId, date,time, micPath, remarks, wages, p1, p2, p3, "0");
                         if (success) {
                            displResult(wages+"          "+p1+"     "+p2+"     "+p3,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO INSERT", Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "CORRECT THE DATA or CANCEL AND ENTER AGAIN", Toast.LENGTH_LONG).show();


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
                          success = db.insert_4_Person_WithWagesTable2(fromIntentPersonId, date,time, micPath, remarks, wages, p1, p2, p3, p4, "0");
                        if (success) {
                            displResult(wages+"          "+p1+"     "+p2+"     "+p3+"     "+p4,"\nDATE: "+date+"\n\n"+"REMARKS: "+remarks+"\n\nMICPATH: "+micPath);
                            dialog.dismiss();//dialog will be dismiss after saved automatically
                        } else
                            Toast.makeText(IndividualPersonDetailActivity.this, "FAILED TO INSERT", Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(IndividualPersonDetailActivity.this, "CORRECT THE DATA or CANCEL AND ENTER AGAIN", Toast.LENGTH_LONG).show();
                }
            }
        });
        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking for permission
                if(checkPermissionForAudio()){
                    if (mStartRecording) {//initially false
                        //while recording user should not perform other task like entering date while recording because app will crash so set all field to setEnabled(false);
                        inputP1.setEnabled(false);
                        inputP2.setEnabled(false);
                        inputP3.setEnabled(false);
                        inputP4.setEnabled(false);
                        toGive_Amount.setEnabled(false);
                        description.setEnabled(false);
                        dateIcon.setEnabled(false);
                        save.setVisibility(View.GONE);
                        cancel.setEnabled(false);
                        deposit_btn_tv.setEnabled(false);

                        playAudioChronometer.setBase(SystemClock.elapsedRealtime());//In Android, Chronometer is a class that implements a simple timer. Chronometer is a subclass of TextView. This class helps us to add a timer in our app.
                        playAudioChronometer.start();
                        playAudioChronometer.setEnabled(false);//when user press save button then set to true playAudioChronometer.setEnabled(true);
                        saveAudio.setBackgroundResource(R.drawable.ic_green_sharp_done_sharp_tick_20);//changing tick color to green so that user can feel to press to save
                        micIcon.setEnabled(false);
                        micIcon.setBackgroundResource(R.drawable.black_sharp_mic_24);//change color when user click

                        Toast.makeText(IndividualPersonDetailActivity.this, "RECORDING STARTED", Toast.LENGTH_SHORT).show();

                        //be carefull take only getExternalFilesDir( null ) https://stackoverflow.com/questions/59017202/mediarecorder-stop-failed
                        File folder = new File(getExternalFilesDir(null) + "/acBookMicRecording");//Creating File directory in phone

                        if (!folder.exists()) {//if folder not exist
                            Toast.makeText(IndividualPersonDetailActivity.this, "Creating acBookMicRecording folder to store audios", Toast.LENGTH_LONG).show();
                            folder.mkdir();//create folder
                        }

                        startRecordingVoice();
                        IndividualPersonDetailActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //while the user is recording screen should be on. it should not close

                    } else {//if recording is not started then stop
                        Toast.makeText(IndividualPersonDetailActivity.this, "AGAIN TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
                    }
                    mStartRecording = !mStartRecording;//so that user should click 2 times to start recording

                }else {//request for permission
                    Toast.makeText(IndividualPersonDetailActivity.this, "AUDIO PERMISSION REQUIRED", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(IndividualPersonDetailActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
                }
            }
        });
        playAudioChronometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file != null) {//checking for null pointer Exception
                    Toast.makeText(IndividualPersonDetailActivity.this, "AUDIO PLAYING", Toast.LENGTH_SHORT).show();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(file.getAbsolutePath());//passing the path where this audio is saved
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(IndividualPersonDetailActivity.this, "AUDIO PLAYING", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(IndividualPersonDetailActivity.this, "TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
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

                    if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                        save.setVisibility(View.VISIBLE);
                     }

                    cancel.setEnabled(true);
                    deposit_btn_tv.setEnabled(true);

                    playAudioChronometer.setTextColor(getColor(R.color.green));//changind text color to green to give feel that is saved
                    micIcon.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);//set background image to cancel
                    stopAndSaveRecordingPathToDB();
                    playAudioChronometer.stop();//stopping chronometer
                    micIcon.setEnabled(false);//so that user cannot press again this button
                    saveAudio.setEnabled(false);//even this button user should not click again
                    playAudioChronometer.setEnabled(true);//when audio is save then user will be able to play
                }else
                    Toast.makeText(IndividualPersonDetailActivity.this, "TAB ON MIC TO START RECORDING", Toast.LENGTH_SHORT).show();
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
                arr[4]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data

                //this will check if other data is right or wrong
                if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                    save.setVisibility(View.VISIBLE);
                 }

                if(!amount.matches("[0-9]+")){//no space or . or ,
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPlease Correct", Toast.LENGTH_LONG).show();
                    toGive_Amount.setTextColor(Color.RED);
                    save.setVisibility(View.GONE);
                    arr[4]=2;//means wrong data
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        inputP1.addTextChangedListener(new TextWatcher() {
            Cursor result =db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP1.getText().toString().trim();
                inputP1.setTextColor(Color.BLACK);
                arr[0]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data

                //this will check if other data is right or wrong
                if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                    save.setVisibility(View.VISIBLE);
                }
                if(!p11.matches("[0-9]+")){//"[.]?[0-9]+[.]?[0-9]*" for float
                    inputP1.setTextColor(Color.RED);
                    save.setVisibility(View.GONE);
                    arr[0]=2;//means wrong data
                     Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {//after text changed for suggestion calculate based on prevoius rate
                result.moveToFirst();
                p1_p2_p3_p4_Change_Tracker(result,inputP1,inputP2,inputP3,inputP4,runtimeSuggestionAmountToGive);
            }
        });
        inputP2.addTextChangedListener(new TextWatcher() {
            Cursor result=db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP2.getText().toString().trim();
                inputP2.setTextColor(Color.BLACK);
                arr[1]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data

                //this will check if other data is right or wrong
                if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                    save.setVisibility(View.VISIBLE);
                }

                if(!p11.matches("[0-9]+")){// "[.]?[0-9]+[.]?[0-9]*"
                    inputP2.setTextColor(Color.RED);
                    save.setVisibility(View.GONE);
                    arr[1]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                result.moveToFirst();
                p1_p2_p3_p4_Change_Tracker(result,inputP1,inputP2,inputP3,inputP4,runtimeSuggestionAmountToGive);
            }
        });
        inputP3.addTextChangedListener(new TextWatcher() {
            Cursor result=db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP3.getText().toString().trim();
                inputP3.setTextColor(Color.BLACK);
                arr[2]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data


                //this will check if other data is right or wrong
                if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                    save.setVisibility(View.VISIBLE);
                }

                if(!p11.matches("[0-9]+")){//space or , or - is restricted
                    inputP3.setTextColor(Color.RED);
                    save.setVisibility(View.GONE);
                    arr[2]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                result.moveToFirst();
                p1_p2_p3_p4_Change_Tracker(result,inputP1,inputP2,inputP3,inputP4,runtimeSuggestionAmountToGive);
            }
        });
        inputP4.addTextChangedListener(new TextWatcher() {
            Cursor result=db.getData("SELECT  R1,R2,R3,R4  FROM " + db.TABLE_NAME3 + " WHERE ID= '" + fromIntentPersonId +"'");
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String p11= inputP4.getText().toString().trim();
                inputP4.setTextColor(Color.BLACK);
                arr[3]=1;//means data is inserted.This line should be here because when user enter wrong data and again enter right data then it should update array to 1 which indicate write data
                //this will check if other data is right or wrong
                if(!isEnterDataIsWrong(arr)) {//this is important if in field data is wrong then save button will not enabled until data is right.if save button is enabled with wrong data then if user has record audio then it will not be saved it will store null so to check right or wrong data this condition is important
                    save.setVisibility(View.VISIBLE);
                }
                if(!p11.matches("[0-9]+")){//space or , or - is restricted
                    inputP4.setTextColor(Color.RED);
                    save.setVisibility(View.GONE);
                    arr[3]=2;//means wrong data
                    Toast.makeText(IndividualPersonDetailActivity.this, "NOT ALLOWED(space  .  ,  -)\nPLEASE CORRECT", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                result.moveToFirst();
                p1_p2_p3_p4_Change_Tracker(result,inputP1,inputP2,inputP3,inputP4,runtimeSuggestionAmountToGive);
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
    private boolean isEnterDataIsWrong(int[] arr) {
        boolean bool;
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
    private boolean isDataPresent(int[] arr){
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
    private boolean checkPermissionForAudio() {//checking for permission of mic and external storage
        if((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
            return true;
        }else
            return false;
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