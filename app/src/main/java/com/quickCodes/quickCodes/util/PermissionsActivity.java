package com.quickCodes.quickCodes.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class PermissionsActivity extends AppCompatActivity {

    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 101;
    public static final int CODE_ACCESSIBILITY = 102;
    public static final String ASK_ACCESSIBILITY = "ask_accessibility";
    private static final String TAG = "USSD DETECTOR";
    public static final String ASK_TIMES = "ask_times";
    public static final String ASK_TIMES_DRAW = "ask_times_draw";
    private Button btn_permissions, btn_drawOverApps, btn_accesibility;
    private Button btn_continue;
    private ImageView imgView_permissions, imageView_draw, imageView_accesibility;
    SharedPreferences sharedPreferences;
//    TextView askTimes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(ASK_ACCESSIBILITY, MODE_PRIVATE);
        checkPermissions();

        setContentView(R.layout.activity_permissions);


        //initalize buttons and image views
//        askTimes = findViewById(R.id.ask_times);
//        askTimes.setText(String.valueOf(sharedPreferences.getInt(ASK_TIMES_DRAW,-1)));

        btn_permissions = findViewById(R.id.button_set_permissions);
        btn_drawOverApps = findViewById(R.id.button_draw_overApps);
        btn_continue = findViewById(R.id.button_continue);
        btn_accesibility = findViewById(R.id.button_accessibility);

        imgView_permissions = findViewById(R.id.imageView_set_permissions);
        imageView_draw = findViewById(R.id.imageView_draw_overApps);
        imageView_accesibility = findViewById(R.id.imageView_accessibility);

        //request permissions on click
        btn_permissions.setOnClickListener(v -> {
            requestPermissions();
            updateUi();

        });

        //REQUEST DRAW OVER OTHER APPS PERMISSION
        btn_drawOverApps.setOnClickListener(v -> {
            requestToDrawOverApps(getApplicationContext());
            updateUi();
        });
        //REQUEST ACCESIBILITY
        btn_accesibility.setOnClickListener(v -> {
            requestAccessibility(getApplicationContext());
            updateUi();
        });

        //GOTO MAIN ACTIVITY ON CLICK OF CONTINUE BUTTON
        btn_continue.setEnabled(false);
        btn_continue.setOnClickListener(v -> {
            startActivity(new Intent(PermissionsActivity.this, MainActivity.class));
            finish();
        });

        //set style for buttons
        btn_accesibility.setBackground(getResources().getDrawable(R.drawable.btn_rounded_white_outline));
        btn_drawOverApps.setBackground(getResources().getDrawable(R.drawable.btn_rounded_white_outline));
        btn_permissions.setBackground(getResources().getDrawable(R.drawable.btn_rounded_white_outline));
        btn_continue.setBackground(getResources().getDrawable(R.drawable.btn_rounded_white_outline));
    }

    private void requestAccessibility(Context applicationContext) {

        //ask for accessibility twice every week, this is for phones that are battery optimized
        //or have limited resources,which leads to auto disabling accessibility
        if (!sharedPreferences.contains(ASK_TIMES)) {
            sharedPreferences.edit().putInt(ASK_TIMES, 2).commit();
            PeriodicWorkRequest accessibilityWorker =
                new PeriodicWorkRequest.Builder(AskAccessibilityWorker.class, 7, TimeUnit.DAYS)
                    .build();
            WorkManager.getInstance(PermissionsActivity.this).enqueue(accessibilityWorker);
        }

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, CODE_ACCESSIBILITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //permission granted
//            if (resultCode == RESULT_OK) drawGranted = true;
            updateUi();
            if (isDrawOverOtherAppsGranted(getApplicationContext())) {
                sharedPreferences.edit().putInt(ASK_TIMES_DRAW, sharedPreferences.getInt(ASK_TIMES_DRAW, 2) - 1).commit();
            }
//            Toast.makeText(this, "draw granted", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == CODE_ACCESSIBILITY) {
            if (isAccessibilitySettingsOn(getApplicationContext())) {
                sharedPreferences.edit().putInt(ASK_TIMES, sharedPreferences.getInt(ASK_TIMES, 2) - 1).commit();
            }
            updateUi();
        }
    }

    private void updateUi() {
        boolean permission = false;
        boolean draw = false;
        boolean accessibility;
        //check if permissions have been granted
        if (checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            if (checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    permission = true;//all permissions have been granted;
                    imgView_permissions.setVisibility(View.VISIBLE);
                    btn_permissions.setVisibility(View.GONE);

                }
            }
        }
        //check if draw over other apps has been granted
        if (isDrawOverOtherAppsGranted(this)) {
            draw = true;
            imageView_draw.setVisibility(View.VISIBLE);
            btn_drawOverApps.setVisibility(View.GONE);
        }
        //check for accesibility granted
        accessibility = isAccessibilitySettingsOn(PermissionsActivity.this);
        if (accessibility) {
            imageView_accesibility.setVisibility(View.VISIBLE);
            btn_accesibility.setVisibility(View.GONE);
        }

        //update ui if both them are true
        if (permission && draw && accessibility) {
            btn_continue.setEnabled(true);
        }
