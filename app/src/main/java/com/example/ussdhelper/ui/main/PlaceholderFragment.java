package com.example.ussdhelper.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ussdhelper.MainActivity;
import com.example.ussdhelper.R;
import com.example.ussdhelper.modals.UssdAction;
import com.example.ussdhelper.util.SQLiteDatabaseHandler;
import com.hover.sdk.api.HoverParameters;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    SQLiteDatabaseHandler db;
    ListView list;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blank, container, false);

        db = new SQLiteDatabaseHandler(getActivity());

        // list all usdActions
        final List<UssdAction> ussdActions = db.allUssdActions();

        if (ussdActions != null) {
            String[] itemsNames = new String[ussdActions.size()];

            for (int i = 0; i < ussdActions.size(); i++) {
                itemsNames[i] = ussdActions.get(i).toString();
            }

            // display like string instances
             list = (ListView) root.findViewById(R.id.list);
            list.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String ussdCode = "*131"+ Uri.encode("#");
                    String uscode = ussdActions.get(position).getCode();
                    String cd = uscode+ Uri.encode("#");
                    createDialog(ussdActions.get(position),cd);
//                    String code = uscode.substring(uscode.lastIndexOf("#"));
//                    Toast.makeText(AddYourOwnActionActivity.this,uscode, Toast.LENGTH_SHORT).show();
                }
            });

        }

        return root;
    }
    public void createDialog(final UssdAction ussdAction, final String cd) {
        if (ussdAction.getSteps() == null || ussdAction.getSteps().length == 0) {
            //execute the code immediately
            Toast.makeText(getActivity(), "No steps Found",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cd)));


        } else {


            final Dialog customDialog;
            //inflate the root dialog
            LayoutInflater inflater = getLayoutInflater();
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.dialog_root, null);
            LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
            //else check for steps and construct the layout
            for (UssdAction.Step step : ussdAction.getSteps()) {
                if (step.getType().equals("Text")) {

                    View rowText = inflater.inflate(R.layout.row_text, null);
                    rowText.setId(step.getId());
                    root.addView(rowText);


                }
                if (step.getType().equals("Tel No")) {
                    View rowTelephone = inflater.inflate(R.layout.row_telephone, null);
                        rowTelephone.setId(step.getId());
                    root.addView(rowTelephone);


                }
                if (step.getType().equals("Number")) {
                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
                    rowAmount.setId(step.getId());
                    root.addView(rowAmount);

                }

            }

            //inflate each row that should be contained in the dialog box
            View rowButtons = inflater.inflate(R.layout.row_buttons, null);
            //add each row to the root
            root.addView(rowButtons);

            customDialog = new Dialog(getActivity());
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            customDialog.setContentView(cardView);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            customDialog.setCancelable(true);


//        final EditText editTextAmount = customDialog.findViewById(R.id.edit_text_amount);
//        final EditText editTextNumber = customDialog.findViewById(R.id.edit_text_mobileNumber);
//        ImageButton imageButton = customDialog.findViewById(R.id.selec_contact_ImageBtn);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity)getActivity()).contactPicker(getActivity());
//            }
//        });


            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(ussdAction.getCode());
                    //get all the user entered values
                    for(UssdAction.Step step: ussdAction.getSteps()){
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById(step.getId());

                        String value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        stringBuilder.append("*"+value);

                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString()+ Uri.encode("#");

                    customDialog.dismiss();
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

                }


            });

            ((Button) customDialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });
            customDialog.show();
//            return customDialog;
        }
    }
}
