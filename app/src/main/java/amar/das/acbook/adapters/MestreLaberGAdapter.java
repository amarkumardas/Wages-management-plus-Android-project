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

public class MestreLaberGAdapter extends RecyclerView.Adapter<MestreLaberGAdapter.ViewHolder> {

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

        holder.profileimg.setOnClickListener(view -> {
            Intent intent = new Intent(contex, IndividualPersonDetailActivity.class);
            intent.putExtra("ID", data.getId());
            intent.putExtra("FromMesterLaberGAdapter", 1);
            contex.startActivity(intent);


            Cursor cursor5 = db.getData("SELECT LATESTDATE FROM " + db.TABLE_NAME1 + " WHERE ID='" + data.getId() + "'");
            cursor5.moveToFirst();
            Toast.makeText(contex, "showLatestdate" + cursor5.getString(0), Toast.LENGTH_SHORT).show();

            //************************leaving date updation if days is 0 between two date then update SET LEAVINGDATE="+null+
            Cursor cursor2 = db.getData("SELECT LEAVINGDATE FROM " + db.TABLE_NAME3 + " WHERE ID='" + data.getId() + "'");
            cursor2.moveToFirst();
            if (cursor2.getString(0) != null) {
                dateArray = cursor2.getString(0).split("-");
//                    d = Integer.parseInt(dateArray[0]);
//                    m = Integer.parseInt(dateArray[1]);
//                    y = Integer.parseInt(dateArray[2]);//dbDate is leaving date
                dbDate = LocalDate.of(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]));//it convert 2022-05-01it add 0 automatically
                //between (2022-05-01,2022-05-01) like
                if (ChronoUnit.DAYS.between(dbDate, todayDate) >= 0) {//if days between leaving date and today date is 0 then leaving date will set null automatically
                    db.updateTable("UPDATE " + db.TABLE_NAME3 + " SET LEAVINGDATE=" + null + " WHERE ID='" + data.getId() + "'");
                }
            }
            cursor2.close();
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



