package com.example.ussdhelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ussdhelper.modals.UssdAction;
import com.example.ussdhelper.modals.UssdAction.Step;
import com.example.ussdhelper.util.SQLiteDatabaseHandler;
import com.hover.sdk.api.HoverParameters;

import java.util.List;

public class AddYourOwnActionActivity extends AppCompatActivity {

    SQLiteDatabaseHandler db;
    Button button;
    EditText actionName,actionCode,actionNetwork;
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

    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Custome codes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            Step step =  new UssdAction.Step(1,spinner1.getSelectedItem().toString(),editText.getText().toString(),1);
            steps[i] = step;
        }
        //insert the data into the database
                UssdAction ussdAction = new UssdAction(lastId++,actionName.getText().toString(),actionCode.getText().toString(),actionNetwork.getText().toString(),steps);
        db.addUssdAction(ussdAction);

        Toast.makeText(this, ussdAction.toString(), Toast.LENGTH_SHORT).show();

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
