package com.quickCodes.quickCodes.ui.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterGridCustomCodes;
import com.quickCodes.quickCodes.fragments.MainFragment;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.quickCodes.quickCodes.fragments.MainFragment.simcardsSlots;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;
import static com.quickCodes.quickCodes.modals.Constants.SEC_USER_DIALED;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

//import com.hover.sdk.api.HoverParameters;

/**
 * A placeholder fragment containing a simple view.
 */
public class CustomCodesFragment extends Fragment {

    UssdActionsViewModel ussdActionsViewModel;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int CONTACT_PICKER_REQUEST = 29;
    private static final String sharedPrefString = "first";

    private PageViewModel pageViewModel;
    public static AdapterGridCustomCodes mAdapter;
    private RecyclerView recyclerView;
    public static List<UssdAction> ussdActions;
    private EditText phoneNumber;


    public static CustomCodesFragment newInstance(int index) {
        CustomCodesFragment fragment = new CustomCodesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new AdapterGridCustomCodes(getActivity());

        ussdActionsViewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(this, new Observer<List<UssdActionWithSteps>>() {
            @Override
            public void onChanged(List<UssdActionWithSteps> ussdActionWithSteps) {
                List<UssdActionWithSteps> customCodes = new ArrayList<>();
                for (UssdActionWithSteps us : ussdActionWithSteps) {
                    if (us.action.getSection() == SEC_CUSTOM_CODES ||us.action.getSection() == SEC_USER_DIALED) {
                        customCodes.add(us);
                    }
                }
                mAdapter.setCustomActions(customCodes);
            }
        });

        SharedPreferences prefs = getActivity().getSharedPreferences(sharedPrefString, Context.MODE_PRIVATE);
        if (!prefs.contains("first")) {
            //TODO add ussd action to db;as custom action
            //UssdAction ussdAction = new UssdAction(3434, "Airtime Balance", "*131","Airtel",);

            prefs.edit().putBoolean("first", true)
                .commit();
        }
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_custom_codes, container, false);

        if (ussdActions != null) {
            String[] itemsNames = new String[ussdActions.size()];

            for (int i = 0; i < ussdActions.size(); i++) {
                itemsNames[i] = ussdActions.get(i).toString();
            }

        }
        initComponent(root);

        return root;
    }

    public void createDialog(UssdActionWithSteps ussdActionWithSteps) {

        //use codes for the selected network mode
//        UssdAction ussdAction = null;
//        String mode1 = "AIRTEL";//TODO get mode in a better way
//        UssdAction ussdAction = ussdActionWithSteps.action;
//        String code = "";
        String uscode1 = ussdActionWithSteps.action.getAirtelCode();//airtel code is default code
        UssdAction action = ussdActionWithSteps.action;

        if (action.getAirtelCode() != null) {
            if (!action.getAirtelCode().isEmpty()) {
                uscode1 = action.getAirtelCode();
            }
        }
        if (action.getMtnCode() != null) {
            if (!action.getMtnCode().isEmpty()) {
                uscode1 = action.getMtnCode();
            }
        }
        if (action.getAfricellCode() != null) {
            if (!action.getAfricellCode().isEmpty()) {
                uscode1 = action.getAfricellCode();
            }
        }
//        if (!action.getMtnCode().isEmpty()) {
//            uscode1 = action.getMtnCode();
//        }
//        if (!action.getAirtelCode().isEmpty()) {
//            uscode1 = action.getAirtelCode();
//        }
//        if (!action.getAfricellCode().isEmpty()) {
//            uscode1 = action.getAfricellCode();
//
//        }
        final String uscode = uscode1;

        if (ussdActionWithSteps.steps == null || ussdActionWithSteps.steps.size() == 0) {
            //execute the code immediately
            //TODO execute code with selected simcard instead for prompting the user to select sim
            String code = uscode1 + Uri.encode("#");
            executeUssd(code, ussdActionWithSteps.action);


        } else {

            final Dialog customDialog;
            //inflate the root dialog
            LayoutInflater inflater = getLayoutInflater();
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.dialog_root, null);
            LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
            //else check for steps and construct the layout
            for (Step step : ussdActionWithSteps.steps) {
                if (step.getType() == TEXT) {

                    View rowText = inflater.inflate(R.layout.row_text, null);
                    rowText.setId((int) step.getStepId());
                    final EditText editText = rowText.findViewById(R.id.editText_text);
                    editText.setHint(step.getDescription());
                    root.addView(rowText);


                }
                if (step.getType() == TELEPHONE) {
                    View rowTelephone = inflater.inflate(R.layout.row_telephone, null);

                    ImageButton imageButton = rowTelephone.findViewById(R.id.selec_contact_ImageBtn);
                    final EditText editText = rowTelephone.findViewById(R.id.edit_text_mobileNumber);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            phoneNumber = editText;
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        }
                    });

                    rowTelephone.setId((int) step.getStepId());
                    root.addView(rowTelephone);


                }
                if (step.getType() == NUMBER) {
                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
                    rowAmount.setId((int) step.getStepId());
                    final EditText editText = rowAmount.findViewById(R.id.edit_text_amount);
                    editText.setHint(step.getDescription());
                    root.addView(rowAmount);

                }

            }

            //inflate each row that should be contained in the dialog box
            View rowButtons = inflater.inflate(R.layout.row_buttons, null);
            //add each row to the root
            root.addView(rowButtons);
            cardView.setPadding(5, 5, 5, 5);

            customDialog = new Dialog(getActivity());
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            customDialog.setContentView(cardView);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.setCancelable(true);


            final UssdActionWithSteps finalUssdAction = ussdActionWithSteps;
            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(uscode);
                    //get all the user entered values
                    for (Step step : finalUssdAction.steps) {
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById((int) step.getStepId());

                        String value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        if (value != null && !value.isEmpty()) {
                            stringBuilder.append("*" + value);
                        }

                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString() + Uri.encode("#");
                    customDialog.dismiss();
                    //execute the ussd
                    executeUssd(fullCode, ussdActionWithSteps.action);
//                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));


                }


            });

            ((Button) customDialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });
            customDialog.show();
        }
    }

    private void initComponent(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new MyItemDecorator(2, 5));
