package amar.das.acbook.adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import amar.das.acbook.R;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.MestreLaberGModel;


public class InactiveMAdapter extends RecyclerView.Adapter<InactiveMAdapter.ViewHolder> {
    Context contex;
    ArrayList<MestreLaberGModel> arrayList;//because more operation is retrieving

    public InactiveMAdapter(Context context, ArrayList<MestreLaberGModel> arrayList){
        this.contex=context;
        this.arrayList=arrayList;
    }
    @NonNull
    @Override
    public InactiveMAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.inactive_single_person_profile,parent,false);
        return new InactiveMAdapter.ViewHolder(view);//constructor  public ViewHolder(@NonNull View itemView)
    }

    @Override
    public void onBindViewHolder(@NonNull InactiveMAdapter.ViewHolder holder, int position) {//to fill data on every view filed
        MestreLaberGModel data=arrayList.get(position);
        byte[] image=data.getPerson_img();//getting image ffrom db
        //getting bytearray image from DB and converting  to bitmap to set in imageview
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
        holder.profileimg.setImageBitmap(bitmap);
        if(data.getAdvanceAmount() > 0 ){//no need to give >= because wastage of time
            holder.amountAdvance.setText(""+data.getAdvanceAmount());
            holder.amountAdvance.setTextColor(Color.RED);
        }else if(data.getBalanceAmount() > 0 ){
            holder.amountAdvance.setText(""+data.getBalanceAmount());
            holder.amountAdvance.setTextColor(contex.getResources().getColor(R.color.green));
        }else {
            holder.amountAdvance.setText("0");//if no advance or balance then set to zero
            holder.amountAdvance.setTextColor(contex.getResources().getColor(R.color.green));
        }

        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(contex, IndividualPersonDetailActivity.class);
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
        TextView amountAdvance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileimg=itemView.findViewById(R.id.profile_img_inactive);
            amountAdvance =itemView.findViewById(R.id.advance_amount_tv_inactive);
        }
    }

}
