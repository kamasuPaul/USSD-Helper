package com.quickCodes.quickCodes.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.StepArrayAdapter;
import com.quickCodes.quickCodes.modals.SimCard;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.cardview.widget.CardView;

import static com.quickCodes.quickCodes.modals.Constants.CHOICE;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

public class Tools {
    public static final String HNI_AUTO_SAVED_CODES = "auto_saved";
    private static final String TOOLS_PREF = "tools_pref";
    private static final String MCC_PREF = "mcc_pref";
    private static final String SELECTED_SIMCARD = "selected_simcard";
    public static final int CONTACT_PICKER_REQUEST = 29;
    public static EditText phoneNumber;
    private static String TAG = "TOOLS";
    private static SubscriptionManager subscriptionManager;
    private static List<SubscriptionInfo> subList;
    public static HashMap<String, String> contacts;
    static List<String> parts;


    @SuppressLint("MissingPermission")
    public static List<SimCard> getAvailableSimCards(Context context) {
        List<SimCard> simCards = new ArrayList<>();
        SharedPreferences preferences = context.getSharedPreferences(TOOLS_PREF, Context.MODE_PRIVATE);

        //TODO,seperate slom with same mnc with slot number

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            subList = subscriptionManager.getActiveSubscriptionInfoList();
//            subscriptionManager.addOnSubscriptionsChangedListener(new SubscriptionManager.OnSubscriptionsChangedListener(){
//                @Override
//                public void onSubscriptionsChanged() {
//                    super.onSubscriptionsChanged();
//                    subList = subscriptionManager.getActiveSubscriptionInfoList();
//                }
//            });
            //handle null pointer excepiton
                if (subList != null) {

                    for (final SubscriptionInfo subscriptionInfo : subList) {
                        final String networkName = subscriptionInfo.getCarrierName().toString().toUpperCase();
                        String mcc = String.valueOf(subscriptionInfo.getMcc());
                        String mnc = String.valueOf(subscriptionInfo.getMnc());
                        if (mnc.length() == 1) {
                            mnc = "0" + mnc;
                        }
                        String hnc = mcc + mnc;
                        int slotIndex = subscriptionInfo.getSimSlotIndex();
                        int subscriptionId = subscriptionInfo.getSubscriptionId();
                        SimCard simCard = new SimCard(networkName, hnc, slotIndex, subscriptionId);
                        simCards.add(simCard);
                        preferences.edit().putString(MCC_PREF, String.valueOf(mcc)).commit();

                    }
                }
        }
        return simCards;
    }

    public static String getMcc(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(TOOLS_PREF, Context.MODE_PRIVATE);
        return preferences.getString(MCC_PREF, "");
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
        //if there are no detected simcards, just return unknown sim
        return availableSimCards.size() > 0 ? availableSimCards.get(0) : new SimCard("UNKNOWN SIM", "64101", 0, 1);
    }

    public static void setSelectedSimcard(Context context, int simslot) {
        SharedPreferences preferences = context.getSharedPreferences(TOOLS_PREF, Context.MODE_PRIVATE);
        preferences.edit().putInt(SELECTED_SIMCARD, simslot).commit();
    }

    @SuppressLint("MissingPermission")
    public static void executeUssd(String fullCode, Context context, int slot) {
        if (isBeastModeOn(context)) {//if beast mode is on use this mode
            replayUssd(fullCode, context, slot);
            return;
        }
        fullCode = fullCode + Uri.encode("#");
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

    @SuppressLint("MissingPermission")
    public static void replayUssd(String ussdcode, Context context, int slot) {
        ussdcode = ussdcode.substring(ussdcode.indexOf("*") + 1);
        String code = ussdcode.replace("*", ":");
        parts = new ArrayList<>(Arrays.asList(code.split(":")));
        String ussd = "*" + parts.get(0) + Uri.encode("#");
        parts.remove(0);

        List<PhoneAccountHandle> phoneAccountHandleList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            for (int i = 0; i < phoneAccountHandleList.size(); i++) {
                PhoneAccountHandle phoneAccountHandle = phoneAccountHandleList.get(i);
                if (i == slot) {
                    Uri uri = Uri.parse("tel:" + ussd);
                    Bundle extras = new Bundle();
                    extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
                    telecomManager.placeCall(uri, extras);
                    break;//break out of the loop
                }
            }

        } else {
            //use normal way of dialing ussd code,because their is not an easy way of getting user selected simcard
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));
        }

