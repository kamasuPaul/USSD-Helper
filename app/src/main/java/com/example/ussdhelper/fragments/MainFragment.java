package com.example.ussdhelper.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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

import com.example.ussdhelper.R;
import com.google.android.material.snackbar.Snackbar;
import com.hover.sdk.api.HoverParameters;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

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
    private static final int CONTACT_PICKER_REQUEST =29;

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
        return root;
    }

    private void checkBalance() {
        //*185*2*1*1*amount*pin
//        Intent i = new HoverParameters.Builder(getActivity())
//            .request("9ad2cd6e")
//            .buildIntent();
//        startActivityForResult(i, 0);
        String ussdCode = "*131"+ Uri.encode("#");
        startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ussdCode)));
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
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                Log.d("MyTag", results.get(0).getDisplayName());
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
                contactPicker();
            }
        });


        ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hoverIntent = new HoverParameters.Builder(getActivity())
                    .request("e0d94aec")
                    .style(R.style.BaseTheme)
                    .extra("MobileNumber", editTextNumber.getText().toString())
                    .extra("Amount", editTextAmount.getText().toString())
                    .buildIntent();
                startActivityForResult(hoverIntent, 0);
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

    private void contactPicker() {
        new MultiContactPicker.Builder(getActivity()) //Activity/fragment context
//                    .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
            .hideScrollbar(false) //Optional - default: false
            .showTrack(true) //Optional - default: true
            .searchIconColor(Color.WHITE) //Option - default: White
            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
            .handleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary)) //Optional - default: Azure Blue
            .bubbleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary)) //Optional - default: Azure Blue
            .bubbleTextColor(Color.WHITE) //Optional - default: White
            .setTitleText("Select Contacts") //Optional - default: Select Contacts
//                    .setSelectedContacts("10", "5" / myList) //Optional - will pre-select contacts of your choice. String... or List<ContactResult>
            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
            .limitToColumn(LimitColumn.NONE) //Optional - default NONE (Include phone + email, limiting to one can improve loading time)
            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out) //Optional - default: No animation overrides
            .showPickerForResult(CONTACT_PICKER_REQUEST);
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
                Intent hoverIntent = new HoverParameters.Builder(getActivity())
                    .request("aa04833e")
                    .style(R.style.BaseTheme)
                    .extra("MobileNumber", editTextNumber.getText().toString())
                    .extra("Amount", editTextAmount.getText().toString())
                    .buildIntent();
                Toast.makeText(getActivity(), editTextNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), editTextAmount.getText().toString(), Toast.LENGTH_SHORT).show();
                startActivityForResult(hoverIntent, 0);
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
    private void withdrawCash(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_enter_amount);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        final EditText editTextAmount = dialog.findViewById(R.id.edit_text_amount);


        ((Button) dialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hoverIntent = new HoverParameters.Builder(getActivity())

                    .request("5aba6be4")
                    .style(R.style.BaseTheme)
                    .extra("Amount", editTextAmount.getText().toString())
                    .buildIntent();
                startActivityForResult(hoverIntent, 0);
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
    private void callMeBack(){
        //7199627a
    }
}
