package com.quickCodes.quickCodes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.quickCodes.quickCodes.modals.CustomAction;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.ui.main.PlaceholderFragment;
import com.quickCodes.quickCodes.util.CustomActionsViewModel;
import com.quickCodes.quickCodes.util.SQLiteDatabaseHandler;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

public class EditActionActivity extends AppCompatActivity {
    SQLiteDatabaseHandler db;
    MaterialButton button;
    EditText actionName,actionCode;
    AutoCompleteTextView actionNetwork;
        LinearLayout parentlayout;
    CustomAction lastAction;

    String action_id;

    CustomActionsViewModel customActionsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_your_own_action);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        customActionsViewModel = ViewModelProviders.of(this).get(CustomActionsViewModel.class);

        action_id = getIntent().getStringExtra("action_id");
        lastAction = customActionsViewModel.getAction(action_id);


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

        actionName.setText(lastAction.getName());
        actionNetwork.setText(lastAction.getName());
        actionCode.setText(lastAction.getCode());



        //**********************MATERIAL SPINNER OR DROP DOWNN ************************************
        String[] COUNTRIES = new String[] {"Airtel", "Mtn", "Africell"};

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                COUNTRIES);

        AutoCompleteTextView editTextFilledExposedDropdown =
            findViewById(R.id.action_network);
        editTextFilledExposedDropdown.setAdapter(adapter);
        //**************************************************************************************//


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

        Random r  = new Random();
        Long codeId = r.nextLong();

        Step[] steps = new Step[count];
        for(int i=0;i<count;i++){
            final View row = parentlayout.getChildAt(i);
            EditText editText = row.findViewById(R.id.number_edit_text);
            Spinner spinner1 = row.findViewById(R.id.type_spinner);
            Log.d("DATA",editText.getText().toString()+spinner1.getSelectedItem().toString());
//            Step step =  new Step(1,spinner1.getSelectedItem().toString(),editText.getText().toString(),1);
//            Step step =  new Step(1,1,1,1,editText.getText().toString());
            Spinner stepTypeSpinner = row.findViewById(R.id.type_spinner);
            Log.d("DATA",editText.getText().toString()+stepTypeSpinner.getSelectedItem().toString());
            int type = Integer.valueOf(stepTypeSpinner.getSelectedItem().toString());
            int weight = 0;
            String des = editText.getText().toString();
            String defaultValue = "";
            Step step =  new Step(codeId,type,weight,des,defaultValue);
            steps[i] = step;
        }

        //insert the data into the database
        String code = actionCode.getText().toString().replaceAll("#","");
        //check if the code is not empty
        //check if the name is not empty
        String actionNameText = actionName.getText().toString();

        if(actionNameText.isEmpty()){
            Toast.makeText(this, "A name is required to save", Toast.LENGTH_SHORT).show();
            return;
        }
        if(code.isEmpty()){
            Toast.makeText(this, "A code is required to save", Toast.LENGTH_SHORT).show();
            return;
        }

        customActionsViewModel.update(new CustomAction(Integer.valueOf(action_id ),actionNameText,code));
        //for now update the ui from here
//        PlaceholderFragment.ussdActions.add(ussdAction);
        //TODO add instant ui refresh after adding or editing an action
        PlaceholderFragment.mAdapter.notifyDataSetChanged();
        Intent t = new Intent(this,MainActivity.class);
        t.putExtra("edit","edit");
        startActivity(t);
    }

    public void onDelete(View view) {
        parentlayout.removeView((View) view.getParent());
    }

    public void onAddField(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.field,null);
        parentlayout.addView(rowView,parentlayout.getChildCount()-1);

    }
}
