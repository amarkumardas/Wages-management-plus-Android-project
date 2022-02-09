package amar.das.acbook.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.R;

public class MestreAdapter extends RecyclerView.Adapter<MestreAdapter.ViewHolder > {

    Context contex;
    ArrayList<MestreLaberGModel> arrayList;//because more operation is retrieving


    public MestreAdapter(Context context, ArrayList<MestreLaberGModel> arrayList){
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
        MestreLaberGModel model=arrayList.get(position);
        byte[] image=model.getPerson_img();
        //getting bytearray image from DB and converting  to bitmap to set in imageview
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
        holder.profileimg.setImageBitmap(bitmap);
        holder.amountAdvance.setText(model.getAdvanceAmount());

        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(contex.getApplicationContext(),"Click on ID"+model.getId(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{//this class will hold only references of view
        ImageView profileimg;
        TextView amountAdvance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileimg=itemView.findViewById(R.id.profile_img);
            amountAdvance =itemView.findViewById(R.id.advance_amount_tv);
        }
    }
}