    //***************For setting or showing different layout to recycler view***********************************************************************************
//    //This adapter will decide which person is active or not.Person will become inactive if its leaving duration is 1 month.if user enter any data in current date then that person become active.
//    Context contex;
//    ArrayList<MestreLaberGModel> arrayList;//because more operation is retrieving
//    //final Calendar current=Calendar.getInstance();//to get current date
//    // String currentDate =current.get(Calendar.DAY_OF_MONTH)+"-"+(current.get(Calendar.MONTH)+1)+"-"+current.get(Calendar.YEAR);
//    String dateArray[];
//    //int d,m,y;
//    LocalDate dbDate, todayDate = LocalDate.now();//current date; return 2022-05-01
//    String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
//    PersonRecordDatabase db;
//
//    private static final int ITEM_VIEW=0;//TO DECIDE which view to show to user
//    private static final int ITEM_LOADING_VIEW=1;//TO DECIDE which view to show to user
//    public static boolean isLoading=true;
////    private OnLoadMore mOnLoadMore;
////    private boolean isLoading;
//
////    public void setOnLoadMore(OnLoadMore onLoadMore1){
////       mOnLoadMore=onLoadMore1;
////    }
////    public void setIsLoading(boolean param){
////        isLoading=param;
////    }
//
//    public MestreLaberGAdapter(Context context, ArrayList<MestreLaberGModel> arrayList,RecyclerView recyclerView){
//        this.contex=context;
//        this.arrayList=arrayList;
//        db=new PersonRecordDatabase(contex);//we cant give this at class level
//
//
//
//    }
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
////        if(viewType==ITEM_VIEW) {
////              view = inflater.inflate(R.layout.single_person_profile, parent, false);
////            return new ViewHolder(view);//constructor  public ViewHolder(@NonNull View itemView)
////        }else if(viewType==ITEM_LOADING_VIEW){
////            view = inflater.inflate(R.layout.single_person_profile, parent, false);
////            return new ViewHolder(view);//constructor  public ViewHolder(@NonNull View itemView)
////        }
//        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
//        View view=null;
//        switch (viewType) {//return specific view based on viewType
//            case ITEM_VIEW: view = inflater.inflate(R.layout.single_person_profile, parent, false);
//                break;
//            case ITEM_LOADING_VIEW: {
//                view = inflater.inflate(R.layout.progress_bar_for_all, parent, false);
//                return new ProgressbarViewHoder(view);
//            }
//        }
//        return new ViewHolder(view);//constructor  public ViewHolder(@NonNull View itemView)
//    }
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder hold, int position) {//to fill data on every view filed
//
//        MestreLaberGModel data = arrayList.get(position);
//
//        switch (getItemViewType(position)) {
//            case ITEM_VIEW: {
//                ViewHolder holder = (ViewHolder) hold;//typecasting
//
//                byte[] image = data.getPerson_img();//getting image from db
//                //getting bytearray image from DB and converting  to bitmap to set in imageview
//                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//                holder.name.setText(data.getName());
//                holder.profileimg.setImageBitmap(bitmap);
//                if (data.getAdvanceAmount() > 0) {//no need to give >= because wastage of time
//                    holder.amountAdvance.setText("" + data.getAdvanceAmount());
//                    holder.amountAdvance.setTextColor(Color.RED);
//                } else if (data.getBalanceAmount() > 0) {
//                    holder.amountAdvance.setText("" + data.getBalanceAmount());
//                    holder.amountAdvance.setTextColor(contex.getColor(R.color.green));
//                } else {
//                    holder.amountAdvance.setText("0");//if no advance or balance then set to zero
//                    holder.amountAdvance.setTextColor(contex.getColor(R.color.green));
//                }
//                if (data.getLatestDate() != null) {//for null pointer exception//https://www.youtube.com/watch?v=VmhcvoenUl0
//                    if (data.getLatestDate().equals(currentDateDBPattern)) //if profile color is yellow that means on current day some data is entered
//                        holder.yellowBg.setBackgroundColor(contex.getColor(R.color.yellow));
//                    else
//                        holder.yellowBg.setBackgroundColor(Color.WHITE);
//
//                    //if user is not active for 1 month then it will become inactive based on latest date
//                    dateArray = data.getLatestDate().split("-");
////                d = Integer.parseInt(dateArray[0]);
////                m = Integer.parseInt(dateArray[1]);
////                y = Integer.parseInt(dateArray[2]);
//                    dbDate = LocalDate.of(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]));//it convert 2022-05-01 it add 0 automatically
//                    // making it active or inactive using latest date (2022-05-01,2022-05-01)
//                    if (ChronoUnit.MONTHS.between(dbDate, todayDate) >= 1) { //ChronoUnit.MONTHS it give total months.here dbDate is first and dbDate will always be lower then today date even if we miss to open app for long days
//                        db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + 0 + "'" + " WHERE ID='" + data.getId() + "'");//user has no permission to make it inactive it is automatically
//
//                    } else {
//                        db.updateTable("UPDATE " + db.TABLE_NAME1 + " SET ACTIVE='" + 1 + "'" + " WHERE ID='" + data.getId() + "'");
//
//                    }
//                } else
//                    holder.yellowBg.setBackgroundColor(Color.WHITE);
//
//                holder.profileimg.setOnClickListener(view -> {
//                    Intent intent = new Intent(contex, IndividualPersonDetailActivity.class);
//                    intent.putExtra("ID", data.getId());
//                    intent.putExtra("FromMesterLaberGAdapter", 1);
//                    contex.startActivity(intent);
//
//
//                    Cursor cursor5 = db.getData("SELECT LATESTDATE FROM " + db.TABLE_NAME1 + " WHERE ID='" + data.getId() + "'");
//                    cursor5.moveToFirst();
//                    Toast.makeText(contex, "showLatestdate" + cursor5.getString(0), Toast.LENGTH_SHORT).show();
//
//                    //************************leaving date updation if days is 0 between two date then update SET LEAVINGDATE="+null+
//                    Cursor cursor2 = db.getData("SELECT LEAVINGDATE FROM " + db.TABLE_NAME3 + " WHERE ID='" + data.getId() + "'");
//                    cursor2.moveToFirst();
//                    if (cursor2.getString(0) != null) {
//                        dateArray = cursor2.getString(0).split("-");
////                    d = Integer.parseInt(dateArray[0]);
////                    m = Integer.parseInt(dateArray[1]);
////                    y = Integer.parseInt(dateArray[2]);//dbDate is leaving date
//                        dbDate = LocalDate.of(Integer.parseInt(dateArray[2]), Integer.parseInt(dateArray[1]), Integer.parseInt(dateArray[0]));//it convert 2022-05-01it add 0 automatically
//                        //between (2022-05-01,2022-05-01) like
//                        if (ChronoUnit.DAYS.between(dbDate, todayDate) >= 0) {//if days between leaving date and today date is 0 then leaving date will set null automatically
//                            db.updateTable("UPDATE " + db.TABLE_NAME3 + " SET LEAVINGDATE=" + null + " WHERE ID='" + data.getId() + "'");
//                        }
//                    }
//                    cursor2.close();
//                });
//                break;
//            }//case 1
//            case ITEM_LOADING_VIEW: {
//                ProgressbarViewHoder progressbarViewHold = (ProgressbarViewHoder) hold;//typecasting
//                if(isLoading==true) {
//                    System.out.println(isLoading);
//                    // isLoading=true;
//                    // progressbarViewHold.progressBar.setIndeterminate(true);
//                    progressbarViewHold.progressBar.setVisibility(View.VISIBLE);
//                }else {
//                    progressbarViewHold.progressBar.setVisibility(View.GONE);
//                    isLoading=false;
//                }
//                System.out.println(isLoading);
//                break;
//            }
//        }//switch
//    }
//    @Override
//    public int getItemCount() {
//        return arrayList.size();//if arraylist is empty then size() method return 0
//    }
//    @Override
//    public int getItemViewType(int position) {//Return the view type of the item at <code>position</code> for the purposes of view recycling.
//        return (position==arrayList.size()-1)? ITEM_LOADING_VIEW:ITEM_VIEW;//position start from 0.SO IT OVERRIDE one data so null is inserted at last of arraylist to avoid loosing 1 data
//    }
//    public class ViewHolder extends RecyclerView.ViewHolder{//this class will hold only references of view
//        ImageView profileimg;
//        TextView amountAdvance,name;
//        LinearLayout yellowBg;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            name=itemView.findViewById(R.id.names_tv);
//            profileimg=itemView.findViewById(R.id.profile_img);
//            amountAdvance =itemView.findViewById(R.id.advance_amount_tv);
//            yellowBg =itemView.findViewById(R.id.yellow_layout);
//        }
//    }
//    //viewholder for progressbar
//    public class ProgressbarViewHoder extends RecyclerView.ViewHolder{
//        ProgressBar progressBar;
//        public ProgressbarViewHoder(@NonNull View itemView) {
//            super(itemView);
//            progressBar=itemView.findViewById(R.id.progress_bar);
//        }
//    }


