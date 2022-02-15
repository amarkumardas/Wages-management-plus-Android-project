package amar.das.acbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.R;

public class MestreLaberGAdapter extends RecyclerView.Adapter<MestreLaberGAdapter.ViewHolder > {

    Context contex;
    ArrayList<MestreLaberGModel> arrayList;//because more operation is retrieving


    public MestreLaberGAdapter(Context context, ArrayList<MestreLaberGModel> arrayList){
        this.contex=context;
        this.arrayList=arrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.single_person_profile,parent,false);
        return new ViewHolder(view);//constructor  public ViewHolder(@NonNull View itemView)
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//to fill data on every view filed
        MestreLaberGModel data=arrayList.get(position);
        byte[] image=data.getPerson_img();//getting image ffrom db
        //getting bytearray image from DB and converting  to bitmap to set in imageview
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
        holder.name.setText(data.getName());
        holder.profileimg.setImageBitmap(bitmap);
        holder.amountAdvance.setText(data.getAdvanceAmount());

        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(contex,IndividualPersonDetailActivity.class);
                intent.putExtra("ID",data.getId());
                contex.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{//this class will hold only references of view
        ImageView profileimg;
        TextView amountAdvance,name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.names_tv);
            profileimg=itemView.findViewById(R.id.profile_img);
            amountAdvance =itemView.findViewById(R.id.advance_amount_tv);
        }
    }
}
