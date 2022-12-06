package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


import amar.das.acbook.adapters.MestreLaberGAdapter;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.FragmentActiveMBinding;


public class ActiveMFragment extends Fragment {

    private FragmentActiveMBinding binding;
    ArrayList<MestreLaberGModel> mestreactiveArrayList;
    RecyclerView mestreRecyclerView;
    MestreLaberGAdapter mestreLaberGAdapter;
    TextView advance,balance;
    PersonRecordDatabase db;

    Boolean isScrolling1 =false;
    LinearLayoutManager layoutManager;
    int currentItem1, totalItem1, scrollOutItems1;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         binding=FragmentActiveMBinding.inflate(inflater,container,false);
         View root=binding.getRoot();
         db=new PersonRecordDatabase(getContext());
         //ids
         mestreRecyclerView=root.findViewById(R.id.recycle_active_mestre);
         progressBar=binding.progressBarActiveM;
         progressBar.setVisibility(View.GONE);//initially visibility will be not there only when data is loading then visibility set visible

         advance=root.findViewById(R.id.active_m_advance);
         balance=root.findViewById(R.id.active_m_balance);

         Cursor advanceBalanceCursor=db.getData("SELECT SUM(ADVANCE),SUM(BALANCE) FROM "+db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1'");
         advanceBalanceCursor.moveToFirst();
        advance.setText(HtmlCompat.fromHtml("ADVANCE- "+"<b>"+advanceBalanceCursor.getLong(0)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        balance.setText(HtmlCompat.fromHtml("BALANCE- "+"<b>"+advanceBalanceCursor.getLong(1)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
         advanceBalanceCursor.close();

        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022

         //mestre
        Cursor cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' ORDER BY LATESTDATE  LIMIT 900");//if lastestdate is null tyhe  te will top of record asc so that today data entered will be below and not entered data person will be up which will indicate that data is not entered

        mestreactiveArrayList =new ArrayList<>(60);//insuring initial capacity of arraylist
        while(cursormestre.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            data.setTime(cursormestre.getString(6));
            mestreactiveArrayList.add(data);//adding data to mestrearraylist
        }
        mestreactiveArrayList.trimToSize();//to release free space

        int nullCountInArraylist[]=countNullAndTodayLatestdate(mestreactiveArrayList);//it has null count and lastestdate count
         Collections.sort(mestreactiveArrayList.subList(nullCountInArraylist[0],mestreactiveArrayList.size()), new Comparator<MestreLaberGModel>() {
            DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            @Override
            public int compare(MestreLaberGModel obj1, MestreLaberGModel obj2) {//asc
                try {
                    return sdf.parse(obj1.getLatestDate()).compareTo(sdf.parse(obj2.getLatestDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        for (MestreLaberGModel s:mestreactiveArrayList) {
            System.out.println(s.getLatestDate());
        }
        int holdCount=0;
        cursormestre=db.getData("SELECT  COUNT(*) FROM " +db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE= '"+currentDateDBPattern+"'");//to get number of rows to decide sublist.The COUNT(*) function returns the number of rows in a table, including the rows including NULL and duplicates.
        if(cursormestre!=null) {
            cursormestre.moveToFirst();//since only 1 column so movetoFirst

            if(cursormestre.getInt(0) != 0) {
                            // if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
               // System.out.println(mestreactiveArrayList.size()-cursormestre.getInt(0)+"-"+mestreactiveArrayList.size());
               // Collections.sort(mestreactiveArrayList.subList(mestreactiveArrayList.size() - cursormestre.getInt(0), mestreactiveArrayList.size()), (obj1, obj2) -> {Integer.parseInt(obj2.getTime().replaceAll("[:am pm]",""))-Integer.parseInt(obj1.getTime().replaceAll("[:am pm]","")));//first making "00:59:30 PM" to 5930 .removing start 0 and :,AM,PM.PARSING TO INTEGER so that start 0 will remove.//sort data by taking time in desc order.index start from 0 n-1.this will keep todays time on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time
                Collections.sort(mestreactiveArrayList.subList(mestreactiveArrayList.size() - cursormestre.getInt(0),mestreactiveArrayList.size()), (obj1, obj2) -> {
                    Date obj1Date=null,obj2Date=null;
                    SimpleDateFormat format24hrs = new SimpleDateFormat("HH:mm:ss aa");//24 hrs format
                    SimpleDateFormat format12hrs = new SimpleDateFormat("hh:mm:ss aa");//12 hrs format
                    try {
                        obj1Date = format12hrs.parse(obj1.getTime());
                        obj2Date = format12hrs.parse(obj2.getTime());
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
//                    String obj1StringDate=format24hrs.format(obj1Date);
//                    String obj2StringDate=format24hrs.format(obj2Date);
                    return Integer.parseInt(format24hrs.format(obj2Date).replaceAll("[:]","").substring(0,6))-Integer.parseInt(format24hrs.format(obj1Date).replaceAll("[:]","").substring(0,6));
                });//first making "00:59:30 PM" to 5930 .removing start 0 and :,AM,PM.PARSING TO INTEGER so that start 0 will remove.//sort   time in desc order.index start from 0 n-1.this will keep todays time on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time


                holdCount=cursormestre.getInt(0);
               // System.out.println("sort last");

                cursormestre=db.getData("SELECT  COUNT(*) FROM " +db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NULL");//to get number of rows to decide sublist.The COUNT(*) function returns the number of rows in a table, including the rows including NULL and duplicates.
                if(cursormestre!=null) {
                    cursormestre.moveToFirst();//since only 1 column so movetoFirst
                    if(cursormestre.getInt(0) != 0) { //this will execute when new person is there and latest date is null
                        // if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
                      // System.out.println(cursormestre.getInt(0)+"-"+(mestreactiveArrayList.size()-holdCount));
                        Collections.sort(mestreactiveArrayList.subList(cursormestre.getInt(0), mestreactiveArrayList.size() - holdCount));//natural sorting based on latestdate desc
                       // System.out.println("middle");
//                     for (MestreLaberGModel n:mestreactiveArrayList.subList(cursormestre.getInt(0), mestreactiveArrayList.size() - holdCount)
//                             ) {
//                            System.out.println(n.getLatestDate()+"**************************");
//                        }


//                       Collections.sort(mestreactiveArrayList.subList(cursormestre.getInt(0), mestreactiveArrayList.size() - holdCount), (obj1, obj2) -> {
//                           DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//                           try {
//                               return f.parse(obj2.getLatestDate()).compareTo(f.parse(obj1.getLatestDate()));
//                           } catch (ParseException e) {
//                               throw new IllegalArgumentException(e);
//                           }
//                       });//sort data by taking latestdate in desc order.index start from 0 n-1.this will keep todays date on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays latestdate

                       //  System.out.println("sort middle");
                     }else{//this will execute when no new person is there and latest date is not null
//                        System.out.println( "0 -"+(mestreactiveArrayList.size()-holdCount));
//                        System.out.println("sort first"+holdCount);
                        Collections.sort(mestreactiveArrayList.subList(0, (mestreactiveArrayList.size() - holdCount)));//desc by taking latestdate
//                        for (MestreLaberGModel n:mestreactiveArrayList.subList(0, (mestreactiveArrayList.size() - holdCount))) {
//                            System.out.println(n.getLatestDate()+" "+n.getName()+"****************h**********");
//                        }


//                        Collections.sort(mestreactiveArrayList.subList(0, (mestreactiveArrayList.size() - holdCount)), (obj1, obj2) -> {
//                            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//                            try {
//                                return f.parse(obj2.getLatestDate()).compareTo(f.parse(obj1.getLatestDate()));
//                            } catch (ParseException e) {
//                                throw new IllegalArgumentException(e);
//                            }
//                        });//desc
//                        System.out.println("sort first"+holdCount);
//                        for (MestreLaberGModel i:mestreactiveArrayList.subList(0, mestreactiveArrayList.size() - holdCount)) {
//                            System.out.println(i.getLatestDate()+" "+i.getName());
//                       }
                    }
                }
            }else{
                //when ther is not latest date
            }
        }
        cursormestre.close();//closing cursor after finish
        db.close();//closing database to prevent dataleak
        mestreLaberGAdapter =new MestreLaberGAdapter(getContext(), mestreactiveArrayList);
        mestreRecyclerView.setAdapter(mestreLaberGAdapter);
        mestreRecyclerView.setHasFixedSize(true);//telling to recycler view that dont calculate item size every time when added and remove from recyclerview
        layoutManager=new GridLayoutManager(getContext(),4);//spancount is number of rows
        mestreRecyclerView.setLayoutManager(layoutManager);
        mestreRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) { //Callback method to be invoked when RecyclerView's scroll state changes.
                super.onScrollStateChanged(recyclerView, newState);
                //this will tell the state of scrolling if user is scrolling then isScrolling variable will become true
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling1 =true;//when user start to scroll then this varilable will be true
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {//Callback method to be invoked when the RecyclerView has been scrolled. This will be called after the scroll has completed.This callback will also be called if visible item range changes after a layout calculation. In that case, dx and dy will be 0.
                super.onScrolled(recyclerView, dx, dy);
                currentItem1 = layoutManager.getChildCount();
                totalItem1 = mestreLaberGAdapter.getItemCount();// totalItem=manager.getItemCount();
                scrollOutItems1 = layoutManager.findFirstVisibleItemPosition();
                // Toast.makeText(getContext(), "c= "+currentItem1+"o= "+scrollOutItems1+"t= "+totalItem1, Toast.LENGTH_SHORT).show();

                if(isScrolling1 && (currentItem1 + scrollOutItems1 == totalItem1)){
                    isScrolling1 =false;
                    progressBar.setVisibility(View.VISIBLE);//progressbar
                    Toast.makeText(getContext(), "PLEASE WAIT LOADING", Toast.LENGTH_SHORT).show();
                    fetchData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' ORDER BY LATESTDATE ",mestreactiveArrayList);
                    mestreRecyclerView.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last

                }
            }
        });
        return root;
    }

    private int[] countNullAndTodayLatestdate(ArrayList<MestreLaberGModel> al) {
        int arr[]=new int[2];
       // LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
       // String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
        for (int i = 0; i < al.size(); i++) {
            if(al.get(i).getLatestDate()== null){
                arr[0]++;//nullCount
            }
            else if(al.get(i).getLatestDate().equals(""+LocalDate.now().getDayOfMonth()+"-"+ LocalDate.now().getMonthValue()+"-"+ LocalDate.now().getYear())){
               arr[1]++;//todayLatestDateCount
            }
        }
        return arr;
    }


    private void fetchData(String query, ArrayList<MestreLaberGModel> arraylist) {
        new Handler().postDelayed(() -> {
            dataLoad(query,arraylist);
            progressBar.setVisibility(View.GONE);//after data loading progressbar disabled
         }, 1000);
    }

    private void dataLoad(String querys,ArrayList<MestreLaberGModel> arraylist){
        db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        Cursor cursormestre = db.getData(querys);//getting image from database
        arraylist.clear();//clearing the previous object which is there
        while (cursormestre.moveToNext()) {
            MestreLaberGModel data = new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            data.setTime(cursormestre.getString(6));
            arraylist.add(data);
            mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() when the list is updated,or inserted or deleted
        }
        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
                                                                                                                                            //converted to 1-5-2022
        cursormestre=db.getData("SELECT  COUNT(*) FROM " +db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE= '"+todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear()+"'");//to get number of rows to decide sublist.The COUNT(*) function returns the number of rows in a table, including the rows including NULL and duplicates.
        if(cursormestre!=null) {
            cursormestre.moveToFirst();//since only 1 column so movetoFirst
//            if(cursormestre.getInt(0) != 0) { //if 0 then no need to sort                                                                         //if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
//               // Collections.sort(arraylist.subList(0, arraylist.size() - cursormestre.getInt(0)));//index start from 0 n-1.this will keep todays date on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays date
//               // Collections.sort(arraylist.subList(arraylist.size() - cursormestre.getInt(0), arraylist.size()),(obj1,obj2)-> -obj1.getTime().compareTo(obj2.getTime()));//sort data by taking time in desc order.index start from 0 n-1.this will keep todays date on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time
//            }
            int holdCount=0;
            if(cursormestre.getInt(0) != 0) { //if 0 then no need to sort

                // if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
                System.out.println(arraylist.size()-cursormestre.getInt(0)+"-"+arraylist.size());
                Collections.sort(arraylist.subList(arraylist.size() - cursormestre.getInt(0),arraylist.size()), (obj1, obj2) -> {
                    Date obj1Date=null,obj2Date=null;
                    SimpleDateFormat format24hrs = new SimpleDateFormat("HH:mm:ss aa");//24 hrs format
                    SimpleDateFormat format12hrs = new SimpleDateFormat("hh:mm:ss aa");//12 hrs format
                    try {
                        obj1Date = format12hrs.parse(obj1.getTime());
                        obj2Date = format12hrs.parse(obj2.getTime());
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
//                    String obj1StringDate=format24hrs.format(obj1Date);
//                    String obj2StringDate=format24hrs.format(obj2Date);
                    return Integer.parseInt(format24hrs.format(obj2Date).replaceAll("[:]","").substring(0,6))-Integer.parseInt(format24hrs.format(obj1Date).replaceAll("[:]","").substring(0,6));
                });//first making "00:59:30 PM" to 5930 .removing start 0 and :,AM,PM.PARSING TO INTEGER so that start 0 will remove.//sort   time in desc order.index start from 0 n-1.this will keep todays time on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time


                holdCount=cursormestre.getInt(0);
               // System.out.println("sort last1");

                cursormestre=db.getData("SELECT  COUNT(*) FROM " +db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NULL");//to get number of rows to decide sublist.The COUNT(*) function returns the number of rows in a table, including the rows including NULL and duplicates.
                if(cursormestre!=null) {
                    cursormestre.moveToFirst();//since only 1 column so movetoFirst
                    if(cursormestre.getInt(0) != 0) { //this will execute when new person is added and latest date in null
                        // if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
                      // System.out.println(cursormestre.getInt(0)+"-"+(arraylist.size()-holdCount));
                        Collections.sort(arraylist.subList(cursormestre.getInt(0),arraylist.size()-holdCount));

//                        Collections.sort(arraylist.subList(cursormestre.getInt(0),arraylist.size()-holdCount),(obj1, obj2) -> {
//                            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//                            try {
//                                return f.parse(obj2.getLatestDate()).compareTo(f.parse(obj1.getLatestDate()));
//                            } catch (ParseException e) {
//                                throw new IllegalArgumentException(e);
//                            }
//                        });//sort data by taking latestdate in desc order.index start from 0 n-1.this will keep todays date on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays latestdate
//                        System.out.println("sort middle1");
//                        for (MestreLaberGModel i:arraylist.subList(cursormestre.getInt(0),arraylist.size()-holdCount)) {
//                            System.out.println(i.getLatestDate());
//                        }
                    }else{//this will execute when no new person is added and latest date is not null
                       // System.out.println( "0 -"+(arraylist.size()-holdCount));
                        Collections.sort(arraylist.subList(0,arraylist.size()-holdCount));
//                        Collections.sort(arraylist.subList(0,arraylist.size()-holdCount),(obj1, obj2) -> {
//                            DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//                            try {
//                                return f.parse(obj2.getLatestDate()).compareTo(f.parse(obj1.getLatestDate()));
//                            } catch (ParseException e) {
//                                throw new IllegalArgumentException(e);
//                            }
//                        });//desc
//                        System.out.println("sort first1");
//                        for (MestreLaberGModel i:arraylist.subList(0,arraylist.size()-holdCount)) {
//                            System.out.println(i.getLatestDate());
//                        }
                    }
                }
            }
        }

        mestreLaberGAdapter.notifyDataSetChanged();
        arraylist.trimToSize();//to release free space


        cursormestre.close();
        db.close();//closing database
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}