package amar.das.acbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.activity.IndividualPersonDetailActivity;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.R;

public class MestreLaberGAdapter extends RecyclerView.Adapter<MestreLaberGAdapter.ViewHolder > {
    //This adapter will decide which person is active or not.Person will become inactive if its leaving duration is 1 month.if user enter any data in current date then that person become active.
   //1.user has no permission to make it inactive it will be automatically inactivated
    //2.inactive is done based on latest date
    //3.whenever user enter or edit any data then if it is inactive then it will becomee active automatically except writing in meta data
    //4.Latest date is updated whenever user enter or edit any data  except writing in meta data and while adding rate
    //5.latest date is  USED to make active or inactive and also to make yellow or white background
    //6 when there is leaving date then only leaving date will be updated according to current date automatically
    //7 account become active when user account is created or user has entered data  except writing in meta data and while adding rate

    Context contex;
    ArrayList<MestreLaberGModel> arrayList;//because more operation is retrieving
    //final Calendar current=Calendar.getInstance();//to get current date
   // String currentDate =current.get(Calendar.DAY_OF_MONTH)+"-"+(current.get(Calendar.MONTH)+1)+"-"+current.get(Calendar.YEAR);
    String dateArray[];
    //int d,m,y;
    LocalDate dbDate, todayDate = LocalDate.now();//current date; return 2022-05-01
    String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
    PersonRecordDatabase db;

    public MestreLaberGAdapter(Context context, ArrayList<MestreLaberGModel> arrayList){
        this.contex=context;
        this.arrayList=arrayList;
         db=new PersonRecordDatabase(contex);//we cant give this at class level
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
        byte[] image=data.getPerson_img();//getting image from db
        //getting bytearray image from DB and converting  to bitmap to set in imageview
        Bitmap bitmap= BitmapFactory.decodeByteArray(image,0, image.length);
        holder.name.setText(data.getName());
        holder.profileimg.setImageBitmap(bitmap);
        if(data.getAdvanceAmount() > 0 ){//no need to give >= because wastage of time
            holder.amountAdvance.setText(""+data.getAdvanceAmount());
            holder.amountAdvance.setTextColor(Color.RED);
        }else if(data.getBalanceAmount() > 0 ){
            holder.amountAdvance.setText(""+data.getBalanceAmount());
            holder.amountAdvance.setTextColor(contex.getColor(R.color.green));
        }else {
            holder.amountAdvance.setText("0");//if no advance or balance then set to zero
            holder.amountAdvance.setTextColor(contex.getColor(R.color.green));
        }
          if(data.getLatestDate() !=null) {//for null pointer exception//https://www.youtube.com/watch?v=VmhcvoenUl0
              if (data.getLatestDate().equals(currentDateDBPattern)) //if profile color is yellow that means on current day some data is entered
                 holder.yellowBg.setBackgroundColor(contex.getColor(R.color.yellow));
             else
                  holder.yellowBg.setBackgroundColor(Color.WHITE);

              //if user is not active for 1 month then it will become inactive based on latest date
                dateArray = data.getLatestDate().split("-");
//                d = Integer.parseInt(dateArray[0]);
//                m = Integer.parseInt(dateArray[1]);
//                y = Integer.parseInt(dateArray[2]);
                dbDate = LocalDate.of(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[1]),Integer.parseInt(dateArray[0]));//it convert 2022-05-01 it add 0 automatically
              // making it active or inactive using latest date (2022-05-01,2022-05-01)
              if(ChronoUnit.MONTHS.between(dbDate, todayDate) >= 1) { //ChronoUnit.MONTHS it give total months.here dbDate is first and dbDate will always be lower then today date even if we miss to open app for long days
                  db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + 0 + "'" + " WHERE ID='" + data.getId() + "'");//user has no permission to make it inactive it is automatically

              }else {
                  db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + 1 + "'" + " WHERE ID='" + data.getId() + "'");

              }
          }else
              holder.yellowBg.setBackgroundColor(Color.WHITE);

        holder.profileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(contex,IndividualPersonDetailActivity.class);
                intent.putExtra("ID",data.getId());
                contex.startActivity(intent);

                Cursor cursor5 = db.getData("SELECT LATESTDATE FROM " + db.TABLE_NAME1 + " WHERE ID='" + data.getId() + "'");
                cursor5.moveToFirst();
                Toast.makeText(contex, "showLatestdate"+cursor5.getString(0), Toast.LENGTH_SHORT).show();

                //************************leaving date updation if days is 0 between two date then update SET LEAVINGDATE="+null+
                Cursor cursor2 = db.getData("SELECT LEAVINGDATE FROM " + db.TABLE_NAME3 + " WHERE ID='" + data.getId() + "'");
                cursor2.moveToFirst();
                if(cursor2.getString(0) != null){
                    dateArray = cursor2.getString(0).split("-");
//                    d = Integer.parseInt(dateArray[0]);
//                    m = Integer.parseInt(dateArray[1]);
//                    y = Integer.parseInt(dateArray[2]);//dbDate is leaving date
                    dbDate = LocalDate.of(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]));//it convert 2022-05-01it add 0 automatically
                                    //between (2022-05-01,2022-05-01) like
                    if(ChronoUnit.DAYS.between(dbDate,todayDate) >= 0){//if days between leaving date and today date is 0 then leaving date will set null automatically
                        db.updateTable("UPDATE " + db.TABLE_NAME3 + " SET LEAVINGDATE="+null+" WHERE ID='" + data.getId() + "'");
                    }
                }
                cursor2 = db.getData("SELECT LEAVINGDATE FROM " + db.TABLE_NAME3 + " WHERE ID='" + data.getId() + "'");
                cursor2.moveToFirst();
                if(cursor2.getString(0) != null){//https://www.youtube.com/watch?v=VmhcvoenUl0
                    dateArray = cursor2.getString(0).split("-");
//                    d = Integer.parseInt(dateArray[0]);
//                    m = Integer.parseInt(dateArray[1]);
//                    y = Integer.parseInt(dateArray[2]);//dbDate is leaving date
                    dbDate = LocalDate.of(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[1]),Integer.parseInt(dateArray[0]));//it convert 2022-05-01 it add 0 automatically
                                                                //between (2022-05-01,2022-05-01) like
                    Toast.makeText(contex, ""+ChronoUnit.DAYS.between(todayDate,dbDate)+" DAYS LEFT TO LEAVE", Toast.LENGTH_SHORT).show();//HERE dbDate will always be higher then todayDate because user will leave in forward date so in method chronounit todayDate is written first and second dbDate to get right days
                }
                cursor2.close();
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
        LinearLayout yellowBg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.names_tv);
            profileimg=itemView.findViewById(R.id.profile_img);
            amountAdvance =itemView.findViewById(R.id.advance_amount_tv);
            yellowBg =itemView.findViewById(R.id.yellow_layout);
        }
    }
}
