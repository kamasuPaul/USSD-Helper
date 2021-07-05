package com.quickCodes.quickCodes.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;

/**
 * A fragment representing a list of Items.
 */
public class StarredFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private AdapterDialer adapterUssdCodesRecent;
    private UssdActionsViewModel ussdActionsViewModel;
    private LiveData<SimCard> simCardLiveData;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StarredFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static StarredFragment newInstance(int columnCount) {
        StarredFragment fragment = new StarredFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        adapterUssdCodesRecent = new AdapterDialer(getActivity());
        simCardLiveData = Tools.getSelectedSimCardLive(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_starred, container, false);
        View view_recent = view.findViewById(R.id.list_starred);
        LinearLayout linear_layout_no_recent_items = view.findViewById(R.id.linear_layout_no_recent_starred_items);
        ussdActionsViewModel =
                new ViewModelProvider(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(getViewLifecycleOwner(), new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> actions) {
                List<UssdActionWithSteps> starred = new ArrayList();
                for (UssdActionWithSteps action : actions) {
                    if (action.action.isStarred()) {
                        starred.add(action);
                    }
                }
                adapterUssdCodesRecent.setUssdActions(starred);

                if (starred.size() < 1) {
                    view_recent.setVisibility(View.GONE);
                    linear_layout_no_recent_items.setVisibility(View.VISIBLE);
                } else {

                    view_recent.setVisibility(View.VISIBLE);
                    linear_layout_no_recent_items.setVisibility(View.GONE);
                }
            }
        });
        adapterUssdCodesRecent.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, ussdActionsViewModel);
            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
                createOptionsMenu(v, ussdActionWithSteps, position);

            }

            @Override
            public void onStarClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
                Toast.makeText(getActivity(), ussdActionWithSteps.action.getName() + "has been " + (ussdActionWithSteps.action.isStarred() ? "un starred" : "starred"), Toast.LENGTH_SHORT).show();
                Tools.updateSetStar(ussdActionWithSteps, ussdActionsViewModel);
            }
        });
        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_starred);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        recyclerView.setAdapter(adapterUssdCodesRecent);

        simCardLiveData.observe(getViewLifecycleOwner(), new Observer<SimCard>() {
            @Override
            public void onChanged(SimCard simCard) {
                Toast.makeText(context, "simcard changed " + simCard.getNetworkName(), Toast.LENGTH_SHORT).show();
                ussdActionsViewModel.getAllCustomActions().observe(getViewLifecycleOwner(), new Observer<List<UssdActionWithSteps>>() {
                    @Override
                    public void onChanged(List<UssdActionWithSteps> actions) {
                        List<UssdActionWithSteps> recentList = new ArrayList();
                        for (UssdActionWithSteps action : actions) {
                            if (action.action.getHni().equals(simCard.getHni())) {
                                if (action.action.isStarred()) {
                                    recentList.add(action);
                                }
                            }
                        }
                        adapterUssdCodesRecent.setUssdActions(recentList);

                        if (recentList.size() < 1) {
                            view_recent.setVisibility(View.GONE);
                            linear_layout_no_recent_items.setVisibility(View.VISIBLE);
                        } else {

                            view_recent.setVisibility(View.VISIBLE);
                            linear_layout_no_recent_items.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });

        return view;
    }

    public void createOptionsMenu(final View v, final UssdActionWithSteps p, final int position) {
        //inflate options menu
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        //inflate the menu from layout_no_item resource file
        popupMenu.inflate(R.menu.action_card_menu);
        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit_menu:
                        //edit clicked
                        Intent i = new Intent(getActivity(), EditActionActivity.class);
                        i.putExtra("action_id", String.valueOf(p.action.getActionId()));
                        i.putExtra("section", SEC_CUSTOM_CODES);
                        startActivity(i);
                        break;
                    case R.id.delete_menu:
                        //delete clicked
                        ussdActionsViewModel.delete(p);
                        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        //show the menu
        popupMenu.show();
    }
}