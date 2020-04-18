package com.quickCodes.quickCodes.screenOverlays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.Constants;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.UssdDetector;
import com.quickCodes.quickCodes.util.database.DataRepository;

import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import static com.quickCodes.quickCodes.AddYourOwnActionActivity.containsIgnoreCase;

public class PhoneCallsOverlayService extends LifecycleService {
    View chatHead;
    String TAG = "PHONE OVERLAY SERVICE";
    private WindowManager windowManager;
    String code;
    DataRepository dataRepository;
    List<UssdActionWithSteps> allUssdActions;

    Long codeId;
    UssdAction action;

    public PhoneCallsOverlayService() {
        dataRepository = new DataRepository(getApplication());
        allUssdActions = dataRepository.getAllUssdActionsNoLiveData();
        Log.d(TAG, allUssdActions.toString());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences =
            this.getSharedPreferences(UssdDetector.AUTO_SAVED_CODES, Context.MODE_PRIVATE);
        code = preferences.getString("code", null);
        code = code.replace("#", "").replace(",", "*").concat("#");
        //make preference null suchthat the same code is not shown again
        preferences.edit().putString("code", null).commit();

        //inflate the chat head layout
        chatHead = LayoutInflater.from(this).inflate(R.layout.overlay_phone_call, null);
        //add the view to the window
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        //Specify the overlay position
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
//        params.x =200;
//        params.alpha = (float) 0.5;

        //add view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHead, params);

        //set the saved text
        TextView textViewCode = chatHead.findViewById(R.id.textView_code_summary);
        TextView textViewDesc = chatHead.findViewById(R.id.textView_description);
        if (code != null) {
            textViewCode.setText(code);
        }


        LinearLayout linearLayoutSaveCode = chatHead.findViewById(R.id.linearLayout_save_code);
        LinearLayout linearLayoutEditCode = chatHead.findViewById(R.id.linearLayout_edit_code);
        LinearLayout linearLayoutRedialCode = chatHead.findViewById(R.id.linearLayout_redial_code);
        LinearLayout linearLayoutDeleteCode = chatHead.findViewById(R.id.linearLayout_delete_code);
        LinearLayout linearLayoutShowMeThisCode = chatHead.findViewById(R.id.linearLayout_show_me_this_code);

        linearLayoutSaveCode.setOnClickListener(v -> saveCode());
        linearLayoutEditCode.setOnClickListener(v -> editCode());
        linearLayoutRedialCode.setOnClickListener(v -> redialCode());
        linearLayoutDeleteCode.setOnClickListener(v -> deleteCode());
        linearLayoutShowMeThisCode.setOnClickListener(v -> showCode());

        //check if code already exists in the database
        boolean exists = false;

        String mycode = code.replace("#", "");
        if (allUssdActions != null) {
            for (UssdActionWithSteps a : allUssdActions) {
                if (containsIgnoreCase(a.action.getAirtelCode(), mycode)) exists = true;
                if (containsIgnoreCase(a.action.getMtnCode(), mycode)) exists = true;
                if (containsIgnoreCase(a.action.getAfricellCode(), mycode)) exists = true;

            }
        } else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
        if (exists) {
            chatHead.findViewById(R.id.linearLayout_buttons).setVisibility(View.GONE);
            chatHead.findViewById(R.id.linearLayout_Already_Exists).setVisibility(View.VISIBLE);
            textViewDesc.setText("This code already saved in quick codes");
        } else {
            //save the code to the database
            Random r = new Random();
            codeId = r.nextLong();//TODO change random number generator
            action = new UssdAction(codeId, code, code.replace("#", ""), null, null, Constants.SEC_USER_DIALED);
            dataRepository.insertAll(new UssdActionWithSteps(action, null));
        }


        //Set the close button.
        ImageView closeButton = (ImageView) chatHead.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close the service and remove the chat head from the window
                stopSelf();
            }
        });
    }

    private void showCode() {
        Intent intent = new Intent(PhoneCallsOverlayService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("action_id", String.valueOf(codeId));
        startActivity(intent);
    }

    private void editCode() {
        Intent intent = new Intent(PhoneCallsOverlayService.this, EditActionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("action_id", String.valueOf(codeId));
        startActivity(intent);
        stopSelf();
    }

    private void saveCode() {
        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();


    }

    private void redialCode() {
        Toast.makeText(this, "redial", Toast.LENGTH_SHORT).show();
        String fullCode = code.replace("#", "") + Uri.encode("#");
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }

    private void deleteCode() {
        Toast.makeText(this, "Successfully deleted", Toast.LENGTH_SHORT).show();
        dataRepository.delete(action);
        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
