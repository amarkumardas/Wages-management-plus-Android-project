package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import amar.das.acbook.adapters.MestreLaberGAdapter;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.FragmentActiveLGBinding;
import amar.das.acbook.utility.ProjectUtility;


public class ActiveLGFragment extends Fragment {

    private FragmentActiveLGBinding binding;
    ArrayList<MestreLaberGModel> lGArrayList;
    RecyclerView lGRecyclerView;
    MestreLaberGAdapter mestreLaberGAdapter;
    PersonRecordDatabase db;
    TextView advance,balance;

    Boolean isScrolling1 =false;
    LinearLayoutManager layoutManager;
    int currentItem1, totalItem1, scrollOutItems1;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding=FragmentActiveLGBinding.inflate(inflater,container,false);
         View root=binding.getRoot();
         db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        //ids
        lGRecyclerView=root.findViewById(R.id.recycle_active_l_g);
        progressBar=binding.progressBarActiveLG;
        progressBar.setVisibility(View.GONE);//initially visibility will be not there only when data is loading then visibility set visible


        advance=root.findViewById(R.id.active_l_g_advance);
        balance=root.findViewById(R.id.active_l_g_balance);
        Cursor advanceBalanceCursor=db.getData("SELECT SUM(ADVANCE),SUM(BALANCE) FROM "+db.TABLE_NAME1+" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')");
        advanceBalanceCursor.moveToFirst();
        advance.setText(HtmlCompat.fromHtml("ADVANCE: "+"<b>"+advanceBalanceCursor.getLong(0)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        balance.setText(HtmlCompat.fromHtml("BALANCE: "+"<b>"+advanceBalanceCursor.getLong(1)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        advanceBalanceCursor.close();

//        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
//        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
//        System.out.println("LG");
        Cursor cursorGL=null;
        lGArrayList =new ArrayList<>(100);

        cursorGL=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')  AND LATESTDATE IS NULL");//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered
        while(cursorGL.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursorGL.getString(2));
            data.setPerson_img(cursorGL.getBlob(0));
            data.setId(cursorGL.getString(1));
            data.setAdvanceAmount(cursorGL.getInt(3));
            data.setBalanceAmount(cursorGL.getInt(4));
            data.setLatestDate(cursorGL.getString(5));
            data.setTime(cursorGL.getString(6));
            lGArrayList.add(data);//adding data to mestrearraylist
        }
       // cursorGL=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')  AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC LIMIT "+ActiveMFragment.initialLoadDataFotActiveMAndL);//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered
        cursorGL=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')  AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC ");//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered

        while(cursorGL.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursorGL.getString(2));
            data.setPerson_img(cursorGL.getBlob(0));
            data.setId(cursorGL.getString(1));
            data.setAdvanceAmount(cursorGL.getInt(3));
            data.setBalanceAmount(cursorGL.getInt(4));
            data.setLatestDate(cursorGL.getString(5));
            data.setTime(cursorGL.getString(6));
            lGArrayList.add(data);//adding data to mestrearraylist
        }


//        //to keep today latest date profile img up
//        cursorGL=db.getData("SELECT  COUNT(*) FROM " +db.TABLE_NAME1+" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') AND LATESTDATE= '"+currentDateDBPattern+"'");//to get number of rows to decide sublist.The COUNT(*) function returns the number of rows in a table, including the rows including NULL and duplicates.
//        if(cursorGL!=null) {
//            cursorGL.moveToFirst();//since only 1 column so movetoFirst
//            if(cursorGL.getInt(0) != 0) { //if 0 then no need to sort                                                                          //if mestreactiveArrayList.size() is 25 then till 24 sublist will be created
//                Collections.sort(lGArrayList.subList(lGArrayList.size() - cursorGL.getInt(0), lGArrayList.size()),(obj1,obj2)-> -obj1.getTime().compareTo(obj2.getTime()));//sort data by taking time in desc order.index start from 0 n-1.this will keep todays date on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time
//            }
//        }
        lGArrayList.trimToSize();
        ProjectUtility.sortArrayList(lGArrayList);
        cursorGL.close();//closing cursor after finish
        db.close();//closing database to prevent dataleak
        mestreLaberGAdapter =new MestreLaberGAdapter(getContext(), lGArrayList);
        //activeMestreCount.setText(""+madapter.getItemCount());
        lGRecyclerView.setAdapter(mestreLaberGAdapter);
        lGRecyclerView.setHasFixedSize(true);//telling to recycler view that dont calculate item size every time when added and remove from recyclerview
        layoutManager=new GridLayoutManager(getContext(),4);
        lGRecyclerView.setLayoutManager(layoutManager);//spancount is number of rows
//        lGRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) { //Callback method to be invoked when RecyclerView's scroll state changes.
//                super.onScrollStateChanged(recyclerView, newState);
//                //this will tell the state of scrolling if user is scrolling then isScrolling variable will become true
//                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//                    isScrolling1 =true;//when user start to scroll then this varilable will be true
//                }
//            }
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {//Callback method to be invoked when the RecyclerView has been scrolled. This will be called after the scroll has completed.This callback will also be called if visible item range changes after a layout calculation. In that case, dx and dy will be 0.
//                super.onScrolled(recyclerView, dx, dy);
//                currentItem1 = layoutManager.getChildCount();
//                totalItem1 = mestreLaberGAdapter.getItemCount();// totalItem=manager.getItemCount();
//                scrollOutItems1 = layoutManager.findFirstVisibleItemPosition();
//                // Toast.makeText(getContext(), "c= "+currentItem1+"o= "+scrollOutItems1+"t= "+totalItem1, Toast.LENGTH_SHORT).show();
//
//                if(isScrolling1 && (currentItem1 + scrollOutItems1 == totalItem1)){
//                    isScrolling1 =false;
//                    progressBar.setVisibility(View.VISIBLE);//progressbar
//                    Toast.makeText(getContext(), "PLEASE WAIT LOADING", Toast.LENGTH_SHORT).show();
//                    fetchData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC",lGArrayList);
//                    lGRecyclerView.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last
//
//                }
//            }
//        });
        return root;
    }
    public int getCountOfTotalRecordFromDb() {
        int count=0;
        PersonRecordDatabase  db=new PersonRecordDatabase(getContext());
        Cursor cursor=null;
        cursor=db.getData("SELECT COUNT() FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')  AND LATESTDATE IS NULL");
        cursor.moveToFirst();
        count=cursor.getInt(0);
        cursor=db.getData("SELECT COUNT() FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') AND LATESTDATE IS NOT NULL ORDER BY LATESTDATE DESC");
        cursor.moveToFirst();
        count=count+cursor.getInt(0);
        db.close();
        return count;
    }
    private void fetchData(String query, ArrayList<MestreLaberGModel> arraylist) {
        new Handler().postDelayed(() -> {
            dataLoad(query,arraylist);
            progressBar.setVisibility(View.GONE);//after data loading progressbar disabled
         }, 1000);
    }

    private void dataLoad(String querys,ArrayList<MestreLaberGModel> arraylist){
        db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        Cursor cursormestre=null;
        arraylist.clear();//clearing the previous object which is there ie.initial data
        arraylist.ensureCapacity(getCountOfTotalRecordFromDb());//to get exact arraylist storage to store exact record

        cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE,TIME FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')  AND LATESTDATE IS NULL");
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
      //  mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() every time the list is updated,or inserted or deleted

        cursormestre = db.getData(querys);//getting image from database
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
        mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() every time the list is updated,or inserted or deleted


        arraylist.trimToSize();//to free space
        ProjectUtility.sortArrayList(arraylist);
        cursormestre.close();
        db.close();//closing database
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}