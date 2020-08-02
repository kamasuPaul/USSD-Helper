package com.quickCodes.quickCodes.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.cardview.widget.CardView;

import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

public class Tools {
    private static final String TOOLS_PREF = "tools_pref";
    private static final String SELECTED_SIMCARD = "selected_simcard";
    public static final int CONTACT_PICKER_REQUEST = 29;
    public static EditText phoneNumber;
    private static String TAG = "TOOLS";
    private static SubscriptionManager subscriptionManager;
    private static List<SubscriptionInfo> subList;
    public static HashMap<String, String> contacts;


    @SuppressLint("MissingPermission")
    public static List<SimCard> getAvailableSimCards(Context context) {
        List<SimCard> simCards = new ArrayList<>();

        //TODO,seperate slom with same mnc with slot number

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            subList = subscriptionManager.getActiveSubscriptionInfoList();

            for (final SubscriptionInfo subscriptionInfo : subList) {
                final String networkName = subscriptionInfo.getCarrierName().toString().toUpperCase();
                String hnc = String.valueOf(subscriptionInfo.getMcc()) + subscriptionInfo.getMnc();
                int slotIndex = subscriptionInfo.getSimSlotIndex();
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                SimCard simCard = new SimCard(networkName, hnc, slotIndex, subscriptionId);
                simCards.add(simCard);
            }
        }
        return simCards;
    }

    public static SimCard getSelectedSimCard(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TOOLS_PREF, Context.MODE_PRIVATE);
        int simslot = preferences.getInt(SELECTED_SIMCARD, -1);
        List<SimCard> availableSimCards = getAvailableSimCards(context);
        for (SimCard simCard : availableSimCards) {
            if (simCard.getSlotIndex() == simslot) {
                return simCard;
            }
        }
        //if they dont match it returns first simcard in phone
        return availableSimCards.get(0);
    }

    public static void setSelectedSimcard(Context context, int simslot) {
        SharedPreferences preferences = context.getSharedPreferences(TOOLS_PREF, Context.MODE_PRIVATE);
        preferences.edit().putInt(SELECTED_SIMCARD, simslot).commit();
    }

    @SuppressLint("MissingPermission")
    public static void executeUssd(String fullCode, Context context, int slot) {
        List<PhoneAccountHandle> phoneAccountHandleList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            for (int i = 0; i < phoneAccountHandleList.size(); i++) {
                PhoneAccountHandle phoneAccountHandle = phoneAccountHandleList.get(i);
                if (i == slot) {
                    Uri uri = Uri.parse("tel:" + fullCode);
                    Bundle extras = new Bundle();
                    extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
                    telecomManager.placeCall(uri, extras);
                    break;//break out of the loop
                }
            }

        } else {
            //use normal way of dialing ussd code,because their is not an easy way of getting user selected simcard
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));
        }
    }

    public static void executeSuperAction(UssdActionWithSteps ussdActionWithSteps, Activity context) {
        //use codes for the currently selected simcard
        SimCard selectedSimCard = getSelectedSimCard((context));
        String mode1 = selectedSimCard.getNetworkName();
        UssdAction ussdAction = ussdActionWithSteps.action;
        String code = "";
        if (mode1.contains("MTN")) code = ussdAction.getMtnCode();
        if (mode1.contains("AIRTEL")) code = ussdAction.getAirtelCode();
        if (mode1.contains("AFRICELL")) code = ussdAction.getAfricellCode();

        //if code is still empty ,this is a custom code ,get its code
        if (code.isEmpty() || code == null) {
            code = ussdAction.getAirtelCode();
        }


        if (ussdActionWithSteps.steps == null || ussdActionWithSteps.steps.size() == 0) {
            //no steps found,execute the code immediately
            executeUssd(code + Uri.encode("#"), context, selectedSimCard.getSlotIndex());
        } else {
            final Dialog customDialog;
            //inflate the root dialog
            LayoutInflater inflater = context.getLayoutInflater();
            CardView cardView = (CardView) context.getLayoutInflater().inflate(R.layout.dialog_root, null);
            LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
            //else check for steps and construct the layout_no_item
            List<Step> steps = ussdActionWithSteps.steps;
            //sort the steps
            Collections.sort(steps, (step1, step2) -> ((Integer) step1.getWeight()).compareTo(step2.getWeight()));
            for (Step step : steps) {
                Toast.makeText(context, "Step" + step.getType() + "::" + step.getStepsAfter(), Toast.LENGTH_SHORT).show();
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
                            context.startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        }
                    });
                    rowTelephone.setId((int) step.getStepId());
                    root.addView(rowTelephone);
                }
                if (step.getType() == NUMBER) {
//                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
//                    rowAmount.setId((int) step.getStepId());
//                    final EditText editText = rowAmount.findViewById(R.id.edit_text_amount);
//                    editText.setHint(step.getDescription());
//                    root.addView(rowAmount);
                    View rowAmount = inflater.inflate(R.layout.row_dropdown, null);
                    rowAmount.setId((int) step.getStepId());
                    String[] data = {"kamasu", "paul"};
                    ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(
                            context,
                            R.layout.dropdown_menu_popup_item,
                            data);

                    AutoCompleteTextView editTextFilledExposedDropdown =
                        rowAmount.findViewById(R.id.action_network);
                    editTextFilledExposedDropdown.setKeyListener(null);
                    editTextFilledExposedDropdown.setAdapter(adapter);
                    root.addView(rowAmount);


                }
            }

            //inflate each row that should be contained in the dialog box
            View rowButtons = inflater.inflate(R.layout.row_buttons, null);
            //add each row to the root
            root.addView(rowButtons);
            cardView.setPadding(5, 5, 5, 5);

            customDialog = new Dialog(context);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            customDialog.setContentView(cardView);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.setCancelable(true);

            final UssdActionWithSteps finalUssdAction = ussdActionWithSteps;
            String finalCode = code;
            Log.d(TAG, finalCode);
            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(finalCode);
                    //get all the user entered values
                    for (Step step : finalUssdAction.steps) {
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById((int) step.getStepId());
                        String value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        if (value != null && !value.isEmpty()) {
                            stringBuilder.append("*" + value);
                            //add additional steps that must immediately follow this step
                            // ,before other independent steps are appended to the final code

                            String stepsAfter = step.getStepsAfter();
                            String[] split = stepsAfter.split(",");
                            for (String s : split
                            ) {
                                stringBuilder.append("*" + s);
                                Log.d(TAG, s);
                            }
                        }
                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString() + Uri.encode("#");
                    Log.d(TAG, fullCode);

                    customDialog.dismiss();
                    //execute the ussd code
                    executeUssd(fullCode, context, selectedSimCard.getSlotIndex());
                    Toast.makeText(context, "code: " + fullCode, Toast.LENGTH_SHORT).show();

                }
            });

            ((Button) customDialog.findViewById(R.id.bt_cancel)).setOnClickListener(v -> customDialog.dismiss());
            customDialog.show();
        }
    }

    public static void setTelephone(String text) {
        phoneNumber.setText(text);
    }

    public static void updateWeightOnClick(UssdActionWithSteps obj, UssdActionsViewModel viewModel) {
        obj.action.setWeight(obj.action.getWeight() + 1);
        viewModel.update(obj);
    }

    public static HashMap<String, String> getContacts() {
        return contacts;
    }

    public static void setContacts(HashMap<String, String> contacts1) {
        contacts = contacts1;
    }

    public static boolean showMeOverlay(Context context) {
        boolean b = PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(context.getResources().getString(R.string.quick_access_dots_pref), false);
        return b;
    }
}
