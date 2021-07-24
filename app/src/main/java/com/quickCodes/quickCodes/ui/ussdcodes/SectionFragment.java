package com.quickCodes.quickCodes.ui.ussdcodes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.fragments.OptionsDialogFragment;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SectionFragment extends Fragment {

    private int section;
    private AdapterDialer adapterUssdCodesRecent;
    private UssdActionsViewModel ussdActionsViewModel;
    private LiveData<SimCard> simCardLiveData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public SectionFragment() {
    }

    public SectionFragment(int section) {
        this.section = section;
    }

    public static SectionFragment newInstance(int section) {

        SectionFragment fragment = new SectionFragment(section);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterUssdCodesRecent = new AdapterDialer(getActivity());
        simCardLiveData = Tools.getSelectedSimCardLive(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_recent, container, false);
        View view_recent = view.findViewById(R.id.list);
        LinearLayout linear_layout_no_recent_items = view.findViewById(R.id.linear_layout_no_recent_items);
        ussdActionsViewModel =
                new ViewModelProvider(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(getViewLifecycleOwner(), new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> ussdActionWithSteps) {
                List<UssdActionWithSteps> starred = new ArrayList();
                for (UssdActionWithSteps action : ussdActionWithSteps) {
                    if (action.action.getSection() == section) {
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

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        recyclerView.setAdapter(adapterUssdCodesRecent);

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
        simCardLiveData.observe(getViewLifecycleOwner(), new Observer<SimCard>() {
            @Override
            public void onChanged(SimCard simCard) {
//                Toast.makeText(context, "simcard changed to " + simCard.getNetworkName(), Toast.LENGTH_SHORT).show();
                ussdActionsViewModel.getAllCustomActions().observe(getViewLifecycleOwner(), new Observer<List<UssdActionWithSteps>>() {
                    @Override
                    public void onChanged(List<UssdActionWithSteps> actions) {
                        List<UssdActionWithSteps> items = new ArrayList();
                        for (UssdActionWithSteps action : actions) {
                            if ((action.action.getHni().equals(simCard.getHni()))
                                    && (action.action.getSection() == section)) {
                                items.add(action);
                            }
                        }

                        adapterUssdCodesRecent.setUssdActions(items);

                        if (items.size() < 1) {
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
        OptionsDialogFragment.newInstance(ussdActionsViewModel, p).show(getActivity().getSupportFragmentManager(), "dialog");
    }
}