//        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));

    }

    public static void executeSuperAction(UssdActionWithSteps ussdActionWithSteps, Activity context) {
        //use codes for the currently selected simcard
        SimCard selectedSimCard = getSelectedSimCard((context));
        String mode1 = selectedSimCard.getNetworkName();
        UssdAction ussdAction = ussdActionWithSteps.action;
        String code = ussdAction.getCode();
//        if (mode1.contains("MTN")) code = ussdAction.getMtnCode();
//        if (mode1.contains("AIRTEL")) code = ussdAction.getAirtelCode();
//        if (mode1.contains("AFRICELL")) code = ussdAction.getAfricellCode();

        //if code is still empty ,this is a custom code ,get its code
//        if (code.isEmpty() || code == null) {
//            code = ussdAction.getAirtelCode();
//        }


        if (ussdActionWithSteps.steps == null || ussdActionWithSteps.steps.size() == 0) {
            //no steps found,execute the code immediately
            executeUssd(code, context, selectedSimCard.getSlotIndex());
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
            //display the steps
            for (Step step : steps) {
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
                    editText.setHint(step.getDescription());
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
                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
                    rowAmount.setId((int) step.getStepId());
                    final EditText editText = rowAmount.findViewById(R.id.edit_text_amount);
                    editText.setHint(step.getDescription());
                    root.addView(rowAmount);
                }
                if (step.getType() == CHOICE) {
                    String defaultValue = step.getDefaultValue();
//                    String test = "1:For myself<>2:For Others";
//                    String test1 = "1:For myself<>2:For Others";

                    View rowAmount = inflater.inflate(R.layout.row_dropdown, null);
                    rowAmount.setId((int) step.getStepId());
                    String[] data = defaultValue.split("<>");
                    String[] data1 = new String[data.length];
                    for (int i = 0; i < data.length; i++) {
                        data1[i] = String.valueOf(data[i].trim().charAt(0));
                        data[i] = data[i].substring(data[i].indexOf(":") + 1);
                    }
                    StepArrayAdapter adapter =
                        new StepArrayAdapter(
                            context,
                            R.layout.dropdown_menu_popup_item,
                            data);

                    AutoCompleteTextView editTextFilledExposedDropdown =
                        rowAmount.findViewById(R.id.action_network);
                    editTextFilledExposedDropdown.setKeyListener(null);
                    editTextFilledExposedDropdown.setAdapter(adapter);
                    editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            adapter.setSelected(data1[position]);
                            adapter.setSelected_pos(position);
                        }
                    });
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
//            Log.d(TAG, finalCode);
            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(finalCode);
                    //get all the user entered values
                    for (Step step : finalUssdAction.steps) {
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById((int) step.getStepId());
                        String value;

                        if (step.getType() == CHOICE) {
                            AutoCompleteTextView spiner = ((AutoCompleteTextView) linearLayout.findViewWithTag("editText"));
                            StepArrayAdapter adapter = (StepArrayAdapter) spiner.getAdapter();
                            value = adapter.getSelected();

                        } else {
                            value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        }
                        if (value != null && !value.isEmpty()) {
                            stringBuilder.append("*" + value);
                            //add additional steps that must immediately follow this step
                            // ,before other independent steps are appended to the final code

                            String stepsAfter = step.getStepsAfter();
                            String[] split = stepsAfter.split(",");
                            for (String s : split
                            ) {
                                if (!s.isEmpty()) {
                                    stringBuilder.append("*" + s);
                                }

                            }
                        }
//                        Log.d(TAG, "step:" + stringBuilder.toString());


                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString();
//                    Log.d(TAG, finalCode);
//                    Log.d(TAG, fullCode);

                    customDialog.dismiss();
                    //execute the ussd code
                    executeUssd(fullCode, context, selectedSimCard.getSlotIndex());

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

    public static boolean dataWasAdded(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = pref.getBoolean("datawasAdded", false);//get false at first
        pref.edit().putBoolean("datawasAdded", true).commit();//value of true will be returned the second and onwards
        return b;
    }

    public static boolean containsHni(final List<SimCard> list, final String hni) {
        if (list.size() > 1) {
            if ((list.get(0).getHni().equals(hni)) || (list.get(1).getHni().equals(hni))) {
                return true;
            }
        } else if (list.size() == 1) {
            if (list.get(0).getHni().equals(hni)) return true;
        } else {
            return false;
        }
        return false;
    }

    public static boolean isBeastModeOn(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean b = pref.getBoolean("beastMode", true);//get true at first
//        pref.edit().putBoolean("beastMode", true).commit();//value of true will be returned the second and onwards
        return b && isAccessibilityServiceEnabled(context, UssdDetector.class);
    }

    public static void setBeastModeOn(Context context, boolean onOrOff) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean("beastMode", onOrOff).commit();//value of true will be returned the second and onwards
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        boolean accessibilityServiceEnabled = false;
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo enabledService : runningServices) {
            ServiceInfo serviceInfo = enabledService.getResolveInfo().serviceInfo;
            if (serviceInfo.packageName.equals(context.getPackageName()) && serviceInfo.name.equals(service.getName())) {
                accessibilityServiceEnabled = true;
            }
        }

        return accessibilityServiceEnabled;
    }
}
