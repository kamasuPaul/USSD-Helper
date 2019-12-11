package com.quickCodes.quickCodes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import androidx.room.util.StringUtil;

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

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.ui.main.PlaceholderFragment;
import com.quickCodes.quickCodes.util.SQLiteDatabaseHandler;
import com.google.android.material.button.MaterialButton;
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

        db = new SQLiteDatabaseHandler(this);

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

        Step[] steps = new Step[count];
        for(int i=0;i<count;i++){
            final View row = parentlayout.getChildAt(i);
            EditText editText = row.findViewById(R.id.number_edit_text);
            Spinner spinner1 = row.findViewById(R.id.type_spinner);
            Log.d("DATA",editText.getText().toString()+spinner1.getSelectedItem().toString());
            Step step =  new Step(1,spinner1.getSelectedItem().toString(),editText.getText().toString(),1);
            steps[i] = step;
        }

        //insert the data into the database
        String code = actionCode.getText().toString().replaceAll("#","");

        UssdAction ussdAction = new UssdAction(lastId++,actionName.getText().toString(), code,actionNetwork.getText().toString(),steps);
        db.addUssdAction(ussdAction);

        //for now update the ui from here
        PlaceholderFragment.ussdActions.add(ussdAction);
        PlaceholderFragment.mAdapter.notifyDataSetChanged();

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
