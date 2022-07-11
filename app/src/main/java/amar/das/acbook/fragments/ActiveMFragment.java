package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import amar.das.acbook.databinding.FragmentActiveMBinding;


public class ActiveMFragment extends Fragment {

    private FragmentActiveMBinding binding;
    ArrayList<MestreLaberGModel> mestreactiveArrayList;
    RecyclerView mestreRecyclerView;
    MestreLaberGAdapter madapter;
    TextView advance,balance;
    PersonRecordDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding=FragmentActiveMBinding.inflate(inflater,container,false);
         View root=binding.getRoot();
         db=new PersonRecordDatabase(getContext());
         //ids
         mestreRecyclerView=root.findViewById(R.id.recycle_active_mestre);
         advance=root.findViewById(R.id.active_m_advance);
         balance=root.findViewById(R.id.active_m_balance);
         Cursor advanceBalanceCursor=db.getData("SELECT SUM(ADVANCE),SUM(BALANCE) FROM "+db.TABLE_NAME1+" WHERE TYPE='M' AND ACTIVE='1'");
         advanceBalanceCursor.moveToFirst();
        advance.setText(HtmlCompat.fromHtml("ADVANCE: "+"<b>"+advanceBalanceCursor.getLong(0)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
        balance.setText(HtmlCompat.fromHtml("BALANCE: "+"<b>"+advanceBalanceCursor.getLong(1)+"</b>",HtmlCompat.FROM_HTML_MODE_LEGACY));
         advanceBalanceCursor.close();

        LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
        String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022

         //mestre
        Cursor cursormestre=db.getData("SELECT IMAGE,ID,NAME,ADVANCE,BALANCE,LATESTDATE FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' ORDER BY LATESTDATE='"+currentDateDBPattern+"' LIMIT 100");//so that today data entered will be below and not entered data person will be up which will indicate that data is not entered
        mestreactiveArrayList =new ArrayList<>();
        while(cursormestre.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            data.setName(cursormestre.getString(2));
            data.setPerson_img(cursormestre.getBlob(0));
            data.setId(cursormestre.getString(1));
            data.setAdvanceAmount(cursormestre.getInt(3));
            data.setBalanceAmount(cursormestre.getInt(4));
            data.setLatestDate(cursormestre.getString(5));
            mestreactiveArrayList.add(data);//adding data to mestrearraylist
        }
        cursormestre.close();//closing cursor after finish
        madapter=new MestreLaberGAdapter(getContext(), mestreactiveArrayList);
        mestreRecyclerView.setAdapter(madapter);
        mestreRecyclerView.setHasFixedSize(true);
        mestreRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));//spancount is number of rows
        db.close();//closing database to prevent dataleak

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}