package amar.das.acbook.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import amar.das.acbook.fragments.ActiveLGFragment;
import amar.das.acbook.fragments.ActiveMFragment;
import amar.das.acbook.fragments.InactiveFragment;

public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
             case 0: return new ActiveMFragment();//default fragment is 0 index setting default as M because m will be less so it will take less time to load
             case 1: return new ActiveLGFragment();
             case 2: return new InactiveFragment();
        }
        return new ActiveMFragment();
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}
