package com.example.ussdhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.ussdhelper.ui.main.SectionsPagerAdapter;
import com.hover.sdk.api.Hover;
import com.hover.sdk.api.HoverParameters;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 20 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hover.initialize(this);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                EasyPermissions
//                //request for permissions
//                EasyPermissions.requestPermissions(
//                    new PermissionRequest.Builder(this,permsi, perms)
//                        .setRationale(R.string.camera_and_location_rationale)
//                        .setPositiveButtonText(R.string.rationale_ask_ok)
//                        .setNegativeButtonText(R.string.rationale_ask_cancel)
//                        .setTheme(R.style.my_fancy_style)
//                        .build());
//                Intent intent = new Intent(getApplicationContext(), ContactPickerActivity.class)
//                    .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.Theme_Light)
//                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
//                    .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, true)
//                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
//                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
//                    .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
//                startActivityForResult(intent, REQUEST_CONTACT);
//                startActivity(new Intent(getApplicationContext(),AddYourOwnActionActivity.class));
//                Intent intent = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
//                    intent = new Intent(Intent.ACTION_ALL_APPS, ContactsContract.Contacts.CONTENT_URI);
//                }
//                startActivityForResult(intent,REQUEST_CONTACT);
//                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                manager.sendUssdRequest("*175*4#",);
                String ussdCode = "*175*4"+ Uri.encode("#");
                startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+ussdCode)));
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
            data != null) {

            // we got a result from the contact picker
            Bundle extras = data.getExtras();
            Set keys = extras.keySet();
            Iterator iterator = keys.iterator();
            while(iterator.hasNext()){
                Toast.makeText(this, iterator.next().toString(), Toast.LENGTH_SHORT).show();
            }

//            // process contacts
//            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
//            for (Contact contact : contacts) {
//                // process the contacts...
//            }
//
//            // process groups
//            List<Group> groups = (List<Group>) data.getSerializableExtra(ContactPickerActivity.RESULT_GROUP_DATA);
//            for (Group group : groups) {
//                // process the groups...
//            }
        }
    }
}
