package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    MestreLaberGAdapter madapter;
    PersonRecordDatabase db;
    TextView advance,balance;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding=FragmentActiveLGBinding.inflate(inflater,container,false);
         View root=binding.getRoot();
         db=new PersonRecordDatabase(getContext());//this should be first statement to load data from db
        //ids
        lGRecyclerView=root.findViewById(R.id.recycle_active_l_g);

        advance=root.findViewById(R.id.active_l_g_advance);
        balance=root.findViewById(R.id.active_l_g_balance);
        Cursor advanceBalanceCursor=db.getData("SELECT SUM(ADVANCE),SUM(BALANCE) FROM "+db.TABLE_NAME1+" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1')");
        advanceBalanceCursor.moveToFirst();
        advance.setText(HtmlCompat.fromHtml("ADVANCE: "+"<b>"+advanceBalanceCursor.getLong(0)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        balance.setText(HtmlCompat.fromHtml("BALANCE: "+"<b>"+advanceBalanceCursor.getLong(1)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        advanceBalanceCursor.close();

        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022

        Cursor cursorGL=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE FROM "+db.TABLE_NAME1 +" WHERE (TYPE='L' OR TYPE='G') AND (ACTIVE='1') ORDER BY LATESTDATE='"+currentDateDBPattern+"' LIMIT 150");//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered
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
        madapter=new MestreLaberGAdapter(getContext(), lGArrayList);
        //activeMestreCount.setText(""+madapter.getItemCount());
        lGRecyclerView.setAdapter(madapter);
        lGRecyclerView.setHasFixedSize(true);
        lGRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));//spancount is number of rows
        db.close();//closing database to prevent dataleak

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}