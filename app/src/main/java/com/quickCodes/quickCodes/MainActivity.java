package com.quickCodes.quickCodes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.quickCodes.quickCodes.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 20 ;
    private static final int REQUEST_CODE = 40 ;
    private static final int CONTACT_PICKER_REQUEST = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                        TabLayout tabs = findViewById(R.id.tabs);
                        tabs.setupWithViewPager(viewPager);
                        FloatingActionButton fab = findViewById(R.id.fab);

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"")));
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

        setupToolBar();
    }
    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
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
                Toast.makeText(this, "share ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_action:
                startActivity(new Intent(getApplicationContext(),AddYourOwnActionActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }
}
