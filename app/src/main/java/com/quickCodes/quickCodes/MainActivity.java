package com.quickCodes.quickCodes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.quickCodes.quickCodes.dialpad.DialPadActivity;
import com.quickCodes.quickCodes.ui.main.SectionsPagerAdapter;

import java.util.Iterator;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 20;
    private static final int REQUEST_CODE = 40;
    private static final int CONTACT_PICKER_REQUEST = 90;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 101;
    public static boolean accessibilityServiceShouldRun = false;
    String edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        edit = intent.getStringExtra("edit");

        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        if (edit != null) {
            viewPager.setCurrentItem(1);
        }
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setupToolBar();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DialPadActivity.class)));


    }


    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
    }

    private void shareApp() {
        String store_url = "https://play.google.com/store/apps/details?id=com.quickCodes.quickCodes";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, store_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Thank you for sharing , continue with"), null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
            data != null) {

            // we got a result from the contact picker
            Bundle extras = data.getExtras();
            Set keys = extras.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Toast.makeText(this, iterator.next().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE && requestCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Set keys = extras.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                Toast.makeText(this, iterator.next().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.share:
                shareApp();
                break;
            case R.id.help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.add_action:
                startActivity(new Intent(getApplicationContext(), AddYourOwnActionActivity.class));
                break;
            case R.id.contacts:
                Intent contactsIntent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                startActivity(contactsIntent);
                break;
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }


}
