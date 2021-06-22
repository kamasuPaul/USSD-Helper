package com.quickCodes.quickCodes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.quickCodes.quickCodes.dialpad.DialPadActivity;
import com.quickCodes.quickCodes.ui.main.SectionsPagerAdapter;
import com.quickCodes.quickCodes.util.Tools;
import com.quickCodes.quickCodes.util.UssdDetector;

import static com.quickCodes.quickCodes.util.PermissionsActivity.CODE_ACCESSIBILITY;
import static com.quickCodes.quickCodes.util.Tools.CONTACT_PICKER_REQUEST;
import static com.quickCodes.quickCodes.util.Tools.isBeastModeOn;
import static com.quickCodes.quickCodes.util.Tools.setBeastModeOn;

public class MainActivity extends AppCompatActivity {
    public static boolean accessibilityServiceShouldRun = false;
    String edit;
    public static String action_id = null;
    Switch beastMode;

    public static void openDialer(Context context) {
//        context.startActivity(new Intent(context, DialPadActivity.class));
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        edit = intent.getStringExtra("edit");
        action_id = intent.getStringExtra("action_id");

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

        fab.setOnClickListener(view -> openDialer(MainActivity.this));
        fabAdd.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddYourOwnActionActivity.class));
        });
        fabContacts.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)));


        //move the fragment
//        if(action_id!=null){
//            viewPager.se
//        }
        //show accessibility setting if its off
        if (accessibilityOff()) {
            showDialogAbout();
        }
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar1);
        beastMode = toolbar.findViewById(R.id.switch_beast_mode);
        setupBeastMode(beastMode);
        setSupportActionBar(toolbar);
    }

    private void shareApp() {
        String store_url = "https://play.google.com/store/apps/details?id=com.quickCodes.quickCodes";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, store_url);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Thank you for sharing , continue with"), null);
    }

    private void setupBeastMode(Switch beastMode) {
        beastMode.setChecked(isBeastModeOn(MainActivity.this));
        beastMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!Tools.isAccessibilityServiceEnabled(getApplicationContext(), UssdDetector.class)) {
                        showDialogAbout();
                    } else {
                        Tools.setBeastModeOn(MainActivity.this, true);
                        beastMode.setChecked(Tools.isBeastModeOn(MainActivity.this));
                    }

                } else {
                    setBeastModeOn(MainActivity.this, false);
                    beastMode.setChecked(Tools.isBeastModeOn(MainActivity.this));
                }
            }
        });
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
        if (requestCode == CODE_ACCESSIBILITY) {
            if (Tools.isAccessibilityServiceEnabled(getApplicationContext(), UssdDetector.class)) {
                Tools.setBeastModeOn(MainActivity.this, true);
                beastMode.setChecked(Tools.isBeastModeOn(MainActivity.this));
            } else {
                Tools.setBeastModeOn(MainActivity.this, false);
                beastMode.setChecked(Tools.isBeastModeOn(MainActivity.this));
            }
        }
    }

    public void showDialogAbout() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_set_accessibility);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //change the status of switch whenever the dialog closes
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                beastMode.setChecked(isBeastModeOn(MainActivity.this));
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((MaterialRippleLayout) dialog.findViewById(R.id.bt_rate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, CODE_ACCESSIBILITY);

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private boolean accessibilityOff() {
        return !Tools.isAccessibilityServiceEnabled(getApplicationContext(), UssdDetector.class);
    }
}
