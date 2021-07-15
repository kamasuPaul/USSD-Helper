package com.quickCodes.quickCodes.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.quickCodes.quickCodes.modals.Constants;
import com.quickCodes.quickCodes.ui.ussdcodes.SectionFragment;

public class UssdCodesAdapter extends FragmentStateAdapter {

    public UssdCodesAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return SectionFragment.newInstance(Constants.SEC_AIRTIME);
            case 1:
                return SectionFragment.newInstance(Constants.SEC_DATA);
            case 2:
                return SectionFragment.newInstance(Constants.SEC_MMONEY);
            case 3:
                return SectionFragment.newInstance(Constants.SEC_CUSTOM_CODES);
            case 4:
                return SectionFragment.newInstance(Constants.AUTO_SAVED_CODES);
        }
        return SectionFragment.newInstance(1);
    }


    @Override
    public int getItemCount() {
        return 5;
    }
}
