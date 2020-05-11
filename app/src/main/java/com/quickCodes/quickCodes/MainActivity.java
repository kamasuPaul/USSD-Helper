package com.quickCodes.quickCodes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.quickCodes.quickCodes.dialpad.DialPadActivity;
import com.quickCodes.quickCodes.ui.main.SectionsPagerAdapter;
import com.quickCodes.quickCodes.util.Tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private static final int CONTACT_PICKER_REQUEST = 29;
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
        //setup floating action buttons
        FloatingActionButton fab = findViewById(R.id.dialer);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        FloatingActionButton fabContacts = findViewById(R.id.fab_contacts);

        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DialPadActivity.class)));
        fabAdd.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddYourOwnActionActivity.class)));
        fabContacts.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)));

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
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int numberIdex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIdex);
                    if (Tools.phoneNumber != null) {
                        if (number.startsWith("+256")) {
                            number = number.replace("+256", "0");
                        }
                        number = number.replace(" ", "");
                        Tools.setTelephone(number);
                    }
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No contact selected", Toast.LENGTH_SHORT).show();
                System.out.println("User closed the picker without selecting items.");
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
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.search:
                gotoSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    private void gotoSearch() {
        Intent searchActivityItent = new Intent(getApplicationContext(), DialPadActivity.class);
        searchActivityItent.putExtra("search", "search");
        startActivity(searchActivityItent);
    }
}
