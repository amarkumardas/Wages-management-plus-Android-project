package amar.das.acbook.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import amar.das.acbook.PersonRecordDatabase;
import amar.das.acbook.R;
import amar.das.acbook.adapters.AllMLGRecordAdapter;
import amar.das.acbook.adapters.SearchAdapter;
import amar.das.acbook.model.MLGAllRecordModel;
import amar.das.acbook.model.SearchModel;

public class FindActivity extends AppCompatActivity {
SearchView searchView;
RecyclerView searchRecycler;
ArrayList<SearchModel> datalist;
ArrayList<MLGAllRecordModel> allMLGList;
PersonRecordDatabase db;
Button goback_click,btn1,btn2,btn3;
Boolean aboolean=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        db=new PersonRecordDatabase(this);//on start only database should be create
        //ids
        goback_click =findViewById(R.id.goback);
        searchView=findViewById(R.id.serach_view);
        searchRecycler=findViewById(R.id.search_recyclerview);
        btn1=findViewById(R.id.mestre_btn);
        btn2=findViewById(R.id.laber_btn);
        btn3=findViewById(R.id.g_btn);


        searchRecycler.setHasFixedSize(true);

        //getting all data
        Cursor cursor=db.getData("SELECT ID,NAME,BANKACCOUNT,AADHARCARD,FATHERNAME FROM "+db.TABLE_NAME+" WHERE ACTIVE='1' OR ACTIVE='0'");
        datalist=new ArrayList<>();

        while(cursor.moveToNext()){
            SearchModel model=new SearchModel();
            model.setId(""+cursor.getString(0));
            model.setName(""+cursor.getString(1));
            model.setAccount(""+cursor.getString(2));
            model.setAadhar(""+cursor.getString(3));
            model.setFather(""+cursor.getString(4));
            datalist.add(model);
        }
        cursor.close();
        SearchAdapter searchAdapter=new SearchAdapter(this,datalist);

        searchRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        searchRecycler.setAdapter(searchAdapter);
        db.close();//closing database to prevent dataleak


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(aboolean==true)//this will set adapter to recycler view while switching from button M L G
                    searchRecycler.setAdapter(searchAdapter);

               searchAdapter.getFilter().filter(newText);
                return false;
            }
        });

        goback_click.setOnClickListener(new View.OnClickListener() {//go from activity to fragment
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void mestre_btn(View view) {
        //setting back ground color
        view.setBackgroundColor(Color.GREEN);
        btn2.setBackgroundColor(Color.WHITE);
        btn3.setBackgroundColor(Color.WHITE);
        btnData("SELECT ID,NAME,ACTIVE FROM "+db.TABLE_NAME+" WHERE TYPE='M'");
    }

    public void laber_btn(View view) {
        //setting back ground color
        view.setBackgroundColor(Color.GREEN);
        btn1.setBackgroundColor(Color.WHITE);
        btn3.setBackgroundColor(Color.WHITE);
        btnData("SELECT ID,NAME,ACTIVE FROM "+db.TABLE_NAME+" WHERE TYPE='L'");
    }

    public void g_btn(View view) {
        //setting back ground color
        view.setBackgroundColor(Color.GREEN);
        btn1.setBackgroundColor(Color.WHITE);
        btn2.setBackgroundColor(Color.WHITE);
        btnData("SELECT ID,NAME,ACTIVE FROM "+db.TABLE_NAME+" WHERE TYPE='G'");
    }

    public void btnData(String query){
        //fetching data
        Cursor cursor2=db.getData(query);
        allMLGList=new ArrayList<>();

        while(cursor2.moveToNext()){
            MLGAllRecordModel model=new MLGAllRecordModel();
            model.setId(cursor2.getString(0));
            model.setName(cursor2.getString(1));
            model.setActive(cursor2.getString(2));//to set view red if inactive
            allMLGList.add(model);
        }
        cursor2.close();
        //sorting according to name IN accending order by default
        allMLGList.sort(new Comparator<MLGAllRecordModel>() {
            @Override
            public int compare(MLGAllRecordModel mlgAllRecordModel, MLGAllRecordModel t1) {
                return mlgAllRecordModel.getName().compareTo(t1.getName());
            }
        });

        AllMLGRecordAdapter allMLGRecordAdapter=new AllMLGRecordAdapter(this,allMLGList);
        searchRecycler.setHasFixedSize(true);
        searchRecycler.setAdapter(allMLGRecordAdapter);
        aboolean=true;//to set recycler view on onQueryTextChange method
        db.close();//closing database to prevent dataleak
        Toast.makeText(FindActivity.this, "Total size: "+allMLGRecordAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
    }

}