//        askTimes.setText(String.valueOf(sharedPreferences.getInt(ASK_TIMES_DRAW,-1)));
    }

    /**
     * copied from stackoverflow
     * https://stackoverflow.com/questions/18094982/detect-if-my-accessibility-service-is-enabled/18095283
     *
     * @param mContext
     * @return
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int askTimes = sharedPreferences.getInt(ASK_TIMES, 8);
        if (askTimes <= 0) {
            return true;
        }
        return Tools.isAccessibilityServiceEnabled(mContext, UssdDetector.class);
    }

    private void checkPermissions() {
        boolean permission = false;
        boolean draw = false;
        boolean accessibility = false;
        //check if permissions have been granted
        if (checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            if (checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    permission = true;//all permissions have been granted;
                }
            }
        }
        //check if draw over other apps has been granted
        if (isDrawOverOtherAppsGranted(this)) {
            draw = true;
        }

        //check accessibility
        if (isAccessibilitySettingsOn(getApplicationContext())) {
            accessibility = true;
        }

        //update ui if all them are true
        if (permission && draw && accessibility) {
            startActivity(new Intent(PermissionsActivity.this, MainActivity.class));
            finish();
        }
    }

    private boolean requestPermissions() {
        final boolean[] all_granted = new boolean[1];
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE
//                Manifest.permission.READ_CALL_LOG,
//                Manifest.permission.PROCESS_OUTGOING_CALLS

            )
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    //if permission granted
                    if (report.areAllPermissionsGranted()) {
                        updateUi();
                        all_granted[0] = true;
                    } else if (report.isAnyPermissionPermanentlyDenied()) {
                        //TODO open setting activity , where permissions can be set
                    } else {
                        DialogOnAnyDeniedMultiplePermissionsListener dialog = DialogOnAnyDeniedMultiplePermissionsListener.Builder
                            .withContext(PermissionsActivity.this)
                            .withTitle("Contacts and Phone State")
                            .withMessage("These permissions are needed to detect your sim cards for easy use")
                            .withButtonText("Continue")
                            .withIcon(R.mipmap.ic_launcher)
                            .build();
                        dialog.onPermissionsChecked(report);
//                        requestPermissions();//aske permission again after showing dialog

//                        for (PermissionDeniedResponse deniedResponse:report.getDeniedPermissionResponses()
//                             ) {
//                            deniedResponse.getRequestedPermission();
//                            DexterActivity
//
//                        }

                        Toast.makeText(PermissionsActivity.this, "This app requires the  requested permissions to work", Toast.LENGTH_LONG).show();
//                        Toast.makeText(MainActivity.this, "The app might not work, Please go to setting and grant permissions", Toast.LENGTH_LONG).show();
                        all_granted[0] = false;

                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    Toast.makeText(PermissionsActivity.this, "This app requires the  requested permissions to work,go to setting to set them", Toast.LENGTH_LONG).show();
                    token.continuePermissionRequest();

                }
            })
            .onSameThread()
            .check();
        return all_granted[0];
    }

    /**
     * Check if the application has draw over other apps permission or not?,
     * This permission is by default available for API<23. But for API > 23,
     * If the draw over permission is not available open the settings screen to grant the permission.
     *
     * @return
     */
    private void requestToDrawOverApps(Context context) {

        //ask to draw over apps once every week if the dr, this is for phones that are battery optimized
        //or have limited resources,which leads to auto disabling accessibility
        if (!sharedPreferences.contains(ASK_TIMES_DRAW)) {
            sharedPreferences.edit().putInt(ASK_TIMES_DRAW, 2).commit();
            PeriodicWorkRequest drawWorker =
                new PeriodicWorkRequest.Builder(AskDrawOverAppsWorker.class, 7, TimeUnit.DAYS)
                    .build();
            WorkManager.getInstance(PermissionsActivity.this).enqueue(drawWorker);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
        //permission already granted on android versions less than Marshmallow
    }

    /**
     * check if  the app is allowed to draw over other apps
     */
    public boolean isDrawOverOtherAppsGranted(Context context) {
        boolean draw = false;
        int askTimes = sharedPreferences.getInt(ASK_TIMES_DRAW, 8);
        if (askTimes <= 0) {
            return true;
        }
        //This permission is by default available for API<23. But not available for API > 23,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(context)) {
            draw = true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            draw = true;
        }
        return draw;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi();
    }

}
