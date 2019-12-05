package com.example.ussdhelper.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import io.github.farhad.contactpicker.ContactPicker;

import android.os.Handler;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ussdhelper.MainActivity;
import com.example.ussdhelper.R;
import com.example.ussdhelper.modals.Step;
import com.example.ussdhelper.modals.UssdAction;
import com.google.android.material.snackbar.Snackbar;
//import com.hover.sdk.api.HoverParameters;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnSelectClickListener;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    Dialog dialog;
    Dialog customDialog;
    private static final int CONTACT_PICKER_REQUEST = 29;
    String mode = null;
    int slot = -1;
    int subscriptionId =0;
    List<SubscriptionInfo> subList;
    List<SuperAction>superActions;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        superActions = new ArrayList<>();
        Step[] tel_nos = {new Step(2, "Text", null, -1)};
        UssdAction action1 =  new UssdAction(0,"Buy Airtime","*185*2*1*1","",tel_nos);
        UssdAction action2 =  new UssdAction(0,"Buy Airtime","*131","", tel_nos);

        SuperAction superAction = new SuperAction(action1,action2);
        superActions.add(superAction);
        superActions.add(simpleAction("Check Balance","*131","*131"));
        superActions.add(simpleAction("Borrow Airtime","*100*4*1","*131"));
        superActions.add(new SuperAction(new UssdAction(0,"Call Me Back","*100*7*7","",
            new Step[]{new Step(0,"Tel No",null,-1)}),
            new UssdAction(0,"Buy Airtime","*131","",
                new Step[]{new Step(0,"Tel No",null,-1)})));
        superActions.add(simpleAction(" PakaLast  ","*100*2*1","*131"));


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

        //**********************AIRTIME****************************************************************//

        //generate layouts for airtime section
        //TODO make this work, i dont why click listeners are not working
        final LinearLayout linearLayoutAirtime = root.findViewById(R.id.linearLayout_airtime);
        for(final SuperAction s: superActions){
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.item_action,null,false);
            View view = cardView.findViewById(R.id.myAction);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "kdfj", Toast.LENGTH_SHORT).show();
                    executeSuperAction(s);
                }
            });
            linearLayoutAirtime.addView(cardView);
            setMargins(cardView,3,3,10,3);

            //set action name
            TextView actionName = cardView.findViewById(R.id.TextView_ActionName);
            actionName.setText(String.valueOf(s.getAirtel().getName()));


        }

         //


//        final LinearLayout checkAirtimeBalance = root.findViewById(R.id.check_balance);
//        checkAirtimeBalance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "kdfj", Toast.LENGTH_SHORT).show();
//                executeSuperAction(new SuperAction(new UssdAction(0,"airtel","*131","",new UssdAction.Step[]{new UssdAction.Step(2,"Tel No","",2)}),new UssdAction(0,"aitel","*131","",new UssdAction.Step[]{})));
//            }
//        });
//        final LinearLayout checkDataBalance = root.findViewById(R.id.check_data_balance);
//        checkDataBalance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkDataBalance();
//            }
//        });
//        final LinearLayout buyAirtime = root.findViewById(R.id.buy_airtime);
//        buyAirtime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialogPolygon();
//            }
//        });
//        final LinearLayout buyData = root.findViewById(R.id.sendData);
//        buyData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialogPolygon1();
//            }
//        });
        final LinearLayout sendMoneyAirtel = root.findViewById(R.id.send_money_airteNo);
        sendMoneyAirtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMoneyToAirtelNo();
            }
        });
        final LinearLayout withDrawCash = root.findViewById(R.id.withdraw_cash);
        withDrawCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawCash();
            }
        });

        //iniatize chip views
        final LinearLayout rootLinearLayoutChips = root.findViewById(R.id.linearLayout_root_chips);
        com.robertlevonyan.views.chip.Chip chipAirtel = root.findViewById(R.id.chip_airel);
        com.robertlevonyan.views.chip.Chip chipMtn = root.findViewById(R.id.chip_mtn);
        com.robertlevonyan.views.chip.Chip chipAfricel = root.findViewById(R.id.chip_africell);
        List<Chip> chips = Arrays.asList(chipAirtel, chipAfricel, chipMtn);
        //set click listeners on chips
        for (final com.robertlevonyan.views.chip.Chip chip : chips) {
            chip.setOnSelectClickListener(new OnSelectClickListener() {
                @Override
                public void onSelectClick(View v, boolean selected) {
                    for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                        if (!v.equals(v1)) {
                            ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);

                        }
//
                    }
                }
            });
            chip.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(View v) {
                    ((com.robertlevonyan.views.chip.Chip) v).setChipSelected(true);

                    for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                        if (!v.equals(v1)) {
                            ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);

                        }
