package com.quickCodes.quickCodes.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;
import static com.quickCodes.quickCodes.modals.Constants.SEC_USER_DIALED;

public class AutoDetectedFragment extends Fragment {
    ArrayList<String> codes = new ArrayList<>();
    UssdActionsViewModel ussdActionsViewModel;
    private RecyclerView recyclerView;
    private AdapterDialer mAdapter;
    View root;

    public AutoDetectedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new AdapterDialer(getActivity());

        ussdActionsViewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
            List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();

            for (UssdActionWithSteps us : ussdActionWithSteps) {
                if (us.action.getSection() == SEC_USER_DIALED) {
                    codes.add(us.action.getCode());
                    airtimeCodes.add(us);
                }
            }
            mAdapter.setUssdActions(airtimeCodes);
            if (airtimeCodes.isEmpty()) {
                if (root != null) {
                    root.findViewById(R.id.Relative_no_item).setVisibility(View.VISIBLE);
                }
            } else {
                if (root != null) {
                    root.findViewById(R.id.Relative_no_item).setVisibility(View.GONE);
                }
            }

        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_auto_saved_codes, container, false);

        recyclerView = root.findViewById(R.id.matched_items_recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                //TODO first look into custom codes
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

            }
        });
        //big call image button
        ((ImageView) (root.findViewById(R.id.image_call))).setOnClickListener(view -> {
            MainActivity.openDialer(getActivity());
        });

        //if theire are no autosaved ussd codes show the the no item view
        if (codes.isEmpty()) {
            if (root != null) {
                root.findViewById(R.id.Relative_no_item).setVisibility(View.VISIBLE);
            }
        } else {
            if (root != null) {
                root.findViewById(R.id.Relative_no_item).setVisibility(View.GONE);
            }
        }

        return root;
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
                        break;
                }
                return false;
            }
        });
        //show the menu
        popupMenu.show();
    }





}

