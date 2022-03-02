package amar.das.acbook.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.model.WagesDetailsModel;

public class WagesDetailsAdapter extends RecyclerView.Adapter<WagesDetailsAdapter.ViewHolder> {
    Context context;
    ArrayList<WagesDetailsModel> arrayList;
    PersonRecordDatabase db;
    int indicator;
    Boolean bool;
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

       } else if( data.getWages() == 0 && data.getDeposit() != 0 && data.getP1() == 0){//if wages is not there and deposit there then set wages and color to green
           holder.wages.setText(""+data.getDeposit());//while entering deposit then there will be no p1 or p2p3p4 so checking data.getP1() == 0
           holder.wages.setTextColor(Color.GREEN);

       }else/**if we dont put else statement then default value will be set*/
           holder.wages.setText("");

       //*************************************Audio and mic*********************************************************
        if((data.getDescription() != null) || data.getMicPath() !=null) {//if audio or description is present then set min icon to green
           holder.spinnerdescAudioIcon.setBackgroundResource(R.drawable.ic_green_sharp_mic_20);
           bool=true;//means data is present
       }
       else {
            holder.spinnerdescAudioIcon.setBackgroundResource(R.drawable.black_sharp_mic_24);
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
                    //after selecting second time remarks data is not shown so initialposition-2 is set so that when second time click it will show data
                int initialposition = holder.spinnerdescAudioIcon.getSelectedItemPosition();
                holder.spinnerdescAudioIcon.setSelection(initialposition-2, false);//clearing auto selected

                }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
       //*************************************done audio and mic*********************************************************
        indicator=get_indicator(data.getId());  //showing data of p1 p2p3p4 if dtat is present
        holder.p1.setText("");//initially all will be blank
        holder.p2.setText("");
        holder.p3.setText("");
        holder.p4.setText("");
        if(data.getP1() != 0)//default skill will be common to all
            holder.p1.setText("" + data.getP1());

         if(indicator==2){
            if(data.getP2() != 0)
                holder.p2.setText(""+data.getP2());
            
        }else if(indicator==3){
            if(data.getP2() != 0)
                holder.p2.setText(""+data.getP2());

            if(data.getP3() != 0)
                holder.p3.setText(""+data.getP3());

        }else if(indicator==4){
            if(data.getP2() != 0)
                holder.p2.setText(""+data.getP2());

            if(data.getP3() != 0)
                holder.p3.setText(""+data.getP3());

            if(data.getP4() != 0)
                holder.p4.setText(""+data.getP4());
        }
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,wages,p1,p2,p3,p4;
        Spinner spinnerdescAudioIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date_in_recycler_tv);
            wages=itemView.findViewById(R.id.wages_in_recycler_tv);
            p1=itemView.findViewById(R.id.p1_in_recycler_tv);
            p2=itemView.findViewById(R.id.p2_in_recycler_tv);
            p3=itemView.findViewById(R.id.p3_in_recycler_tv);
            p4=itemView.findViewById(R.id.p4_in_recycler_tv);
            spinnerdescAudioIcon =itemView.findViewById(R.id.spinner_in_recycler_tv);
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
}
