package amar.das.acbook.adapters;

import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import amar.das.acbook.fragments.ActiveLGFragment;
import amar.das.acbook.fragments.ActiveMFragment;
import amar.das.acbook.fragments.InactiveFragment;
import amar.das.acbook.ui.search.SearchFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

//        System.out.println("frag pos************"+position);
//        position=SearchFragment.currentTabPosition;
//        System.out.println("static*************"+position);

        switch (position) {
             case 0: return new ActiveMFragment();//default fragment is 0 index setting default as M because m will be less so it will take less time to load
             case 1: return new ActiveLGFragment();
             case 2: return new InactiveFragment();
        }

        return null;
    }
    @Override
    public int getItemCount() {
        return 3;
    }//since 3 fragment there
}