//
                    }

                }
            });
        };
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

            for(final SubscriptionInfo subscriptionInfo: subList){
                String networkName = subscriptionInfo.getCarrierName().toString();

                final Chip chip1 = (Chip)getLayoutInflater().inflate(R.layout.chip, null);
                //set margin for the chip
                chip1.setText(networkName);
                if(networkName.contains("MTN"))chip1.setChipIcon(getResources().getDrawable(R.drawable.mtn));
                if(networkName.contains("AFRICELL"))chip1.setChipIcon(getResources().getDrawable(R.drawable.africell));
                if(subscriptionInfo.getSimSlotIndex()==0){
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
                        //notify the user of their action
                        Toast.makeText(getActivity(),"You have changed to "+ mode+" codes", Toast.LENGTH_SHORT).show();

                    }
                });
                //add detected chips
                rootLinearLayoutChips.addView(chip1);
                setMargins(chip1,0,0,15,0);


                //hide default chips
                for (Chip chip:chips)chip.setVisibility(View.GONE);

            }

        }

//                    Toast.makeText(getActivity(), "Detected simcards:"subscriptionInfo.toString(), Toast.LENGTH_SHORT).show();


        return root;
    }

    @SuppressLint("MissingPermission")
    private void executeUssd(String fullCode) {
        Toast.makeText(getActivity(),"method called"+fullCode, Toast.LENGTH_SHORT).show();

        TelecomManager telecomManager = null;
        List<PhoneAccountHandle>phoneAccountHandleList = null;
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
            telecomManager = (TelecomManager)getActivity().getSystemService(Context.TELECOM_SERVICE);
            phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
            for(int i=0;i<phoneAccountHandleList.size();i++){
                PhoneAccountHandle phoneAccountHandle = phoneAccountHandleList.get(i);
                if(i==slot){
                    Uri uri = Uri.parse("tel:" + fullCode);
                    Bundle extras = new Bundle();
                    extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,phoneAccountHandle);
                    telecomManager.placeCall(uri,extras);
                    break;//break out of the loop
                }
            }

        }else{
            //use normal way of dialing ussd code,because their is not an easy way of getting user selected simcard
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

        }

    }


    /**
     * this mthd checks a super action  for the network and  ussdAction and executes it
     * @param superAction
     */
    public void executeSuperAction(SuperAction superAction) {
        //use codes for the selected network mode
        final UssdAction ussdAction;
        switch (mode){
            case "Airtel":
                ussdAction = superAction.getAirtel();
                break;
            case "Mtn":
                ussdAction = superAction.getMtn();
                break;
            case "Africell":
                ussdAction = superAction.getAirtel();
                break;
            default:
                ussdAction = superAction.getAirtel();
        }
        if (ussdAction.getSteps() == null || ussdAction.getSteps().length == 0) {
            //execute the code immediately
            Toast.makeText(getActivity(), "No steps Found",Toast.LENGTH_SHORT).show();
            executeUssd(ussdAction.getCode()+Uri.encode("#"));

        } else {
            Toast.makeText(getActivity(), " steps Found",Toast.LENGTH_SHORT).show();


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
                    root.addView(rowText);


                }
                if (step.getType().equals("Tel No")) {
                    View rowTelephone = inflater.inflate(R.layout.row_telephone, null);

                            ImageButton imageButton = rowTelephone.findViewById(R.id.selec_contact_ImageBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO pick contact
            }
        });

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

            // TODO  add code for choosing phone number
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
                    for(Step step: ussdAction.getSteps()){
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById(step.getId());

                        String value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        if(value!=null&&!value.isEmpty()){
                            stringBuilder.append("*"+value);
                        }

                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString()+ Uri.encode("#");

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
        Toast.makeText(getActivity(), "aciit callde", Toast.LENGTH_SHORT).show();
//        if (resultCode == 0) {
//            Toast.makeText(getActivity(), data.getDataString(), Toast.LENGTH_SHORT).show();
//            if (dialog != null) {
//                dialog.dismiss();
//            }
//
//        }
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getActivity().getContentResolver().query(contactUri,projection,null,null,null);

                if(cursor !=null && cursor.moveToFirst()){
                    int numberIdex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIdex);
                    final EditText editTextNumber = customDialog.findViewById(R.id.edit_text_mobileNumber);
                    if(editTextNumber!=null){
                        editTextNumber.setText(number);
                    }


                }

