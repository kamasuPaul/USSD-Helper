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
import com.quickCodes.quickCodes.util.SQLiteDatabaseHandler;
import com.quickCodes.quickCodes.util.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

//import androidx.room.util.StringUtil;
//import com.hover.sdk.api.HoverParameters;


public class AddYourOwnActionActivity extends AppCompatActivity {

    SQLiteDatabaseHandler db;
    MaterialButton button;
    EditText actionName,actionCode;
    AutoCompleteTextView actionNetwork;
//    Spinner spinner;
    int lastId =6;
    LinearLayout parentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_your_own_action);
        setupToolBar();
        actionName =  findViewById(R.id.action_name);
        actionCode =  findViewById(R.id.action_code);
        actionNetwork =  findViewById(R.id.action_network);
//        spinner = findViewById(R.id.type_spinner);

        parentlayout = findViewById(R.id.layout_parent);

        button =  findViewById(R.id.add_new_action);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               addNewAction();
           }
       });

//        db = new SQLiteDatabaseHandler(this);

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

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add custom codes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backbutton);
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

        List<Step>steps = new ArrayList<>();
        for(int i=0;i<count;i++){

            final View row = parentlayout.getChildAt(i);
            EditText editText = row.findViewById(R.id.number_edit_text);
            Spinner stepTypeSpinner = row.findViewById(R.id.type_spinner);

            String t = stepTypeSpinner.getSelectedItem().toString();
            int type = TEXT;
            if(t.equalsIgnoreCase("Text"))  type = TEXT;
            if(t.equalsIgnoreCase("Number"))  type = NUMBER;
            if(t.equalsIgnoreCase("Tel No"))  type = TELEPHONE;
            int weight = i;

            String des = editText.getText().toString();
            String defaultValue = "";
            Step step =  new Step(codeId,type,weight,des,defaultValue);

            steps.add(step);
        }

        //insert the data into the database
        String code = actionCode.getText().toString().replaceAll("#","");
        //TODO check if the code starts with a *

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

        UssdActionsViewModel v = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        UssdAction ussdAction = new UssdAction(codeId, actionNameText, code,code,code, SEC_CUSTOM_CODES);
        v.insert(ussdAction,steps);
//        db.addUssdAction(ussdAction);

        //for now update the ui from here
//        PlaceholderFragment.ussdActions.add(ussdAction);
//        PlaceholderFragment.mAdapter.notifyDataSetChanged();

        Toast.makeText(this, ussdAction.getName()+" Has been added", Toast.LENGTH_SHORT).show();
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
}
