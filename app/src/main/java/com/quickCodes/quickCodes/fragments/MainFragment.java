package com.quickCodes.quickCodes.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterUssdCodes;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.PageViewModel;
import com.quickCodes.quickCodes.util.UssdActionsViewModel;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnSelectClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_AIRTIME;
import static com.quickCodes.quickCodes.modals.Constants.SEC_DATA;
import static com.quickCodes.quickCodes.modals.Constants.SEC_MMONEY;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;

public class MainFragment extends Fragment {

    private static final int CONTACT_PICKER_REQUEST = 29;
    Dialog dialog;
    String mode = null;
    int slot = -1;
    int subscriptionId = 0;
    List<SubscriptionInfo> subList;
    EditText phoneNumber;
    RecyclerView airtimeRecyclerView, dataRecyclerView, mmRecyclerView;
    private OnFragmentInteractionListener mListener;
    private UssdActionsViewModel viewModel;
    private AdapterUssdCodes adapterUssdCodes, adapterUssdCodes1, adapterUssdCodes2;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapterUssdCodes = new AdapterUssdCodes(getActivity());
        adapterUssdCodes1 = new AdapterUssdCodes(getActivity());
        adapterUssdCodes2 = new AdapterUssdCodes(getActivity());
        viewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        viewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
            List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
            List<UssdActionWithSteps> dataCodes = new ArrayList<>();
            List<UssdActionWithSteps> mmoneyCodes = new ArrayList<>();
            for (UssdActionWithSteps us : ussdActionWithSteps) {
                Log.d("SIZE", String.valueOf( us.action.getName().length()));
                if(us.action.getName().length()<15){
                    int len = 15- us.action.getName().length();
                    String d = "";
                    for(int i=0;i<len;i++){
                        d = d+" ";
                    }
                    us.action.setName(us.action.getName()+d);
                }

                if (us.action.getSection() == SEC_AIRTIME) {
                    airtimeCodes.add(us);
                }
                if (us.action.getSection() == SEC_DATA) {
                    dataCodes.add(us);
                }
                if (us.action.getSection() == SEC_MMONEY) {
                    mmoneyCodes.add(us);
                }
            }
            adapterUssdCodes.setUssdActions(airtimeCodes);
            adapterUssdCodes1.setUssdActions(dataCodes);
            adapterUssdCodes2.setUssdActions(mmoneyCodes);

        });
        adapterUssdCodes.setOnItemClickListener(new AdapterUssdCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                executeSuperAction(obj);
            }

            @Override
            public void onItemDelete(View view, UssdAction obj, int position) {

            }

            @Override
            public void onItemEdit(View view, UssdAction obj, int position) {

            }
        });
        adapterUssdCodes1.setOnItemClickListener(new AdapterUssdCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                executeSuperAction(obj);
            }

            @Override
            public void onItemDelete(View view, UssdAction obj, int position) {

            }

            @Override
            public void onItemEdit(View view, UssdAction obj, int position) {

            }
        });
        adapterUssdCodes2.setOnItemClickListener(new AdapterUssdCodes.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                executeSuperAction(obj);
            }

            @Override
            public void onItemDelete(View view, UssdAction obj, int position) {

            }

            @Override
            public void onItemEdit(View view, UssdAction obj, int position) {

            }
        });
        setUpDialog();

    }





    private void setUpDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_enter_number_and_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        //setup airtime
        airtimeRecyclerView = root.findViewById(R.id.airtimeRecylerView);
        airtimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        airtimeRecyclerView.setAdapter(adapterUssdCodes);

        //setup data
        dataRecyclerView = root.findViewById(R.id.dataRecylerView);
        dataRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        dataRecyclerView.setAdapter(adapterUssdCodes1);
        //setup mobile money
        mmRecyclerView = root.findViewById(R.id.mmoneyRecylerView);
        mmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mmRecyclerView.setAdapter(adapterUssdCodes2);


        //TRIAL CODE TO USE MCC AND MNC
        TelephonyManager t = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//            if(t.getPhoneType()!= TelephonyManager.PHONE_TYPE_CDMA){
        Toast.makeText(getActivity(), t.getNetworkOperator(), Toast.LENGTH_SHORT).show();
        Log.d("TELEPHONE", t.getNetworkOperator());
        Log.d("TELEPHONE", t.getNetworkOperatorName());
        PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.addSimcard(t.getNetworkOperator(),t.getNetworkOperatorName());


