package amar.das.acbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import amar.das.acbook.R;
import amar.das.acbook.databinding.ActivityCustomizeLayoutOrDepositAmountBinding;
import amar.das.acbook.databinding.ActivityIndividualPersonDetailBinding;

public class CustomizeLayoutOrDepositAmount extends AppCompatActivity {
ActivityCustomizeLayoutOrDepositAmountBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0); //we have used overridePendingTransition(), it is used to remove activity create animation while re-creating activity.
        binding =ActivityCustomizeLayoutOrDepositAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String [] districts=getResources().getStringArray(R.array.districts);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(CustomizeLayoutOrDepositAmount.this, android.R.layout.select_dialog_item,districts);
        binding.customSpinnerSetting.setAdapter(adapter);

        //ids


        Spinner  spinner=findViewById(R.id.custom_spinner_setting);
        TextView micIcon=findViewById(R.id.custom_mic_tv);
        TextView dateIcon=findViewById(R.id.custom_date_icon_tv);
        TextView inputDate=findViewById(R.id.custom_date_tv);
        TextView saveAudio=findViewById(R.id.custom_save_audio_tv);

        Chronometer playAudioChronometer =findViewById(R.id.custom_chronometer);


        EditText deposit_amount=findViewById(R.id.custom_deposit_et);

        EditText description=findViewById(R.id.custom_description_et);
        Button save=findViewById(R.id.custom_save_btn);
        Button cancel=findViewById(R.id.custom_cancel_btn);



















        //https://stackoverflow.com/questions/5368225/spinner-item-gets-automatically-selected-upon-entering-activity-how-do-i-avoid
        //when activity is loaded spinner item is selected automatically so to avoid this we are using spinnerSetting.setSelection(initialposition, false);
//        int initialposition=spinnerSetting.getSelectedItemPosition();
//        spinnerSetting.setSelection(initialposition, false);//clearing auto selected item
//        spinnerSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//
//                String a=spinnerSetting.getItemAtPosition(pos).toString();
//                if(a.equals("ADD L")){//p2
//                    hardcodedP2.setText("L");
//                    inputP2.setVisibility(View.VISIBLE);
//                    hardcodedP3.setVisibility(View.GONE);
//                    inputP3.setVisibility(View.GONE);
//                    hardcodedP4.setVisibility(View.GONE);
//                    inputP4.setVisibility(View.GONE);
//                         Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//                } else if(a.equals("ADD M")){//p3
//                    hardcodedP3.setText("M");
//                    hardcodedP4.setVisibility(View.GONE);
//                    inputP4.setVisibility(View.GONE);
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//                }else if(a.equals("ADD G")){//p4
//                    hardcodedP4.setText("G");
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//                }else if(a.equals("DEPOSIT AMOUNT")){
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//                }else if(a.equals("REMOVE P1")){
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//
//                }else if(a.equals("REMOVE P2")){
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//
//                }else if(a.equals("REMOVE P3")){
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//
//                }else if(a.equals("REMOVE P4")){
//                    Toast.makeText(IndividualPersonDetailActivity.this, "dd"+a, Toast.LENGTH_SHORT).show();
//
//                }
//
//                dialog.dismiss();//after selection we have to close and show dialog otherwise human error may occur when they want to enter sequently LL or MM
//               // dialog.show();
//                spinnerSetting.setSelection(initialposition, false);//after show dialog clearing auto selected item
//
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(IndividualPersonDetailActivity.this, "no", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


}