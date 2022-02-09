package amar.das.acbook.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import amar.das.acbook.fragments.ActiveLGFragment;
import amar.das.acbook.fragments.ActiveMFragment;
import amar.das.acbook.fragments.InactiveFragment;

public class FragmentAdapter extends FragmentPagerAdapter {//this adapter will help to replace fragments
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new InactiveFragment();
            case 1: return new ActiveMFragment();
            case 2: return new ActiveLGFragment();
            default:return new ActiveMFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;//since 3 fragment in tablayout
    }

    //to give page title
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if(position==0)
            title="INACTIVE";
        if(position==1)
            title="M";
        if(position==2)
            title="L G";
        return title;
    }
}