//                List<ContactResult> results = MultiContactPicker.obtainResult(data);
//                Log.d("MyTag", results.get(0).getDisplayName());
            } else if(resultCode == RESULT_CANCELED){
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
    // TODO: Rename method, update argument and hook method into UI event
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void showDialogPolygon() {


//        if(true)return;//end here
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_enter_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final EditText editTextAmount = dialog.findViewById(R.id.edit_text_amount);


        ((Button) dialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ussdCode = "*185*2*1*1*"+editTextAmount.getText().toString().trim()+ Uri.encode("#");
                startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ussdCode)));
//        *185*2*1*1*amount*pin
//
//                Intent hoverIntent = new HoverParameters.Builder(getActivity())
//
//                    .request("46e80959")
//                    .style(R.style.BaseTheme)
//                    .extra("Amount", editTextAmount.getText().toString())
//                    .buildIntent();
//                startActivityForResult(hoverIntent, 0);
            }

        });

        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    private void showDialogPolygon1() {

        //inflate the root dialog
        LayoutInflater inflater = getLayoutInflater();
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.dialog_root,null);
        LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
        //inflate each row that should be contained in the dialog box
        View rowAmount = inflater.inflate(R.layout.row_amount,null);
        View rowTelephone = inflater.inflate(R.layout.row_telephone,null);
        View rowText = inflater.inflate(R.layout.row_text,null);
        View rowButtons = inflater.inflate(R.layout.row_buttons,null);
        //add each row to the root
        root.addView(rowAmount);
        root.addView(rowTelephone);
        root.addView(rowText);
        root.addView(rowButtons);

        customDialog = new Dialog(getActivity());
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        customDialog.setContentView(cardView);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        customDialog.setCancelable(true);


        final EditText editTextAmount = customDialog.findViewById(R.id.edit_text_amount);
        final EditText editTextNumber = customDialog.findViewById(R.id.edit_text_mobileNumber);
        ImageButton imageButton = customDialog.findViewById(R.id.selec_contact_ImageBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i,CONTACT_PICKER_REQUEST);

//                ((MainActivity)getActivity()).contactPicker(getActivity());
            }
        });


        ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent hoverIntent = new HoverParameters.Builder(getActivity())
