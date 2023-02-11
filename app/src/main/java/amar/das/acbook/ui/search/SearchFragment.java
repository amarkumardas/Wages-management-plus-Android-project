package amar.das.acbook.ui.search;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

import amar.das.acbook.activity.CustomizeLayoutOrDepositAmount;
import amar.das.acbook.activity.FindActivity;
import amar.das.acbook.activity.InsertDataActivity;
import amar.das.acbook.R;
import amar.das.acbook.adapters.FragmentAdapter;
import amar.das.acbook.databinding.FragmentSearchTabBinding;

public class SearchFragment extends Fragment  {
    private FragmentSearchTabBinding binding ;
    TextView searchBox;
   //public static Integer currentTabPosition=0;
    private String[] titles=new String[]{"M","L","INACTIVE"};//to set on pager
    //important
    //to store image in db we have to convert Bitmap to bytearray
    //to set in imageview we have to get from db as Blob known as large byte and convert it to Bitmap then set in imageview
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchTabBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Taking multiple permission at once by user https://www.youtube.com/watch?v=y0gX4FD3nxk or  https://www.youtube.com/watch?v=y0gX4FD3nxk
        //CHECKING ALL PERMISSION IS GRANTED OR NOT
        if((getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        && (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) && (getContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)){
        }else{//if user not granted permission then request for permission
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 80);
        }
        //ids
        searchBox=root.findViewById(R.id.search_click_tv);

        //fragmentAdapter=new FragmentAdapter(getActivity());
        //setting adapter to viewpager
        binding.viewPager2.setAdapter(new FragmentAdapter(getActivity()));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2,((tab,position)-> tab.setText(titles[position]))).attach();//set text to page according to position
//        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {//when tab select or swipe it will perform operation so we are getting position
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        searchBox.setOnClickListener(view -> {
             Intent intent=new Intent(getContext(),FindActivity.class);
             startActivity(intent);

         });
        binding.verticledotsmenuClick.setOnClickListener(view -> {
            PopupMenu popup=new PopupMenu(getContext(),binding.verticledotsmenuClick);
            popup.inflate(R.menu.popuo_menu);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.insert_new:{
                            Intent intent = new Intent(getContext(),InsertDataActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.update:{//can be add more item like setting
                            Toast.makeText(getContext(), "Update button clicked", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    return true;
                }
            });
            popup.show();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