//            }
//*****************************************SIMCARDS IN PHONE*********************************************************
        //initialize chip views
        final LinearLayout rootLinearLayoutChips = root.findViewById(R.id.linearLayout_root_chips);
        com.robertlevonyan.views.chip.Chip chipAirtel = root.findViewById(R.id.chip_airel);
        com.robertlevonyan.views.chip.Chip chipMtn = root.findViewById(R.id.chip_mtn);
        com.robertlevonyan.views.chip.Chip chipAfricell = root.findViewById(R.id.chip_africell);
        List<Chip> chips = Arrays.asList(chipAirtel, chipAfricell, chipMtn);

        //set click listeners on chips
        for (final com.robertlevonyan.views.chip.Chip chip : chips) {
            chip.setOnSelectClickListener((v, selected) -> {
                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);
                    }
                }
            });
            chip.setOnClickListener(v -> {
                ((Chip) v).setChipSelected(true);

                for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                    if (!v.equals(v1)) {
                        ((Chip) v1).setChipSelected(false);

                    }
                }
            });
        }
        ;
        //make the first chip selected
        chipAirtel.setChipSelected(true);
        //set default mode to airtel
        mode = "Airtel";
        slot = 0;

        //if the api level is greaterthat 22 ie lollipop, get simcards inside phone
        SubscriptionManager subscriptionManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            subList = subscriptionManager.getActiveSubscriptionInfoList();

            for (final SubscriptionInfo subscriptionInfo : subList) {
                final String networkName = subscriptionInfo.getCarrierName().toString().toUpperCase();

                final Chip chip1 = (Chip) getLayoutInflater().inflate(R.layout.chip, null);
                //set margin for the chip
                chip1.setText(networkName);
                if (networkName.contains("MTN")) {
                    chip1.setChipIcon(getResources().getDrawable(R.drawable.mtn));
                    hideNonMtnAction();
                }
                if (networkName.contains("AFRICELL"))
                    chip1.setChipIcon(getResources().getDrawable(R.drawable.africell));
                if (subscriptionInfo.getSimSlotIndex() == 0) {
                    chip1.setChipSelected(true);
                    mode = chip1.getText().toString();
                    slot = 0;
                }
                chip1.setOnSelectClickListener(new OnSelectClickListener() {
                    @Override
                    public void onSelectClick(View v, boolean selected) {
                        for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                            if (!v.equals(v1)) {
                                ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);
                                mode = chip1.getText().toString();
                                slot = subscriptionInfo.getSimSlotIndex();
                                subscriptionId = subscriptionInfo.getSubscriptionId();
                            }
//
                        }
                        //if the selected mode is mtn remove non mtn action cards
                            filterAndHideActions(mode);
                        Toast.makeText(getActivity(), "You have changed to " + mode + " codes", Toast.LENGTH_SHORT).show();

                    }
                });
                chip1.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                    @Override
                    public void onClick(View v) {
                        //mark the current chip as selected
                        ((com.robertlevonyan.views.chip.Chip) v).setChipSelected(true);
                        //mark all the others as deselected
                        for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                            if (!v.equals(v1)) {
                                ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);
                                mode = chip1.getText().toString();
                                slot = subscriptionInfo.getSimSlotIndex();
                                subscriptionId = subscriptionInfo.getSubscriptionId();
                            }
                        }
                        //if the selected mode is mtn remove non mtn action cards
                        filterAndHideActions(mode);
                        //notify the user of their action
                        Toast.makeText(getActivity(), "You have changed to " + mode + " codes", Toast.LENGTH_SHORT).show();

                    }
                });
                //add detected chips
                rootLinearLayoutChips.addView(chip1);
                setMargins(chip1, 0, 0, 15, 0);

                //filter out actions that dont apply for the currently selected network
                filterAndHideActions(mode);


                //hide default chips
                for (Chip chip : chips) chip.setVisibility(View.GONE);

            }

        }

        return root;
    }

    private void filterAndHideActions(String mode) {
        if(mode!=null){

                viewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
                    List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
                    List<UssdActionWithSteps> dataCodes = new ArrayList<>();
                    List<UssdActionWithSteps> mmoneyCodes = new ArrayList<>();
                    for (UssdActionWithSteps us : ussdActionWithSteps) {
                        if(mode.contains("MTN")) {
                            if (us.action.getMtnCode() == null) {
                                continue;
                            }
                        }
                        if (mode.contains("AFRICELL")){
                            if ((us.action.getAfricellCode()==null)) {
                                continue;
                            }
                            String code = us.action.getAfricellCode();
                            if(code!=null){
                                if(code.contains("not"))continue;
                            }
                        }
                        if (us.action.getSection() == SEC_AIRTIME) {
                            airtimeCodes.add(us);
                        }
                        if (us.action.getSection() == SEC_DATA) {
                            dataCodes.add(us);
                        }
                        if (us.action.getSection() == SEC_MMONEY) {
                            mmoneyCodes.add(us);
                        }
                    }
                    adapterUssdCodes.setUssdActions(airtimeCodes);
                    adapterUssdCodes1.setUssdActions(dataCodes);
                    adapterUssdCodes2.setUssdActions(mmoneyCodes);

                });
            }

    }

    /**
     * methods to remove cards with codes that are not supported on mtn
     */
    private void hideNonMtnAction() {
        List<RecyclerView> recyclerViews = Arrays.asList(airtimeRecyclerView,
            dataRecyclerView, mmRecyclerView);
        for (RecyclerView l : recyclerViews) {
            ArrayList<View> hiddenViews = getViewsByTag(l, "hide");
            for (View v : hiddenViews) v.setVisibility(View.GONE);

        }
    }

    private void unHideNonMtnAction() {
        List<RecyclerView> recyclerViews = Arrays.asList(airtimeRecyclerView,
            dataRecyclerView, mmRecyclerView);
        for (RecyclerView l : recyclerViews) {
            ArrayList<View> hiddenViews = getViewsByTag(l, "hide");
            for (View v : hiddenViews) v.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void executeUssd(String fullCode) {
        TelecomManager telecomManager = null;
        List<PhoneAccountHandle> phoneAccountHandleList = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //TODO if api level is greater than 26 do background codes,do this later,not important right now
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //            TelephonyManager manager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//            manager.createForSubscriptionId(subscriptionId).sendUssdRequest("*131#", new TelephonyManager.UssdResponseCallback() {
//                /**
//                 * Called when a USSD request has succeeded.  The {@code response} contains the USSD
//                 * response received from the network.  The calling app can choose to either display the
//                 * response to the user or perform some operation based on the response.
//                 * <p>
//                 * USSD responses are unstructured text and their content is determined by the mobile network
//                 * operator.
//                 *
//                 * @param telephonyManager the TelephonyManager the callback is registered to.
//                 * @param request          the USSD request sent to the mobile network.
//                 * @param response         the response to the USSD request provided by the mobile network.
//                 **/
//                @Override
//                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
//                    super.onReceiveUssdResponse(telephonyManager, request, response);
//                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
//                }
//
//                /**
//                 * Called when a USSD request has failed to complete.
//                 *
//                 * @param telephonyManager the TelephonyManager the callback is registered to.
//                 * @param request          the USSD request sent to the mobile network.
//                 * @param failureCode      failure code indicating why the request failed.  Will be either
//                 *                         {@link TelephonyManager#USSD_RETURN_FAILURE} or
//                 *                         {@link TelephonyManager#USSD_ERROR_SERVICE_UNAVAIL}.
//                 **/
//                @Override
//                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
//                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
//                    Toast.makeText(getActivity(),String.valueOf(failureCode), Toast.LENGTH_SHORT).show();
//
//                }
//            }, new Handler());
            }

            telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
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
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

        }

    }

    /**
     * this mthd checks a super action  for the network and  ussdAction and executes it
     *
     * @param ussdActionWithSteps
     */
    public void executeSuperAction(UssdActionWithSteps ussdActionWithSteps) {

        //use codes for the selected network mode
//        UssdAction ussdAction = null;
        String mode1 = mode.toUpperCase();
        UssdAction ussdAction = ussdActionWithSteps.action;
        String code = "";
        if (mode1.contains("MTN")) code = ussdAction.getMtnCode();
        if (mode1.contains("AIRTEL")) code = ussdAction.getAirtelCode();
        if (mode1.contains("AFRICELL")) code = ussdAction.getAfricellCode();


        if (ussdActionWithSteps.steps == null || ussdActionWithSteps.steps.size() == 0) {
            //execute the code immediately
            executeUssd(code + Uri.encode("#"));

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
            String finalCode = code;
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
                        }

                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString() + Uri.encode("#");

                    customDialog.dismiss();
                    //execute the ussd code
                    executeUssd(fullCode);


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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }
    //************************ UTILITY METHODS *******************************************************************************

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //adopted from statck overflow https://stackoverflow.com/questions/8817377/android-how-to-find-multiple-views-with-common-attribute
    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}

