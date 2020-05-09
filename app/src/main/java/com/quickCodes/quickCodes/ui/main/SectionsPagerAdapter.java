package com.quickCodes.quickCodes.ui.main;

import android.content.Context;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.fragments.AutoDetectedFragment;
import com.quickCodes.quickCodes.fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<>();
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        fragmentList.add(new MainFragment());
        fragmentList.add(CustomCodesFragment.newInstance(1));
        fragmentList.add(new AutoDetectedFragment());

    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a CustomCodesFragment (defined as a static inner class below).
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }
}
