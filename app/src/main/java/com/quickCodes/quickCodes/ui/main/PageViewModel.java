package com.quickCodes.quickCodes.ui.main;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {
    public MutableLiveData<HashMap<String, String>> numberlist;

    public MutableLiveData<HashMap<String, String>> getNumberlist(Context context) {
        if (numberlist == null) {
            numberlist = new MutableLiveData<>();
            getContactList(context);
        }
        return numberlist;
    }

    private void getContactList(Context context) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            HashMap<String, String> contacts = new HashMap<>();

            @Override
            public void run() {
                try {
                    ContentResolver cr = context.getContentResolver();
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

                                    contacts.put(phoneNo, name);

//                                    Log.i("TAG----", "Name: " + name);
//                                    Log.i("TAG----", "Phone Number: " + phoneNo);
                                }
                                pCur.close();
                            }
                        }
                    }
                    if (cur != null) {
                        cur.close();
                    }
                    numberlist.postValue(contacts);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //add contatcts to list of searchable items
//                            mAdapter.setContactList(namelist, numberlist);
//                        }
//                    });


                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
