package com.quickCodes.quickCodes.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.quickCodes.quickCodes.fragments.RecentFragment;
import com.quickCodes.quickCodes.fragments.StarredFragment;

public class RecentAdapter extends FragmentStateAdapter {

    public RecentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return RecentFragment.newInstance(1);
            case 1:
                return StarredFragment.newInstance(1);
        }
        return RecentFragment.newInstance(1);
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
