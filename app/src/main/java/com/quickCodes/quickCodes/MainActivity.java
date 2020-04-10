package com.quickCodes.quickCodes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.quickCodes.quickCodes.dialpad.DialPadActivity;
import com.quickCodes.quickCodes.screenOverlays.ChatHeadService;
import com.quickCodes.quickCodes.ui.main.SectionsPagerAdapter;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 20 ;
    private static final int REQUEST_CODE = 40 ;
    private static final int CONTACT_PICKER_REQUEST = 90;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 101 ;
    String edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        edit = intent.getStringExtra("edit");



        //ask for permissions
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    //if permission granted
                    if(report.areAllPermissionsGranted()){
                        setContentView(R.layout.activity_main);
                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());

                        ViewPager viewPager = findViewById(R.id.view_pager);
                        viewPager.setAdapter(sectionsPagerAdapter);
                        if(edit!= null){
                            viewPager.setCurrentItem(1);
                        }
                        TabLayout tabs = findViewById(R.id.tabs);
                        tabs.setupWithViewPager(viewPager);
                        FloatingActionButton fab = findViewById(R.id.fab);

                        setupToolBar();

                            //draw over other apps
//                        initializeView(getApplicationContext());




                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, DialPadActivity.class));
//                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"")));
                            }
                        });

                    }else{
//                        DialogOnAnyDeniedMultiplePermissionsListener.Builder
//                            .withContext(getApplicationContext())
//                            .withTitle("Contacts and Phone State")
//                            .withMessage("These permissions are needed to detect your sim cards")
//                            .withButtonText("Continue")
//                            .withIcon(R.mipmap.ic_launcher)
//                            .build();
                        Toast.makeText(MainActivity.this, "This app requires the  requested permissions to work", Toast.LENGTH_LONG).show();
//                        Toast.makeText(MainActivity.this, "The app might not work, Please go to setting and grant permissions", Toast.LENGTH_LONG).show();


                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();

                }
            })
            .onSameThread()
            .check();

    }
    /**
     * Set and initialize the view elements.
     */
    public void initializeView(Context context) {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            startService(new Intent(context, ChatHeadService.class));
//                finish();
        }
    }
    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
    }
    private void shareApp() {
        String store_url = "https://play.google.com/store/apps/details?id=com.quickCodes.quickCodes";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,store_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent,"Thank you for sharing , continue with"),null);
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
        }
        if(requestCode== REQUEST_CODE && requestCode== Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            Set keys = extras.keySet();
            Iterator iterator = keys.iterator();
            while(iterator.hasNext()){
                Toast.makeText(this, iterator.next().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.share:
                shareApp();
                break;
            case R.id.help:
                startActivity(new Intent(getApplicationContext(),HelpActivity.class));
                break;
            case R.id.add_action:
                startActivity(new Intent(getApplicationContext(),AddYourOwnActionActivity.class));
                break;
            case R.id.contacts:
                Intent contactsIntent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                startActivity(contactsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


}
