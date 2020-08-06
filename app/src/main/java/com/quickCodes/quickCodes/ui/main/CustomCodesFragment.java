package com.quickCodes.quickCodes.ui.main;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterGridCustomCodes;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;

//import com.hover.sdk.api.HoverParameters;

/**
 * A placeholder fragment containing a simple view.
 */
public class CustomCodesFragment extends Fragment {
    private static final String TAG = "CustomCodesFragment";
    UssdActionsViewModel ussdActionsViewModel;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String sharedPrefString = "first";
//    String info_text ="You will find all your custom codes \n here";

    public static AdapterGridCustomCodes mAdapter;
    private RecyclerView recyclerView;

    public static CustomCodesFragment newInstance(int index) {
        CustomCodesFragment fragment = new CustomCodesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AdapterGridCustomCodes(getActivity());
        ussdActionsViewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
            List<UssdActionWithSteps> customCodes = new ArrayList<>();
            for (UssdActionWithSteps us : ussdActionWithSteps) {
                if (us.action.getSection() == SEC_CUSTOM_CODES) {
                    List<SimCard> simCards = Tools.getAvailableSimCards(getActivity());
                    if (Tools.containsHni(simCards, us.action.getHni())) {
                        customCodes.add(us);

                    }
                }
            }
            mAdapter.setCustomActions(customCodes);
//            if(customCodes.isEmpty()){
//                info_text = "You have not saved any codes yet,all your custom codes will appear \n here. To add any code tap the + button";
//            }
        });
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_codes, container, false);
        initComponent(root);
        return root;
    }


    private void initComponent(View root) {
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new MyItemDecorator(2, 5));
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridCustomCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, ussdActionsViewModel);
            }

            @Override
            public void onItemDelete(View view, UssdActionWithSteps obj, int position) {
                ussdActionsViewModel.delete(obj);
            }

            @Override
            public void onItemEdit(View view, UssdActionWithSteps obj, int position) {
                Intent i = new Intent(getActivity(), EditActionActivity.class);
                i.putExtra("action_id", String.valueOf(obj.action.getActionId()));
                startActivity(i);
            }
        });
//        TextView info = root.findViewById(R.id.info_textView);
//        info.setText(info_text);

    }

//********************************************UTILITY METHODS ****************************
    public static class MyItemDecorator extends RecyclerView.ItemDecoration {
        private int margin, columns;
        public MyItemDecorator(int columns, int margin) {
            this.columns = columns;
            this.margin = margin;
        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position < columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if (position % columns == 0) {
                outRect.left = margin;
            }
        }
    }
}


