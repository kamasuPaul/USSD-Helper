package com.example.ussdhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
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
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 20 ;
    private static final int CONTACT_PICKER_REQUEST = 90;

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
                startActivity(new Intent(getApplicationContext(),AddYourOwnActionActivity.class));
//                Intent intent = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ECLAIR) {
//                    intent = new Intent(Intent.ACTION_ALL_APPS, ContactsContract.Contacts.CONTENT_URI);
//                }
//                startActivityForResult(intent,REQUEST_CONTACT);
//                TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                manager.sendUssdRequest("*175*4#",);
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
    public void contactPicker(Context context) {
        new MultiContactPicker.Builder(this) //Activity/fragment context
//                    .theme(R.style.MyCustomPickerTheme) //Optional - default: MultiContactPicker.Azure
            .hideScrollbar(false) //Optional - default: false
            .showTrack(true) //Optional - default: true
            .searchIconColor(Color.WHITE) //Option - default: White
            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
            .handleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
            .bubbleColor(ContextCompat.getColor(this, R.color.colorPrimary)) //Optional - default: Azure Blue
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

}