//        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridCustomCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {

                //TODO first look into custom codes
                String initialCode = obj.action.getAirtelCode();
                String cd = initialCode + Uri.encode("#");
                createDialog(obj);
                MainFragment.updateWeightOnClick(obj, ussdActionsViewModel);
            }

            @Override
            public void onItemDelete(View view, UssdActionWithSteps obj, int position) {
                ussdActionsViewModel.delete(obj);
            }

            @Override
            public void onItemEdit(View view, UssdActionWithSteps obj, int position) {
                Intent i = new Intent(getActivity(), EditActionActivity.class);
                i.putExtra("action_id", String.valueOf(obj.action.getActionId()));
                startActivity(i);
            }
        });

    }

    @SuppressLint("MissingPermission")
    public  void executeUssd(String fullCode, UssdAction action) {
        String hnc = action.getNetwork();
        String simcardSlot = simcardsSlots.get(hnc);

        TelecomManager telecomManager = null;
        List<PhoneAccountHandle> phoneAccountHandleList = null;
        // if the users phone is android is  Marshmallow or above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (simcardsSlots.containsKey(hnc)) {//if the network choosen by user when saving code is still available in simcard
                //TODO if api level is greater than 26 do background codes,do this later,not important right now
                telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
                phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                for (int i = 0; i < phoneAccountHandleList.size(); i++) {
                    PhoneAccountHandle phoneAccountHandle = phoneAccountHandleList.get(i);
                    if (i == Integer.valueOf(simcardSlot)) {
                        Uri uri = Uri.parse("tel:" + fullCode);
                        Bundle extras = new Bundle();
                        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
                        telecomManager.placeCall(uri, extras);
                        break;//break out of the loop
                    }
                }
            } else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

            }

        } else {
            //use normal way of dialing ussd code,because their is not an easy way of getting user selected simcard
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int numberIdex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIdex);
                    if (phoneNumber != null) {
                        if (number.startsWith("+256")) {
                            number = number.replace("+256", "0");
                        }
                        number = number.replace(" ", "");
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

    public static class MyItemDecorator extends RecyclerView.ItemDecoration {
        private int margin, columns;

        public MyItemDecorator(int columns, int margin) {
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
            if (position < columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if (position % columns == 0) {
                outRect.left = margin;
            }
        }
    }
}


