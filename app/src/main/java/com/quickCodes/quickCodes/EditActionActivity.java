package com.quickCodes.quickCodes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import static com.quickCodes.quickCodes.AddYourOwnActionActivity.containsIgnoreCase;
import static com.quickCodes.quickCodes.fragments.MainFragment.simcards;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

public class EditActionActivity extends AppCompatActivity {
    MaterialButton button;
    EditText actionName,actionCode;
    AutoCompleteTextView actionNetwork;
        LinearLayout parentlayout;
    UssdActionWithSteps lastAction;

    String action_id;

    UssdActionsViewModel ussdActionsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_your_own_action);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ussdActionsViewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);

        action_id = getIntent().getStringExtra("action_id");
        lastAction = ussdActionsViewModel.getussdActionWithSteps(action_id);


        actionName =  findViewById(R.id.action_name);
        actionCode =  findViewById(R.id.action_code);
        actionNetwork =  findViewById(R.id.action_network);


//        spinner = findViewById(R.id.type_spinner);

        parentlayout = findViewById(R.id.layout_parent);

        button =  findViewById(R.id.add_new_action);
        button.setText("Save");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewAction();
            }
        });

        actionName.setText(lastAction.action.getName());
        //retrieve the selected network if the simcard is still inside the phone orthiwe ignore it
        if(simcards.containsValue(lastAction.action.getNetwork())){
            String networkName = getKey(simcards, lastAction.action.getNetwork());
            actionNetwork.setText(networkName.toUpperCase());
        }else{
            Toast.makeText(this, lastAction.action.getNetwork(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, simcards.toString(), Toast.LENGTH_SHORT).show();
        }
        actionCode.setText(lastAction.action.getAirtelCode());


        //**********************MATERIAL SPINNER OR DROP DOWNN ************************************
        //get available networks from main activity
        for (Map.Entry<String, String> entry : simcards.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

        }

        //add all networks to the array
        String[] networks = simcards.keySet().toArray(new String[simcards.keySet().size()]);

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                networks);

        AutoCompleteTextView editTextFilledExposedDropdown =
            findViewById(R.id.action_network);
        editTextFilledExposedDropdown.setKeyListener(null);
        editTextFilledExposedDropdown.setAdapter(adapter);

        //**************************************************************************************//


        //**********************ADD PREVIOUSLY ADDED STEPS IN THE ACTION**************************//
        //sort the steps according to weight and display them
        List<Step> steps = lastAction.steps;
        Collections.sort(steps, (step1, step2) -> ((Integer) step1.getWeight()).compareTo(step2.getWeight()));
        LayoutInflater inflater = getLayoutInflater();

        for(Step step: steps){
            final View rowView = inflater.inflate(R.layout.field,null);
            //set the description of the step
            ((EditText)rowView.findViewById(R.id.number_edit_text)).setText(step.getDescription());
            //set its type,text,number or telephone
            ((Spinner)rowView.findViewById(R.id.type_spinner)).setSelection(step.getType());
            parentlayout.addView(rowView,parentlayout.getChildCount());

        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void addNewAction(){
        int count = parentlayout.getChildCount();
        Long codeId = lastAction.action.getActionId();

        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            final View row = parentlayout.getChildAt(i);
            EditText editText = row.findViewById(R.id.number_edit_text);
            Spinner stepTypeSpinner = row.findViewById(R.id.type_spinner);

            String t = stepTypeSpinner.getSelectedItem().toString();

            int type = TEXT;
            if (t.equalsIgnoreCase(getString(R.string.text))) type = TEXT;
            if (t.equalsIgnoreCase(getString(R.string.number))) type = NUMBER;
            if (t.equalsIgnoreCase(getString(R.string.contact))) type = TELEPHONE;
            int weight = i;

            String des = editText.getText().toString();
            String defaultValue = "";
            Step step = new Step(codeId, type, weight, des, defaultValue);

            steps.add(step);
        }

        //insert the data into the database
        String code = actionCode.getText().toString().replaceAll("#", "");
        //TODO check if the code starts with a *

        //check if the code is not empty
        //check if the name is not empty
        String actionNameText = actionName.getText().toString();

        if (actionNameText.isEmpty()) {
            Toast.makeText(this, "A name is required to save", Toast.LENGTH_SHORT).show();
            return;
        }
        if (code.isEmpty()) {
            Toast.makeText(this, "A code is required to save", Toast.LENGTH_SHORT).show();
            return;
        }
        //get newtork
        //store the action code in the correct network
        //store the chosen network ie its MNC in on of the other networks
        String networkName = actionNetwork.getText().toString();
        String hnc = simcards.get(networkName);
        String airtelCode = "", mtnCode = "", africellCode = "";
        if (containsIgnoreCase(networkName, "MTN")) {
            mtnCode = code;
        }
        if (containsIgnoreCase(networkName, "AIRTEL")) {
            airtelCode = code;
        }
        if (containsIgnoreCase(networkName, "AFRICELL")) {
            africellCode = code;
        }
        UssdActionsViewModel v = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        UssdAction ussdAction = new UssdAction(codeId, actionNameText,airtelCode,mtnCode,africellCode, SEC_CUSTOM_CODES,hnc);
        v.update(new UssdActionWithSteps(ussdAction,steps));
        //TODO go to custom codes fragment
        Toast.makeText(this, ussdAction.getName()+" Has been Edited", Toast.LENGTH_SHORT).show();
        finish();

    }

    public void onDelete(View view) {
        parentlayout.removeView((View) view.getParent());
    }

    public void onAddField(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.field,null);
        parentlayout.addView(rowView,parentlayout.getChildCount()-1);

    }
    /*
    adopted from https://techiedelight.com/get-map-key-from-value-java/
     */
    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
