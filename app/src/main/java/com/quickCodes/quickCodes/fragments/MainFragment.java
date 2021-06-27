package com.quickCodes.quickCodes.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.adapters.AdapterSimCards;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.AppLifeCycleListener;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.quickCodes.quickCodes.modals.Constants.SEC_AIRTIME;
import static com.quickCodes.quickCodes.modals.Constants.SEC_DATA;
import static com.quickCodes.quickCodes.modals.Constants.SEC_MMONEY;

public class MainFragment extends Fragment {

    RecyclerView airtimeRecyclerView, dataRecyclerView, mmRecyclerView, recyclerView;
    private UssdActionsViewModel viewModel;
    private AdapterDialer adapterUssdCodes, adapterUssdCodes1, adapterUssdCodes2;
    private AdapterDialer mAdapter;
    private UssdActionsViewModel ussdActionsViewModel;


    private final static String TAG = "MainFragment";
    private AdapterSimCards adapterSimcards;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterUssdCodes = new AdapterDialer(getActivity());
        adapterUssdCodes1 = new AdapterDialer(getActivity());
        adapterUssdCodes2 = new AdapterDialer(getActivity());
        viewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        viewModel.getAllCustomActions().observe(this, new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> ussdActionWithSteps) {

                List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
                List<UssdActionWithSteps> recent = new ArrayList<>();
                List<UssdActionWithSteps> dataCodes = new ArrayList<>();
                List<UssdActionWithSteps> mmoneyCodes = new ArrayList<>();
                for (UssdActionWithSteps us : ussdActionWithSteps) {
                    //skip nulls
                    if (us == null || us.action == null) continue;
                    if (us.action.getName().length() < 15) {
                        int len = 15 - us.action.getName().length();
                        String d = "";
                        for (int i = 0; i < len; i++) {
                            d = d + " ";
                        }
                        us.action.setName(us.action.getName() + d);
                    }
                    //skip loan codes
//                    if (us.action.getActionId() == 203 || us.action.getActionId() == 4) {
//                        continue;
//                    }
                    if (us.action.getSection() == SEC_AIRTIME) {
                        Log.d(TAG, us.action.toString());
                        airtimeCodes.add(us);
                    }
                    if (us.action.getSection() == SEC_DATA) {
                        dataCodes.add(us);
                    }
                    if (us.action.getSection() == SEC_MMONEY) {
                        mmoneyCodes.add(us);
                    }
                    if (recent.size() < 5) {
                        recent.add(us);
                    }
                }
                adapterUssdCodes.setUssdActions(airtimeCodes);
                adapterUssdCodes1.setUssdActions(dataCodes);
                adapterUssdCodes2.setUssdActions(mmoneyCodes);

                //filter out actions that dont apply for the currently selected network
                filterAndHideActions(Tools.getSelectedSimCard(getActivity()).getHni(), null);

            }
        });

        adapterUssdCodes.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, viewModel);
            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
            }
        });
//        });
        adapterUssdCodes1.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, viewModel);
            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
            }
        });
        adapterUssdCodes2.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                Tools.executeSuperAction(obj, getActivity());
                Tools.updateWeightOnClick(obj, viewModel);
            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
            }
        });
        adapterSimcards = new AdapterSimCards(getActivity());


    }


    private void filterAndHideActions(String mode, @Nullable View root) {
        if (mode != null) {

            viewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
                List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
                List<UssdActionWithSteps> dataCodes = new ArrayList<>();
                List<UssdActionWithSteps> mmoneyCodes = new ArrayList<>();
                for (UssdActionWithSteps us : ussdActionWithSteps) {
                    //skip loan codes
//                    if (us.action.getActionId() == 203 || us.action.getActionId() == 4) {
//                        continue;
//                    }
                    if (!(mode.equals(us.action.getHni()))) {
                        continue;

                    }

                    if (us.action.getSection() == SEC_AIRTIME) {
                        airtimeCodes.add(us);
                    }
                    if (us.action.getSection() == SEC_DATA) {
                        dataCodes.add(us);
                    }
                    if (us.action.getSection() == SEC_MMONEY) {
                        mmoneyCodes.add(us);
                    }
                }
                adapterUssdCodes.setUssdActions(airtimeCodes);
                adapterUssdCodes1.setUssdActions(dataCodes);
                adapterUssdCodes2.setUssdActions(mmoneyCodes);

//                if (root != null) {// A view is required to show snabars
                    if (airtimeCodes.size() == 0 && dataCodes.size() == 0 && mmoneyCodes.size() == 0) {
                        //TODO show no codes found page with button to add custom
                        Toast.makeText(getActivity(), "No default ussd codes found for this network, Try adding custom codes", Toast.LENGTH_LONG).show();
//                        Snackbar snackbar = Snackbar.make(root, "No default ussd codes found for this network, add custom codes", Snackbar.LENGTH_LONG);
//                        snackbar.setAction("Add ", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(getActivity(), AddYourOwnActionActivity.class));
//                            }
//                        });
//                        snackbar.show();
                    }
//                }

            });

        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout_no_item for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        //setup airtime
        airtimeRecyclerView = root.findViewById(R.id.airtimeRecylerView);
        airtimeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        airtimeRecyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        airtimeRecyclerView.setAdapter(adapterUssdCodes);
//
//        //setup data
        dataRecyclerView = root.findViewById(R.id.dataRecylerView);
        dataRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        dataRecyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        dataRecyclerView.setAdapter(adapterUssdCodes1);
//        //setup mobile money
        mmRecyclerView = root.findViewById(R.id.mmoneyRecylerView);
        mmRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mmRecyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        mmRecyclerView.setAdapter(adapterUssdCodes2);


        //add this fragment as alifecycle owner so that its lifecycle is observed for lifecycle changes
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifeCycleListener(getActivity()));
        return root;
    }
}

