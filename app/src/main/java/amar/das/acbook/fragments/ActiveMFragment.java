package amar.das.acbook.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;


import amar.das.acbook.adapters.MestreLaberGAdapter;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.FragmentActiveMBinding;
import amar.das.acbook.utility.MyProjectUtility;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

//        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
//        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022

       // int totalRecord=getCountOfTotalRecordFromDb();//this will execute when refreshed ie.when new person is added and it will be helpfull to know exactly how much the record is there to load

        Cursor cursormestre=null;
        mestreactiveArrayList =new ArrayList<>(40);//insuring initial capacity to 40 because at first at least 40 record will be inserted 37+if new person present
        //**if lastestdate is null then first it will be top of arraylist thats why two whileoop is used
        cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NULL");
        while(cursormestre.moveToNext()){//if cursor has 0 record then cursormestre.moveToNext() return false
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            data.setTime(cursormestre.getString(6));
            mestreactiveArrayList.add(data);
        }
        cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC LIMIT 37");
        while(cursormestre.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            data.setTime(cursormestre.getString(6));
            mestreactiveArrayList.add(data);
        }

        mestreactiveArrayList.trimToSize();//to release free space
        sortArrayList(mestreactiveArrayList);
        cursormestre.close();//closing cursor after finish
        db.close();//closing database to prevent dataleak
        mestreLaberGAdapter =new MestreLaberGAdapter(getContext(), mestreactiveArrayList);
        mestreRecyclerView.setAdapter(mestreLaberGAdapter);
        mestreRecyclerView.setHasFixedSize(true);//telling to recycler view that dont calculate item size every time when added and remove from recyclerview
        layoutManager=new GridLayoutManager(getContext(),4);//spancount is number of rows
        mestreRecyclerView.setLayoutManager(layoutManager);
        mestreRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //int hold=40;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {//Callback method to be invoked when RecyclerView's scroll state changes.
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
                    fetchData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC ",mestreactiveArrayList);
                    //fetchData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC LIMIT "+(hold)+","+40,mestreactiveArrayList);
                    // hold=hold+40;
                   // mestreRecyclerView.scrollToPosition(0);
                   // if((hold) > totalRecord){
                       // Toast.makeText(getContext(), "hold-"+hold+"-total record "+totalRecord, Toast.LENGTH_SHORT).show();
                        mestreRecyclerView.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last
                   // }
                }
            }
        });
        return root;
    }



    public int getCountOfTotalRecordFromDb() {
        int count=0;
        PersonRecordDatabase  db=new PersonRecordDatabase(getContext());
        Cursor cursor=null;
        cursor=db.getData("SELECT COUNT() FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NULL");
        cursor.moveToFirst();
        count=cursor.getInt(0);
        cursor=db.getData("SELECT COUNT() FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NOT NULL");
        cursor.moveToFirst();
        count=count+cursor.getInt(0);
        db.close();
        return count;
    }

    public static void sortArrayList(ArrayList<MestreLaberGModel> mestreactiveArrayList) {
     /**
      *This function will sort Arraylist in such a way that all LatestDate which is null will be at top.
      * and based on that it will sort the remaining record*/

        int nullCountInArraylist[]=countNullAndTodayLatestdateAndBringAllLatestDateWhichIsNullAtTop(mestreactiveArrayList);//it will return null count and lastestdate count and swap all lastestdate which is null and bring at top of arrayList

        if(nullCountInArraylist[0]== 0 && nullCountInArraylist[1] == 0){//if today LAtestdate is not there and new person not there then execute this
            Collections.sort(mestreactiveArrayList.subList(0, mestreactiveArrayList.size()));//natural sorting based on latestdate desc
         }else {
            //sorting to original array in asc order by taking latestdate
            Collections.sort(mestreactiveArrayList.subList(nullCountInArraylist[0], mestreactiveArrayList.size()), new Comparator<MestreLaberGModel>() {
                DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                @Override
                public int compare(MestreLaberGModel obj1, MestreLaberGModel obj2) {
                    try {
                        return sdf.parse(obj1.getLatestDate()).compareTo(sdf.parse(obj2.getLatestDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
            //arraylist Manipulation
            if (nullCountInArraylist[1] != 0) { //if there is today latestdate then sort last part using time desc order
                Collections.sort(mestreactiveArrayList.subList(mestreactiveArrayList.size() - nullCountInArraylist[1], mestreactiveArrayList.size()), (obj1, obj2) -> {
                    Date obj1Date = null, obj2Date = null;
                    SimpleDateFormat format24hrs = new SimpleDateFormat("HH:mm:ss aa");//24 hrs format
                    SimpleDateFormat format12hrs = new SimpleDateFormat("hh:mm:ss aa");//12 hrs format
                    try {
                        obj1Date = format12hrs.parse(obj1.getTime());
                        obj2Date = format12hrs.parse(obj2.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    String obj1StringDate=format24hrs.format(obj1Date);
//                    String obj2StringDate=format24hrs.format(obj2Date);
                   return Integer.parseInt(format24hrs.format(obj2Date).replaceAll("[:]", "").substring(0, 6)) - Integer.parseInt(format24hrs.format(obj1Date).replaceAll("[:]", "").substring(0, 6));
                });//first making "00:59:30 PM" to 5930 .removing start 0 and :,AM,PM.PARSING TO INTEGER so that start 0 will remove.//sort   time in desc order.index start from 0 n-1.this will keep todays time on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time
            }
            //since array list already sorted in asc order so just reversing to get desc order
            reverseArrayListUsingIndex(mestreactiveArrayList,nullCountInArraylist[0], mestreactiveArrayList.size() - nullCountInArraylist[1]);
        }
    }

    public static <T> void reverseArrayListUsingIndex(List<T> al, int start, int end){
        while (start < end) {
            T a=al.get(start);
            T b=al.get(end-1);
            al.set(start,b);
            al.set(end-1,a);
            start++;
            end--;
        }
    }

    public static int[] countNullAndTodayLatestdateAndBringAllLatestDateWhichIsNullAtTop(ArrayList<MestreLaberGModel> al) {
        int arr[]=new int[2];
        int nullIndex=0;
       // LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
       // String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
        for (int i = 0; i < al.size(); i++) {
            if(al.get(i).getLatestDate()== null){
                swapAndBringNullObjAtTopOfArrayList(al,nullIndex,i);
                nullIndex++;
                arr[0]++;//nullCount
            }                                                    //today date
            else if(al.get(i).getLatestDate().equals(""+LocalDate.now().getDayOfMonth()+"-"+ LocalDate.now().getMonthValue()+"-"+ LocalDate.now().getYear())){
               arr[1]++;//todayLatestDateCount
            }
        }
        return arr;
    }

    public static void swapAndBringNullObjAtTopOfArrayList(ArrayList<MestreLaberGModel> al, int nullIndex, int i) {
        MestreLaberGModel nullObj=al.get(i);
        MestreLaberGModel nonNullObj=al.get(nullIndex);
        al.set(i,nonNullObj);
        al.set(nullIndex,nullObj);
    }

    private void fetchData(String query, ArrayList<MestreLaberGModel> arraylist) {
        new Handler().postDelayed(() -> {
            dataLoad(query,arraylist);
            progressBar.setVisibility(View.GONE);//after data loading progressbar disabled
         }, 1000);
    }

    private void dataLoad(String querys,ArrayList<MestreLaberGModel> arraylist) {
        db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        Cursor cursormestre=null;
        arraylist.clear();//clearing the previous object which is there
        arraylist.ensureCapacity(getCountOfTotalRecordFromDb());//to get exact arraylist storage to store exact record

        //**if lastestdate is null then first it will be top of arraylist thats why two whileoop is used
        cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' AND LATESTDATE IS NULL");
        while(cursormestre.moveToNext()){//if cursor has 0 record then cursormestre.moveToNext() return false
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            data.setTime(cursormestre.getString(6));
            arraylist.add(data);//adding data to mestrearraylist
        }
       // mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() when the list is updated,or inserted or deleted


        cursormestre = db.getData(querys);//getting data from db
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
        }
        mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() when the list is updated,or inserted or deleted

        arraylist.trimToSize();//to release free space
        sortArrayList(arraylist);
        cursormestre.close();
        db.close();//closing database
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}