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

import java.util.ArrayList;

import amar.das.acbook.R;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.MLGAllRecordModel;

public class AllMLGRecordAdapter extends RecyclerView.Adapter<AllMLGRecordAdapter.ViewHolder> {
    Context context;
    ArrayList<MLGAllRecordModel> arrayList;//because more operation is retrieving
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
        holder.amount.setText("AD: "+data.getAmount());
        if(data.getActive().equals("0")) { //if account is not active then view will be in red color which indicate inactive
            holder.name.setTextColor(Color.RED);
            holder.amount.setTextColor(Color.RED);
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
        TextView name,amount;

        LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name_tv);
            amount=itemView.findViewById(R.id.advance_money_tv);
            layout=itemView.findViewById(R.id.linear_layout);
        }
    }
}
