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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

import amar.das.acbook.activity.CustomizeLayoutOrDepositAmount;
import amar.das.acbook.activity.FindActivity;
import amar.das.acbook.activity.InsertDataActivity;
import amar.das.acbook.R;
import amar.das.acbook.adapters.FragmentAdapter;
import amar.das.acbook.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment  {
    private FragmentSearchBinding binding ;
    TextView searchBox;
    //important
    //to store image in db we have to convert Bitmap to bytearray
    //to set in imageview we have to get from db as Blob known as large byte and convert it to Bitmap then set in imageview
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
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

        //setting adapter to viewpager
        binding.viewPager.setAdapter(new FragmentAdapter(getChildFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

         searchBox.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(getContext(),FindActivity.class);
                 startActivity(intent);

             }
         });


        binding.verticledotsmenuClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

