package com.quickCodes.quickCodes.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterUssdCodes;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.AppLifeCycleListener;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;
import com.robertlevonyan.views.chip.Chip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.quickCodes.quickCodes.modals.Constants.SEC_AIRTIME;
import static com.quickCodes.quickCodes.modals.Constants.SEC_DATA;
import static com.quickCodes.quickCodes.modals.Constants.SEC_MMONEY;

public class MainFragment extends Fragment {

    RecyclerView airtimeRecyclerView, dataRecyclerView, mmRecyclerView;
    private OnFragmentInteractionListener mListener;
    private UssdActionsViewModel viewModel;
    private AdapterUssdCodes adapterUssdCodes, adapterUssdCodes1, adapterUssdCodes2;


    private final static String TAG = "MainFragment";

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterUssdCodes = new AdapterUssdCodes(getActivity());
        adapterUssdCodes1 = new AdapterUssdCodes(getActivity());
        adapterUssdCodes2 = new AdapterUssdCodes(getActivity());
        viewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        viewModel.getAllCustomActions().observe(this, new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> ussdActionWithSteps) {

                List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
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
                    if (us.action.getActionId() == 203 || us.action.getActionId() == 4) {
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

            }
        });
        adapterUssdCodes.setOnItemClickListener((view, obj, position) -> {
            Tools.executeSuperAction(obj, getActivity());
            Tools.updateWeightOnClick(obj, viewModel);
        });
        adapterUssdCodes1.setOnItemClickListener((view, obj, position) -> {
            Tools.executeSuperAction(obj, getActivity());
            Tools.updateWeightOnClick(obj, viewModel);
        });
        adapterUssdCodes2.setOnItemClickListener((view, obj, position) -> {
            Tools.executeSuperAction(obj, getActivity());
            Tools.updateWeightOnClick(obj, viewModel);
        });
    }

    /**
     * method for setting the layout_no_item margin of a view.. adapted from kcoppock answer stackoverflow
     *
     * @param v the  view whose layout_no_item is to be set
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void filterAndHideActions(String mode) {
        if (mode != null) {

            viewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
                List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
                List<UssdActionWithSteps> dataCodes = new ArrayList<>();
                List<UssdActionWithSteps> mmoneyCodes = new ArrayList<>();
                for (UssdActionWithSteps us : ussdActionWithSteps) {
                    //skip loan codes
                    if (us.action.getActionId() == 203 || us.action.getActionId() == 4) {
                        continue;
                    }
                    if (!(mode.equals(us.action.getHni()))) {
                            continue;

                    }
//                    if (mode.contains("AFRICELL")) {
//                        if ((us.action.getAfricellCode() == null)) {
//                            continue;
//                        }
//                        String code = us.action.getAfricellCode();
//                        if (code != null) {
//                            if (code.contains("not")) continue;
//                        }
//                    }
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

            });
        }

    }

    //************************  UTILITYMETHODS *******************************************************************************


    /**
     * methods to remove cards with codes that are not supported on mtn
     */
    private void hideNonMtnAction() {
        List<RecyclerView> recyclerViews = Arrays.asList(airtimeRecyclerView,
            dataRecyclerView, mmRecyclerView);
        for (RecyclerView l : recyclerViews) {
            ArrayList<View> hiddenViews = getViewsByTag(l, "hide");
            for (View v : hiddenViews) v.setVisibility(View.GONE);

        }
    }

    //adopted from stack overflow https://stackoverflow.com/questions/8817377/android-how-to-find-multiple-views-with-common-attribute
    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout_no_item for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        //setup airtime
        airtimeRecyclerView = root.findViewById(R.id.airtimeRecylerView);
        airtimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        airtimeRecyclerView.setAdapter(adapterUssdCodes);

        //setup data
        dataRecyclerView = root.findViewById(R.id.dataRecylerView);
        dataRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        dataRecyclerView.setAdapter(adapterUssdCodes1);
        //setup mobile money
        mmRecyclerView = root.findViewById(R.id.mmoneyRecylerView);
        mmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mmRecyclerView.setAdapter(adapterUssdCodes2);

//*****************************************SIMCARDS IN PHONE*********************************************************
        //initialize chip views
        final LinearLayout rootLinearLayoutChips = root.findViewById(R.id.linearLayout_root_chips);
        com.robertlevonyan.views.chip.Chip chipAirtel = root.findViewById(R.id.chip_airel);
        com.robertlevonyan.views.chip.Chip chipMtn = root.findViewById(R.id.chip_mtn);
        com.robertlevonyan.views.chip.Chip chipAfricell = root.findViewById(R.id.chip_africell);
        List<Chip> chips = Arrays.asList(chipAirtel, chipAfricell, chipMtn);

//        //set click listeners on chips
        for (final com.robertlevonyan.views.chip.Chip chip : chips) {
            chip.setOnSelectClickListener((v, selected) -> {
                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);
                    }
                }
            });
            chip.setOnClickListener(v -> {
                ((Chip) v).setChipSelected(true);

                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);
                    }
                }
            });
        }
        //make the first chip selected
        //chipAirtel.setChipSelected(true);
        //if the api level is greaterthat 22 ie lollipop, get simcards inside phone
        List<SimCard> availableSimCards = Tools.getAvailableSimCards(getContext());

        for (SimCard simCard : availableSimCards) {
            final Chip chip1 = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
            String networkName = simCard.getNetworkName();
            chip1.setText(simCard.getNetworkName());
            if (networkName.contains("MTN")) {
                chip1.setChipIcon(getResources().getDrawable(R.drawable.mtn));
//                hideNonMtnAction();
            }
            if (networkName.contains("AFRICELL"))
                chip1.setChipIcon(getResources().getDrawable(R.drawable.africell));
            if (simCard.getSlotIndex() == 0) {
                chip1.setChipSelected(true);
            }
            chip1.setOnSelectClickListener((v, selected) -> {
                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);
                        Tools.setSelectedSimcard(getActivity(), simCard.getSlotIndex());
                    }
                }
                //if the selected mode is mtn remove non mtn action cards
                filterAndHideActions(simCard.getHni());
                Toast.makeText(getActivity(), "You have changed to " + simCard.getNetworkName() + " codes", Toast.LENGTH_SHORT).show();

            });
            chip1.setOnClickListener(v -> {
                //mark the current chip as selected
                ((Chip) v).setChipSelected(true);
                //mark all the others as deselected
                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);
                        Tools.setSelectedSimcard(getActivity(), simCard.getSlotIndex());
                    }
                }
                //if the selected mode is mtn remove non mtn action cards
                filterAndHideActions(simCard.getHni());
                //notify the user of their action
                Toast.makeText(getActivity(), "You have changed to " + simCard.getNetworkName() + " codes", Toast.LENGTH_SHORT).show();

            });
            //add detected chips
            rootLinearLayoutChips.addView(chip1);
            setMargins(chip1, 0, 0, 15, 0);


            //hide default chips
            for (Chip chip : chips) chip.setVisibility(View.GONE);

        }
        //filter out actions that dont apply for the currently selected network
        filterAndHideActions(Tools.getSelectedSimCard(getActivity()).getHni());

        //add this fragment as alifecycle owner so that its lifecycle is observed for lifecycle changes
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifeCycleListener(getActivity()));
        return root;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

