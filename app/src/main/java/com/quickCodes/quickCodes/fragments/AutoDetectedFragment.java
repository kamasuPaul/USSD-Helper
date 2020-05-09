package com.quickCodes.quickCodes.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.quickCodes.quickCodes.fragments.MainFragment.simcardsSlots;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_USER_DIALED;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

public class AutoDetectedFragment extends Fragment {
    private static final int CONTACT_PICKER_REQUEST = 200;

    ArrayList<String> codes = new ArrayList<>();
    UssdActionsViewModel ussdActionsViewModel;
    Dialog dialog;
    private RecyclerView recyclerView;
    private AdapterDialer mAdapter;
    private EditText phoneNumber;


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
                    codes.add(us.action.getAirtelCode());
                    airtimeCodes.add(us);
                }
            }
            mAdapter.setUssdActions(airtimeCodes);

        });

        setUpDialog();

    }

    private void setUpDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_enter_number_and_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auto_saved_codes, container, false);

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
                String initialCode = obj.action.getAirtelCode();
                String cd = initialCode + Uri.encode("#");
                createDialog(obj);
            }

            @Override
            public void onItemDelete(View view, UssdActionWithSteps obj, int position) {
                ussdActionsViewModel.delete(obj);
            }

            @Override
            public void onItemEdit(View view, UssdActionWithSteps obj, int position) {

            }

            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {
                createOptionsMenu(v, ussdActionWithSteps, position);
            }
        });
        return root;
    }

    public void createOptionsMenu(final View v, final UssdActionWithSteps p, final int position) {
        //inflate options menu
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        //inflate the menu from layout resource file
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
                        i.putExtra("section", p.action.getSection());
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

    public void createDialog(UssdActionWithSteps ussdActionWithSteps) {

        String uscode1 = ussdActionWithSteps.action.getAirtelCode();//airtel code is default code

        //check for phone number clicks
        if (!uscode1.contains("*")) {//if it doesnt contain a * its aphone number, exececute it immediately
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + uscode1)));
            return;
        }

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

    @SuppressLint("MissingPermission")
    public void executeUssd(String fullCode, UssdAction action) {
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

}

