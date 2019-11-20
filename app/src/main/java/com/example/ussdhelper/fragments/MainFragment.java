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
import android.widget.Toast;

import com.example.ussdhelper.MainActivity;
import com.example.ussdhelper.R;
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
    List<SubscriptionInfo> subList;

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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final LinearLayout checkAirtimeBalance = root.findViewById(R.id.check_balance);
        checkAirtimeBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBalance();
            }
        });
        final LinearLayout checkDataBalance = root.findViewById(R.id.check_data_balance);
        checkDataBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataBalance();
            }
        });
        final LinearLayout buyAirtime = root.findViewById(R.id.buy_airtime);
        buyAirtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPolygon();
            }
        });
        final LinearLayout buyData = root.findViewById(R.id.sendData);
        buyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPolygon1();
            }
        });
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

//        pageViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        //iniatize chip views
        final LinearLayout rootLinearLayoutChips = root.findViewById(R.id.linearLayout_root_chips);
        com.robertlevonyan.views.chip.Chip chipAirtel = root.findViewById(R.id.chip_airel);
        com.robertlevonyan.views.chip.Chip chipMtn = root.findViewById(R.id.chip_mtn);
        com.robertlevonyan.views.chip.Chip chipAfricel = root.findViewById(R.id.chip_africell);
        List<Chip> chips = Arrays.asList(chipAirtel, chipAfricel, chipMtn);
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
        SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        subList = subscriptionManager.getActiveSubscriptionInfoList();
//                    Toast.makeText(getActivity(), "Detected simcards:"subscriptionInfo.toString(), Toast.LENGTH_SHORT).show();

        for(SubscriptionInfo subscriptionInfo: subList){
            Toast.makeText(getActivity(), "Slot "+subscriptionInfo.getSimSlotIndex()+1+" "+subscriptionInfo.getCarrierName()+" "+subscriptionInfo.getIccId(), Toast.LENGTH_SHORT).show();
            subscriptionInfo.getNumber();
            String networkName = subscriptionInfo.getCarrierName().toString();
            final Chip chip1 = (Chip)getLayoutInflater().inflate(R.layout.chip, null);
            chip1.setText(networkName);

            chip1.setOnSelectClickListener(new OnSelectClickListener() {
                @Override
                public void onSelectClick(View v, boolean selected) {
                    for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                        if (!v.equals(v1)) {
                            ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);
                            mode = chip1.getText().toString();
                            Toast.makeText(getActivity(), mode, Toast.LENGTH_SHORT).show();

                        }
//
                    }
                }
            });
            chip1.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(View v) {
                    ((com.robertlevonyan.views.chip.Chip) v).setChipSelected(true);

                    for (View v1 : getViewsByTag(rootLinearLayoutChips, "chip")) {
                        if (!v.equals(v1)) {
                            ((com.robertlevonyan.views.chip.Chip) v1).setChipSelected(false);
                            mode = chip1.getText().toString();
                            Toast.makeText(getActivity(), mode, Toast.LENGTH_SHORT).show();



                        }
//
                    }

                }
            });


            rootLinearLayoutChips.addView(chip1);

        }

        return root;
    }

    private int checkSelfPermission(String readPhoneState) {
        return 1;
    }

    @SuppressLint({"NewApi", "MissingPermission"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkBalance() {
        CharSequence chosenNetworkId ="";
        for(SubscriptionInfo subscriptionInfo: subList) {
            Toast.makeText(getActivity(), "Slot " + subscriptionInfo.getSimSlotIndex() + 1 + " " + subscriptionInfo.getCarrierName() + " " + subscriptionInfo.getIccId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), mode, Toast.LENGTH_SHORT).show();
            if ((subscriptionInfo.getCarrierName()).toString().contains(("Airtel"))) {
                chosenNetworkId = subscriptionInfo.getIccId();
                Toast.makeText(getActivity(), "df"+chosenNetworkId, Toast.LENGTH_SHORT).show();

            }
        }
        TelecomManager telecomManager = (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
        @SuppressLint("MissingPermission") List<PhoneAccountHandle>list = telecomManager.getCallCapablePhoneAccounts();
        for(PhoneAccountHandle phoneAccountHandle : list){
            Toast.makeText(getActivity(), "bdfuodfu", Toast.LENGTH_SHORT).show();
            Log.d("CODE",chosenNetworkId.toString());
            Log.d("CODE",phoneAccountHandle.getId());
            phoneAccountHandle.
//            if(phoneAccountHandle.getId().contains(chosenNetworkId)){
                Toast.makeText(getActivity(), "bdfuodfu", Toast.LENGTH_SHORT).show();
                String cd = "*131" + Uri.encode("#");
//                    Uri uri = Uri.fromParts("tel",Uri.parse("tel:" + cd))
                Bundle extras = new Bundle();
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE,phoneAccountHandle);
                telecomManager.placeCall(Uri.parse("tel:" + cd),extras);

//                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + cd)));
//            }
        }

//        String ussdCode = "*131"+ Uri.encode("#");
//        startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ussdCode)));
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
    private void callMeBack(){
        //7199627a
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
}