//                    .request("e0d94aec")
//                    .style(R.style.BaseTheme)
//                    .extra("MobileNumber", editTextNumber.getText().toString())
//                    .extra("Amount", editTextAmount.getText().toString())
//                    .buildIntent();
//                startActivityForResult(hoverIntent, 0);
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
    private void checkDataBalance() {
//        Intent i = new HoverParameters.Builder(getActivity())
//            .request("c7f7271b")
//            .buildIntent();
//        startActivityForResult(i, 0);
        String ussdCode = "*175*4" + Uri.encode("#");
        startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ussdCode)));

    }
    private void sendMoneyToAirtelNo(){
       // *185*1*1*mn*amount*pin
        final EditText editTextAmount = dialog.findViewById(R.id.edit_text_amount);
        final EditText editTextNumber = dialog.findViewById(R.id.edit_text_mobileNumber);
        ((Button) dialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent hoverIntent = new HoverParameters.Builder(getActivity())
//                    .request("aa04833e")
//                    .style(R.style.BaseTheme)
//                    .extra("MobileNumber", editTextNumber.getText().toString())
//                    .extra("Amount", editTextAmount.getText().toString())
//                    .buildIntent();
//                Toast.makeText(getActivity(), editTextNumber.getText().toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), editTextAmount.getText().toString(), Toast.LENGTH_SHORT).show();
//                startActivityForResult(hoverIntent, 0);
            }

        });

        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();

    }
    @SuppressLint("MissingPermission")
    private void withdrawCash(){
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
//        dialog.setContentView(R.layout.dialog_enter_amount);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setCancelable(true);
//        final EditText editTextAmount = dialog.findViewById(R.id.edit_text_amount);
//
//
//        ((Button) dialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
                /*
                trial code for getting result from ussd code

                 */
                TelephonyManager manager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.sendUssdRequest("*131#", new TelephonyManager.UssdResponseCallback() {
                        /**
                         * Called when a USSD request has succeeded.  The {@code response} contains the USSD
                         * response received from the network.  The calling app can choose to either display the
                         * response to the user or perform some operation based on the response.
                         * <p>
                         * USSD responses are unstructured text and their content is determined by the mobile network
                         * operator.
                         *
                         * @param telephonyManager the TelephonyManager the callback is registered to.
                         * @param request          the USSD request sent to the mobile network.
                         * @param response         the response to the USSD request provided by the mobile network.
                         **/
                        @Override
                        public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                            super.onReceiveUssdResponse(telephonyManager, request, response);
                            Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                        }

                        /**
                         * Called when a USSD request has failed to complete.
                         *
                         * @param telephonyManager the TelephonyManager the callback is registered to.
                         * @param request          the USSD request sent to the mobile network.
                         * @param failureCode      failure code indicating why the request failed.  Will be either
                         *                         {@link TelephonyManager#USSD_RETURN_FAILURE} or
                         *                         {@link TelephonyManager#USSD_ERROR_SERVICE_UNAVAIL}.
                         **/
                        @Override
                        public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                            super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                            Toast.makeText(getActivity(),String.valueOf(failureCode), Toast.LENGTH_SHORT).show();

                        }
                    }, new Handler());
                }else{
                    //use normal way of dialing ussd code

                }
//                Intent hoverIntent = new HoverParameters.Builder(getActivity())
//
//                    .request("5aba6be4")
//                    .style(R.style.BaseTheme)
//                    .extra("Amount", editTextAmount.getText().toString())
//                    .buildIntent();
//                startActivityForResult(hoverIntent, 0);
//            }
//
//        });
//
//        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//        dialog.show();
    }


    //************************ UTILITY METHODS *************************************************

    public SuperAction simpleAction(String name,String airtelCode,String mtnCode){
        Step[] tel_nos = {};
        UssdAction action1 =  new UssdAction(0,name,airtelCode,"",tel_nos);
        UssdAction action2 =  new UssdAction(0,name,mtnCode,"", tel_nos);

        SuperAction superAction = new SuperAction(action1,action2);
        return superAction;
    }
    //adopted from statck overflow https://stackoverflow.com/questions/8817377/android-how-to-find-multiple-views-with-common-attribute
    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
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
     * @param v the  view whose layout is to be set
     * @param l left
     * @param t top
     * @param r right
     * @param b bottom
     */
    public static void setMargins(View v,int l,int t,int r,int b){
        if(v.getLayoutParams()instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l,t,r,b);
            v.requestLayout();
        }
    }

    //TODO change its location
    class SuperAction{
        int id;
        UssdAction airtel;
        UssdAction mtn;
        public SuperAction(UssdAction airtel,UssdAction mtn){
            this.airtel = airtel;
            this.mtn = mtn;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public UssdAction getAirtel() {
            return airtel;
        }

        public void setAirtel(UssdAction airtel) {
            this.airtel = airtel;
        }

        public UssdAction getMtn() {
            return mtn;
        }

        public void setMtn(UssdAction mtn) {
            this.mtn = mtn;
        }
    }
}

