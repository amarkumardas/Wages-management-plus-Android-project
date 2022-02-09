package amar.das.acbook.fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import java.util.ArrayList;

import amar.das.acbook.adapters.MestreAdapter;
import amar.das.acbook.model.MestreLaberGModel;
import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.databinding.FragmentInactiveBinding;



public class InactiveFragment extends Fragment {
    private FragmentInactiveBinding binding;
    static int amountad=1;
    ArrayList<MestreLaberGModel> arrayList1_6000;
    ArrayList<MestreLaberGModel> arrayList6001_above;

    RecyclerView recyclerView1_6000;
    RecyclerView recyclerView6001_above;
    MestreAdapter madapter;
    Boolean isScrolling1 =false;
    Boolean isScrolling2 =false;

    int currentItem1, totalItem1, scrollOutItems1,currentItem2, totalItem2, scrollOutItems2;
    PersonRecordDatabase db;
    LinearLayoutManager manager1,manager2;

   public InactiveFragment(){}//required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentInactiveBinding.inflate(inflater, container, false);
         View root=binding.getRoot();
        db=new PersonRecordDatabase(getContext());//on start only database should be create
        //ids
        recyclerView1_6000 =root.findViewById(R.id.recycle1_6000);
        //mestreRecyclerView.setHasFixedSize(true);//telling to recycler view that dont calculate item size every time when added and remove from recyclerview
        recyclerView6001_above =root.findViewById(R.id.recycle6001_above);

        //1-6000
        Cursor cursormestre=db.getData("SELECT IMAGE,ID FROM "+db.TABLE_NAME+" WHERE TYPE='M' AND ACTIVE='1' LIMIT 14 ");//getting image from database
        // Cursor cursorinactive=db.getImage("SELECT IMAGE,ADVANCEAMOUNT FROM "+db.TABLE_NAME+" WHERE ADVANCEAMOUNT  BETWEEN 0 AND 3000 AND ACTIVE='0' ORDER BY ADVANCEAMOUNT DESC");//this query will fetch image and advanceamount between 0 to 3000 and is not active ie;0 in decending order
        arrayList1_6000 =new ArrayList<>();
        byte[] image;
        String id;
        while(cursormestre.moveToNext()){
            MestreLaberGModel model=new MestreLaberGModel();
            image=cursormestre.getBlob(0);
            id=cursormestre.getString(1);
            model.setPerson_img(image);
            model.setAdvanceAmount("0000000000");
            model.setId(id);
            arrayList1_6000.add(model);
        }
        cursormestre.close();
        madapter=new MestreAdapter(getContext(), arrayList1_6000);
        //activeMestreCount.setText(""+madapter.getItemCount());
        recyclerView1_6000.setAdapter(madapter);
        manager1 =new GridLayoutManager(getContext(),2);//grid layout

        recyclerView1_6000.setLayoutManager(manager1);
        recyclerView1_6000.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override//this method is called when we start scrolling recycleview
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //this will tell the state of scrolling if user is scrolling then isScrolling variable will become true
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling1 =true;//when user start to scroll then this varilable will be true
                }
            }

            @Override//after scrolling finished then this method will be called
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem1 = manager1.getChildCount();
                totalItem1 =madapter.getItemCount();// totalItem=manager.getItemCount();
                scrollOutItems1 = manager1.findFirstVisibleItemPosition();

                if(isScrolling1 && (currentItem1 + scrollOutItems1 == totalItem1)){
                    isScrolling1 =false;
                    Toast.makeText(getContext(), "Please Wait Loading", Toast.LENGTH_SHORT).show();
                    fetchData("SELECT IMAGE,ID FROM " + db.TABLE_NAME + " WHERE TYPE='M' OR TYPE='L' OR TYPE='G' AND ACTIVE='1'",arrayList1_6000);
                    recyclerView1_6000.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last

                }
            }
        });


        //6001-ABOVE
        Cursor cursorinactive=db.getData("SELECT IMAGE,ID FROM " + db.TABLE_NAME + " WHERE TYPE='M' OR TYPE='L' OR TYPE='G' AND ACTIVE='1' LIMIT 14 ");//getting image from database
        // Cursor cursorinactive=db.getImage("SELECT IMAGE,ADVANCEAMOUNT FROM "+db.TABLE_NAME+" WHERE ADVANCEAMOUNT  BETWEEN 10001 AND 1000000 AND ACTIVE='0' ORDER BY ADVANCEAMOUNT DESC");

        arrayList6001_above =new ArrayList<>();
        while(cursorinactive.moveToNext()){
            MestreLaberGModel model=new MestreLaberGModel();
            image=cursorinactive.getBlob(0);
            id=cursorinactive.getString(1);
            model.setPerson_img(image);
            model.setAdvanceAmount("0000000000");
            model.setId(id);
            arrayList6001_above.add(model);
        }
        cursorinactive.close();
        db.close();
        madapter=new MestreAdapter(getContext(), arrayList6001_above);

        manager2 =new GridLayoutManager(getContext(),2);//grid layout
        recyclerView6001_above.setAdapter(madapter);
        recyclerView6001_above.setLayoutManager(manager2);
        recyclerView6001_above.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override//this method is called when we start scrolling recycleview
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //this will tell the state of scrolling if user is scrolling then isScrolling variable will become true
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling2 =true;//when user start to scroll then this varilable will be true
                }
            }

            @Override//after scrolling finished then this method will be called
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItem2 = manager2.getChildCount();
                totalItem2 =madapter.getItemCount();// totalItem=manager.getItemCount();
                scrollOutItems2 = manager2.findFirstVisibleItemPosition();

                if(isScrolling2 && (currentItem2 + scrollOutItems2 == totalItem2)){
                     isScrolling2 =false;
                    Toast.makeText(getContext(), "Please Wait Loading", Toast.LENGTH_SHORT).show();
                    fetchData("SELECT IMAGE,ID FROM " + db.TABLE_NAME + " WHERE TYPE='M' OR TYPE='L' OR TYPE='G' AND ACTIVE='1'",arrayList6001_above); //LIMIT 14 OFFSET 50 NOT working
                    recyclerView6001_above.clearOnScrollListeners();//this will remove scrollListener so we wont be able to scroll after loading all data and finished scrolling to last
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
                //Cursor cursorinactive=db.getImage("SELECT IMAGE,ADVANCEAMOUNT FROM "+db.TABLE_NAME+" WHERE ADVANCEAMOUNT  BETWEEN  50000 AND 1000000 AND ACTIVE='0' ORDER BY ADVANCEAMOUNT DESC");//this query will fetch image and advanceamount between 0 to 3000 and is not active ie;0 in decending order
            }
        }, 3000);
    }

    private void dataLoad(String querys,ArrayList<MestreLaberGModel> arraylist){
        Cursor cursormestre = db.getData(querys);//getting image from database
        byte[] image;
        String id;
        //Toast.makeText(getContext(), ""+arraylist.size(), Toast.LENGTH_SHORT).show(); error null pointer exception
        arraylist.clear();//clearing the previous object which is there ie.14 object
        while (cursormestre.moveToNext()) {
            MestreLaberGModel model = new MestreLaberGModel();
             image = cursormestre.getBlob(0);
             id=cursormestre.getString(1);
            model.setPerson_img(image);
            model.setAdvanceAmount(""+amountad++);
            model.setId(id);
            arraylist.add(model);
            madapter.notifyDataSetChanged();//Use the notifyDataSetChanged() every time the list is updated,or inserted or deleted
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