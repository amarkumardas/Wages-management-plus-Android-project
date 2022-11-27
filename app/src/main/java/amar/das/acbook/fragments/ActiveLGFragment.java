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

import java.time.LocalDate;
import java.util.ArrayList;

import amar.das.acbook.adapters.MestreLaberGAdapter;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.FragmentActiveLGBinding;


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
        advance.setText(HtmlCompat.fromHtml("ADVANCE- "+"<b>"+advanceBalanceCursor.getLong(0)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        balance.setText(HtmlCompat.fromHtml("BALANCE- "+"<b>"+advanceBalanceCursor.getLong(1)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        advanceBalanceCursor.close();

        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
        System.out.println("LG");
        Cursor cursorGL=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') ORDER BY LATESTDATE='"+currentDateDBPattern+"' LIMIT 36");//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered
        lGArrayList =new ArrayList<>();

        while(cursorGL.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursorGL.getString(2));
            data.setPerson_img(cursorGL.getBlob(0));
            data.setId(cursorGL.getString(1));
            data.setAdvanceAmount(cursorGL.getInt(3));
            data.setBalanceAmount(cursorGL.getInt(4));
            data.setLatestDate(cursorGL.getString(5));
            lGArrayList.add(data);//adding data to mestrearraylist
        }
        cursorGL.close();//closing cursor after finish
        db.close();//closing database to prevent dataleak
        mestreLaberGAdapter =new MestreLaberGAdapter(getContext(), lGArrayList);
        //activeMestreCount.setText(""+madapter.getItemCount());
        lGRecyclerView.setAdapter(mestreLaberGAdapter);
        lGRecyclerView.setHasFixedSize(true);//telling to recycler view that dont calculate item size every time when added and remove from recyclerview
        layoutManager=new GridLayoutManager(getContext(),4);
        lGRecyclerView.setLayoutManager(layoutManager);//spancount is number of rows
        lGRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                    fetchData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') ORDER BY LATESTDATE='"+currentDateDBPattern+"'",lGArrayList);
                    lGRecyclerView.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last

                }
            }
        });
        return root;
    }
    private void fetchData(String query, ArrayList<MestreLaberGModel> arraylist) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dataLoad(query,arraylist);
                progressBar.setVisibility(View.GONE);//after data loading progressbar disabled
                //Cursor cursorinactive=db.getImage("SELECT IMAGE,ADVANCEAMOUNT FROM "+db.TABLE_NAME+" WHERE ADVANCEAMOUNT  BETWEEN  50000 AND 1000000 AND ACTIVE='0' ORDER BY ADVANCEAMOUNT DESC");//this query will fetch image and advanceamount between 0 to 3000 and is not active ie;0 in decending order
            }

        }, 2000);
    }

    private void dataLoad(String querys,ArrayList<MestreLaberGModel> arraylist){
        db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        Cursor cursormestre = db.getData(querys);//getting image from database
        //Toast.makeText(getContext(), ""+arraylist.size(), Toast.LENGTH_SHORT).show(); error null pointer exception
        arraylist.clear();//clearing the previous object which is there ie.14 object
        while (cursormestre.moveToNext()) {
            MestreLaberGModel data = new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            arraylist.add(data);
            mestreLaberGAdapter.notifyDataSetChanged();//Use the notifyDataSetChanged() every time the list is updated,or inserted or deleted
        }
        cursormestre.close();
        db.close();//closing database
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}