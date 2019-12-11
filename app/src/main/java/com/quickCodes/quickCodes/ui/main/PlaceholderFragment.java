package com.quickCodes.quickCodes.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterGridCustomCodes;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.util.SQLiteDatabaseHandler;
import com.google.android.material.snackbar.Snackbar;
//import com.hover.sdk.api.HoverParameters;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int CONTACT_PICKER_REQUEST = 29;

    private PageViewModel pageViewModel;
    SQLiteDatabaseHandler db;
    public static AdapterGridCustomCodes mAdapter;
    private RecyclerView recyclerView;
    public static List<UssdAction>  ussdActions;
    private EditText phoneNumber;


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
        View root = inflater.inflate(R.layout.fragment_custom_codes, container, false);

        db = new SQLiteDatabaseHandler(getActivity());

        // list all usdActions
        ussdActions = db.allUssdActions();

        if (ussdActions != null) {
            String[] itemsNames = new String[ussdActions.size()];

            for (int i = 0; i < ussdActions.size(); i++) {
                itemsNames[i] = ussdActions.get(i).toString();
            }
                initComponent(root);

        }

        return root;
    }
    public void createDialog(final UssdAction ussdAction, final String cd) {
        if (ussdAction.getSteps() == null || ussdAction.getSteps().length == 0) {
            //execute the code immediately
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cd)));


        } else {


            final Dialog customDialog;
            //inflate the root dialog
            LayoutInflater inflater = getLayoutInflater();
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.dialog_root, null);
            LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
            //else check for steps and construct the layout
            for (Step step : ussdAction.getSteps()) {
                if (step.getType().equals("Text")) {

                    View rowText = inflater.inflate(R.layout.row_text, null);
                    rowText.setId(step.getId());
                    final EditText editText = rowText.findViewById(R.id.editText_text);
                    editText.setHint(step.getDescription());
                    root.addView(rowText);


                }
                if (step.getType().equals("Tel No")) {
                    View rowTelephone = inflater.inflate(R.layout.row_telephone, null);
                    ImageButton imageButton = rowTelephone.findViewById(R.id.selec_contact_ImageBtn);
                    final EditText editText = rowTelephone.findViewById(R.id.edit_text_mobileNumber);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO pick contact
                            phoneNumber = editText;
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        }
                    });

                    rowTelephone.setId(step.getId());
                    root.addView(rowTelephone);


                }
                if (step.getType().equals("Number")) {
                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
                    rowAmount.setId(step.getId());
                    final EditText editText = rowAmount.findViewById(R.id.edit_text_amount);
                    editText.setHint(step.getDescription());
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


            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(ussdAction.getCode());
                    //get all the user entered values
                    for(Step step: ussdAction.getSteps()){
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
    private void initComponent(View root) {
        recyclerView = (RecyclerView)root. findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new MyItemDecorator(2,5));
//        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        mAdapter = new AdapterGridCustomCodes(getActivity(), ussdActions);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridCustomCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdAction obj, int position) {
                String uscode = ussdActions.get(position).getCode();
                String cd = uscode+ Uri.encode("#");
                createDialog(ussdActions.get(position),cd);
                Snackbar.make(view, "Item " + obj.getCode() + " clicked", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDelete(View view, UssdAction obj, int position) {
                ussdActions.remove(position);
                mAdapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Toast.makeText(getActivity(), "aciit callde", Toast.LENGTH_SHORT).show();
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int numberIdex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIdex);
                    if (phoneNumber != null) {
                        phoneNumber.setText(number);
                    }


                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "No contact selected", Toast.LENGTH_SHORT).show();
                System.out.println("User closed the picker without selecting items.");
            }
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String[] sessionTextArr = data.getStringArrayExtra("ussd_messages");
            String uuid = data.getStringExtra("uuid");
            Toast.makeText(getActivity(), sessionTextArr.toString(), Toast.LENGTH_LONG).show();

        } else if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        }
    }
//********************************************UTILITY METHODS ****************************
    /**
     * method for setting the layout margin of a view.. adapted from kcoppock answer stackoverflow
     *
     * @param v the  view whose layout is to be set
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

    class MyItemDecorator extends RecyclerView.ItemDecoration{
        private  int margin,columns;
        public MyItemDecorator(int columns,int margin) {
            this.columns = columns;
            this.margin = margin;

        }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position <columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if(position%columns==0){
                outRect.left = margin;
            }
        }
        }
    }


