package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    PersonRecordDatabase db;
    int amountad=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding=FragmentActiveMBinding.inflate(inflater,container,false);
         View root=binding.getRoot();
         db=new PersonRecordDatabase(getContext());
         //ids
         mestreRecyclerView=root.findViewById(R.id.recycle_active_mestre);

         //mestre
        Cursor cursormestre=db.getData("SELECT IMAGE,ID,NAME FROM "+db.TABLE_NAME1 +" WHERE TYPE='M' AND ACTIVE='1' LIMIT 100");//getting only mestre image from database
        mestreactiveArrayList =new ArrayList<>();
        String id,name;
        while(cursormestre.moveToNext()){
            MestreLaberGModel data=new MestreLaberGModel();
            byte[] image=cursormestre.getBlob(0);
            id=cursormestre.getString(1);
            name=cursormestre.getString(2);
            data.setName(name);
            data.setPerson_img(image);
            data.setId(id);
            data.setAdvanceAmount(""+amountad++);
            mestreactiveArrayList.add(data);//adding data to mestrearraylist
        }
        cursormestre.close();//closing cursor after finish
        madapter=new MestreLaberGAdapter(getContext(), mestreactiveArrayList);
        //activeMestreCount.setText(""+madapter.getItemCount());
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