package amar.das.acbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import amar.das.acbook.R;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.MLGAllRecordModel;

public class AllMLGRecordAdapter extends RecyclerView.Adapter<AllMLGRecordAdapter.ViewHolder> {

    Context context;
    ArrayList<MLGAllRecordModel> arrayList;//because more operation is retrieving
    //for date***********************
    String dateArray[]=new String[3];
    int d,m,y;
    LocalDate dbLatestDate, currentDate =LocalDate.now();

   //array lis has data name id and active
    public AllMLGRecordAdapter(Context context,ArrayList<MLGAllRecordModel> data){
        this.arrayList=data;
        this.context=context;
    }

    @NonNull
    @Override
    public AllMLGRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.l_m_g_singlerecord,parent,false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMLGRecordAdapter.ViewHolder holder, int position) {
        MLGAllRecordModel data=arrayList.get(position);
        holder.name.setText(Html.fromHtml("<b>"+data.getName()+"</b>"));
        holder.inactiveDuration.setText("ACTIVE");//when user is active then it show active
        if(data.getActive().equals("0")) { //if account is not active then view will be in red color which indicate inactive
            holder.name.setTextColor(Color.RED);
           //if they are not active then only it will show months
            if(data.getLatestDate() !=null) {//https://www.youtube.com/watch?v=VmhcvoenUl0
                dateArray = data.getLatestDate().split("-");
                d = Integer.parseInt(dateArray[0]);
                m = Integer.parseInt(dateArray[1]);
                y = Integer.parseInt(dateArray[2]);
                dbLatestDate = LocalDate.of(y, m, d);//it convert 01.05.2022 it add 0 automatically
                holder.inactiveDuration.setText(""+ChronoUnit.MONTHS.between(dbLatestDate, currentDate)+" MONTHS");
            }
            holder.inactiveDuration.setTextColor(Color.RED);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//WE have to send id
                Intent intent=new Intent(context, IndividualPersonDetailActivity.class);
                intent.putExtra("ID",data.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, inactiveDuration;
        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_tv);
            inactiveDuration =itemView.findViewById(R.id.inactive_duration_tv);
            layout=itemView.findViewById(R.id.linear_layout);
        }
    }
}
