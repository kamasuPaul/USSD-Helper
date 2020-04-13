package com.quickCodes.quickCodes.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.quickCodes.quickCodes.fragments.MainFragment.CODE_DRAW_OVER_OTHER_APP_PERMISSION;

public class PermissionsActivity extends AppCompatActivity {

    private Button btn_permissins,btn_drawOverApps;
    private Button btn_continue;
    private ImageView imgView_permissions,imageView_draw;
    private boolean drawGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();

        setContentView(R.layout.activity_permissions);


        //permissions
        btn_permissins = findViewById(R.id.button_set_permissions);
        btn_drawOverApps = findViewById(R.id.button_draw_overApps);
        btn_continue = findViewById(R.id.button_continue);

        imgView_permissions = findViewById(R.id.imageView_set_permissions);
        imageView_draw =      findViewById(R.id.imageView_draw_overApps);

        //request permissions on click
        btn_permissins.setOnClickListener(v -> {
            requestPermissions();
            updateUi();

        });

        //REQUEST DRAW OVER OTHER APPS PERMISSION
        btn_drawOverApps.setOnClickListener(v->{
            requestToDrawOverApps(getApplicationContext());
            updateUi();
        });

        //GOTO MAIN ACTIVITY ON CLICK OF CONTINUE BUTTON
        btn_continue.setEnabled(false);
        btn_continue.setOnClickListener(v -> {
            startActivity(new Intent(PermissionsActivity.this, MainActivity.class));
            finish();
        });
    }

    private boolean requestPermissions() {
        final boolean[] all_granted = new boolean[1];
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    //if permission granted
                    if(report.areAllPermissionsGranted()){
                        updateUi();
                            all_granted[0] = true;
                    }else if (report.isAnyPermissionPermanentlyDenied()){
                        //TODO open setting activity , where permissions can be set
                    } else{
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
     * @return
     */
    private void requestToDrawOverApps(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            //permission already granted
            drawGranted = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CODE_DRAW_OVER_OTHER_APP_PERMISSION){
            //permission granted
            if(resultCode==RESULT_OK)            drawGranted = true;
            updateUi();
            Toast.makeText(this, "draw granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUi() {
        boolean permission = false;
        boolean draw = false;
        //check if permissions have been granted
        if(checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            if(checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
                if(checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
                        permission = true;//all permissions have been granted;
                    imgView_permissions.setVisibility(View.VISIBLE);
                    btn_permissins.setVisibility(View.GONE);

                }
            }
        }
        //check if draw over other apps has been granted
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)){
            draw = true;
            imageView_draw.setVisibility(View.VISIBLE);
            btn_drawOverApps.setVisibility(View.GONE);
        }

        //update ui if both them are true
        if(permission&&draw){
            btn_continue.setEnabled(true);
        }
    }
    private void checkPermissions() {
        boolean permission = false;
        boolean draw = false;
        //check if permissions have been granted
        if(checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            if(checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
                if(checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED){
                    permission = true;//all permissions have been granted;
                }
            }
        }
        //check if draw over other apps has been granted
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)){
            draw = true;
        }

        //update ui if both them are true
        if(permission&&draw){
            startActivity(new Intent(PermissionsActivity.this, MainActivity.class));
            finish();
        }
    }
}
