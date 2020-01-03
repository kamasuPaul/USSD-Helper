package com.quickCodes.quickCodes;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity2 extends AppCompatActivity {

TextView number,t1,t2,t3,t4,t5,t6,t7,t8,t9,t0,tstar,th,tname,tnumber;
ImageView cancel;
AppCompatImageView imageViewCall;

String userinput = null;



ArrayList<String> namelist = new ArrayList<> ();
ArrayList<String> numberlist = new ArrayList<> ();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main2);

        findview ();
        checkpermission ();
        getContactList ();


        t1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("1");
                matchContact (number.getText ().toString ());
            }
        });

        t2.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("2");
                matchContact (number.getText ().toString ());
            }
        });

        t3.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("3");
                matchContact (number.getText ().toString ());
            }
        });

        t4.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("4");
                matchContact (number.getText ().toString ());
            }
        });

        t5.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("5");
                matchContact (number.getText ().toString ());
            }
        });

        t6.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("6");
                matchContact (number.getText ().toString ());
            }
        });

        t7.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("7");
                matchContact (number.getText ().toString ());
            }
        });

        t8.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("8");
                matchContact (number.getText ().toString ());
            }
        });

        t9.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("9");
                matchContact (number.getText ().toString ());
            }
        });

        t0.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("0");
                matchContact (number.getText ().toString ());
            }
        });

        th.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("#");
                matchContact (number.getText ().toString ());
            }
        });

        tstar.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                number.append ("*");
                matchContact (number.getText ().toString ());
            }
        });

        imageViewCall.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {

                userinput = number.getText ().toString ();
                PackageManager pm = getApplicationContext ().getPackageManager();
                int hasPerm = pm.checkPermission(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        getApplicationContext ().getPackageName());
                if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + userinput));
                    startActivity(intent);
                }

            }
        });
        
        cancel.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                userinput = number.getText ().toString ();
                if (!userinput.equals ("")) {
                    String newStr = userinput.substring (0, userinput.length () - 1);
                    number.setText (newStr);
                }
                matchContact (number.getText ().toString ());
            }
        });

    }

    public void findview(){
        number = findViewById (R.id.number);
        t1 = findViewById (R.id.one);
        t2 = findViewById (R.id.two);
        t3 = findViewById (R.id.three);
        t4 = findViewById (R.id.four);
        t5 = findViewById (R.id.five);
        t6 = findViewById (R.id.six);
        t7 = findViewById (R.id.seven);
        t8 = findViewById (R.id.eight);
        t9 = findViewById (R.id.nine);
        t0 = findViewById (R.id.zero);
        tstar = findViewById (R.id.star);
        th = findViewById (R.id.hatch);
        imageViewCall = findViewById (R.id.call);
        tname = findViewById (R.id.matchedname);
        tnumber = findViewById (R.id.matchednumber);

        cancel = findViewById (R.id.cancel);
    }

    public void checkpermission(){
        if ((ContextCompat.checkSelfPermission (getApplicationContext (), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission (getApplicationContext (), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission (getApplicationContext (), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions (MainActivity2.this,new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS}, 1);
        }else{
            Toast.makeText (this, "permission not granted", Toast.LENGTH_SHORT).show ();
        }
    }

    private void getContactList() {

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
}
