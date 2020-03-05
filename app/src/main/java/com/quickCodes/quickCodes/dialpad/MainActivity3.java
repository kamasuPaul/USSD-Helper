package com.quickCodes.quickCodes.dialpad;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.quickCodes.quickCodes.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity3 extends AppCompatActivity {
    String num;
    TextView edit_text,tname,tnumber;
    ImageView one, two, three, four, five, six, seven, eight, nine, zero, star, hash, sim1, sim2, clear;

    BottomSheetBehavior bottomSheetBehavior;


    ArrayList<String> namelist = new ArrayList<> ();
    ArrayList<String> numberlist = new ArrayList<> ();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        getContactList ();



//        // get the bottom sheet view
//        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
//
//        // init the bottom sheet behavior
//        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
//
//        // change the state of the bottom sheet
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//        // set callback for changes
//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });

        edit_text = (TextView) findViewById(R.id.edit_text);
        edit_text.setOnClickListener(null);
        tname = findViewById (R.id.matchedname);
        tnumber = findViewById (R.id.matchednumber);
        one = (ImageView) findViewById(R.id.one);



        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // edit_text.setTextColor(getResources().getColor(red));
                edit_text.setText(edit_text.getText().toString() + "1");
                matchContact (edit_text.getText ().toString ());


            }
        });

        two = (ImageView) findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_text.setTextColor(getResources().getColor(R.color.green_50));
                matchContact (edit_text.getText ().toString ());


            }
        });

        three = (ImageView) findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "3");
                matchContact (edit_text.getText ().toString ());

            }
        });

        four = (ImageView) findViewById(R.id.four);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "4");
                matchContact (edit_text.getText ().toString ());

            }
        });

        five = (ImageView) findViewById(R.id.five);
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "5");
                matchContact (edit_text.getText ().toString ());

            }
        });

        six = (ImageView) findViewById(R.id.six);
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "6");
                matchContact (edit_text.getText ().toString ());

            }
        });

        seven = (ImageView) findViewById(R.id.seven);
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "7");
                matchContact (edit_text.getText ().toString ());

            }
        });

        eight = (ImageView) findViewById(R.id.eight);
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "8");
                matchContact (edit_text.getText ().toString ());

            }
        });

        nine = (ImageView) findViewById(R.id.nine);
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "9");
                matchContact (edit_text.getText ().toString ());

            }
        });

        zero = (ImageView) findViewById(R.id.zero);
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "0");
                matchContact (edit_text.getText ().toString ());

            }
        });
        zero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "+");
                matchContact (edit_text.getText ().toString ());

                return true;
            }
        });

        star = (ImageView) findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "*");
                matchContact (edit_text.getText ().toString ());

            }
        });
        star.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + ",");
                return true;
            }
        });


        hash = (ImageView) findViewById(R.id.hash);
        hash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_text.setText(edit_text.getText().toString() + "#");
                matchContact (edit_text.getText ().toString ());

//
//                if (edit_text.length() == 5) {
//
//                    num = edit_text.getText().toString();
//                    if (num.contains("#")) {
//                        num = num.replace("#", "%23");
//                    }
//
//                    if (num.contains("*") && num.charAt(0) == '*') {
//                        makePhoneCall();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Star is missing", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(MainActivity.this, "Wrong Number", Toast.LENGTH_SHORT).show();
//                }


            }
        });
        hash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + ";");

                return true;
            }
        });

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text.length() > 0) {

                    //to remove last added char or digit
                    String num1 = edit_text.getText().toString().substring(0, edit_text.length() - 1);
                    edit_text.setText(num1);
                }
            }
        });
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText("");
                return true;
            }
        });

        sim1 = (ImageView) findViewById(R.id.sim1);
        sim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = edit_text.getText().toString();
                if (num.contains("#")) {
                    num = num.replace("#", "%23");
                }

                makePhoneCall();
            }
        });
    }

    public void makePhoneCall() {

        //if there is already number we have to call and if the is no number we have to ask permmision
        if (num.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(MainActivity3.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                ActivityCompat.requestPermissions(MainActivity3.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

            } else {
                String dial = "tel:" + num;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(this, "Enter the PhoneNumber", Toast.LENGTH_SHORT).show();
        }
    }
    private void getContactList() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ContentResolver cr = getContentResolver();
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                    if ((cur != null ? cur.getCount() : 0) > 0) {
                        while (cur != null && cur.moveToNext()) {
                            String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));



                            if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                                while (pCur.moveToNext()) {
                                    String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                                    namelist.add (name);
                                    numberlist.add (phoneNo);

                                    Log.i("TAG----", "Name: " + name);
                                    Log.i("TAG----",  "Phone Number: " + phoneNo);
                                }
                                pCur.close();
                            }
                        }
                    }
                    if(cur!=null){
                        cur.close();
                    }


                }catch (SecurityException e){
                    e.printStackTrace ();
                }

            }
        });

    }

    public void matchContact(String contact){

        if (namelist.size () != 0 && numberlist.size () != 0){
            for (String num: numberlist){
                if (num.contains (contact)){
                    //tname.setText ();
                    tnumber.setText (num);
                    int nameindex = numberlist.indexOf (num);
                    tname.setText (namelist.get (nameindex));
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(MainActivity3.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                makePhoneCall();
            }


        } else {
            Toast.makeText(this, "need Permission", Toast.LENGTH_SHORT).show();
        }
    }
}
