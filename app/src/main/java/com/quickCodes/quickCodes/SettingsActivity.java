package com.quickCodes.quickCodes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.quickCodes.quickCodes.util.Tools;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.settings, new SettingsFragment())
            .commit();

        setupToolBar();
    }

    public void showFaq(View view) {
        startActivity(new Intent(getApplicationContext(), HelpActivity.class));

    }

    //    public void showDialogAbout(View view) {
//        View aboutView = getLayoutInflater().inflate(R.layout_no_item.dialog_about, null);
//        Dialog d = new Dialog(getApplicationContext());
//        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        d.setContentView(aboutView);
//        d.setCancelable(true);
//        d.show();
//    }
    public void showDialogAbout(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText("Version " + BuildConfig.VERSION_NAME);


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((MaterialRippleLayout) dialog.findViewById(R.id.bt_rate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.rateAction(SettingsActivity.this);
        }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showPrivacyPolicy(View view) {
        String url = "https://quick-codes.flycricket.io/privacy.html";
        Intent t = new Intent(Intent.ACTION_VIEW);
        t.setData(Uri.parse(url));
        startActivity(t);
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

}
