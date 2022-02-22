package amar.das.acbook.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

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