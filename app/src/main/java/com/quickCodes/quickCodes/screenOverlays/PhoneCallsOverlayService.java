package com.quickCodes.quickCodes.screenOverlays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
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
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.database.DataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_PHONE;
import static com.quickCodes.quickCodes.AddYourOwnActionActivity.containsIgnoreCase;
import static com.quickCodes.quickCodes.util.UssdDetector.AUTO_SAVED_CODES;
import static com.quickCodes.quickCodes.util.UssdDetector.STEP_TEL;

public class PhoneCallsOverlayService extends LifecycleService {
    View chatHead;
    String TAG = "PHONE OVERLAY SERVICE";
    private WindowManager windowManager;
    String code;
    String menuItem;
    DataRepository dataRepository;
    List<UssdActionWithSteps> allUssdActions;

    Long codeId;
    UssdAction action;

    public PhoneCallsOverlayService() {
        dataRepository = new DataRepository(getApplication());
        allUssdActions = dataRepository.getAllUssdActionsNoLiveData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }


    public static boolean myequalsIgnoreCase(String s, String substring) {
        boolean b = false;
        if (s != null) {
            b = s.equalsIgnoreCase(substring);
        }
        return b;
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
        stopSelf();

    }

    private void redialCode() {
//        Toast.makeText(this, "redial", Toast.LENGTH_SHORT).show();
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

    private void showCode() {
        Intent intent = new Intent(PhoneCallsOverlayService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("action_id", String.valueOf(codeId));
        startActivity(intent);
        stopSelf();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences =
            this.getSharedPreferences(AUTO_SAVED_CODES, Context.MODE_PRIVATE);
        code = preferences.getString("code", null);
        menuItem = preferences.getString("menuItem", null);
        if (menuItem != null) {
            int comaposition = code.indexOf(",");
            if (comaposition == -1) {
                menuItem = code
                    + menuItem
                    .replaceAll("[.,*]", "")
                    .replaceAll("\\s", "")
                    .replace(" ", "");
            } else {
                menuItem = code.substring(0, comaposition)
                    + menuItem
                    .replaceAll("[.,*]", "")
                    .replaceAll("\\s", "")
                    .replace(" ", "");
            }

        }
        code = code.replace("#", "").replace(",", "*").concat("#");


        //inflate the layout_no_item
        chatHead = LayoutInflater.from(this).inflate(R.layout.overlay_phone_call, null);

        //specify the window stuff
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? TYPE_APPLICATION_OVERLAY : TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        //Specify the overlay position
        params.gravity = Gravity.BOTTOM | Gravity.LEFT;

        //add view to the window
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(chatHead, params);

        //set the textview text
        TextView textViewCode = chatHead.findViewById(R.id.textView_code_summary);
        TextView textViewDesc = chatHead.findViewById(R.id.textView_description);
        TextView textViewMenu = chatHead.findViewById(R.id.textView_desc_menu);
        if (code != null) {
            textViewCode.setText(code);
        }
        if (menuItem != null) {
            textViewMenu.setText(menuItem);
        } else {
            //make the menu item not null
            menuItem = code.toString();
        }


        LinearLayout linearLayoutSaveCode = chatHead.findViewById(R.id.linearLayout_save_code);
        LinearLayout linearLayoutEditCode = chatHead.findViewById(R.id.linearLayout_edit_code);
        LinearLayout linearLayoutRedialCode = chatHead.findViewById(R.id.linearLayout_redial_code);
        LinearLayout linearLayoutDeleteCode = chatHead.findViewById(R.id.linearLayout_delete_code);
        LinearLayout linearLayoutShowMeThisCode = chatHead.findViewById(R.id.linearLayout_show_me_this_code);

        //add listener to  views on the layout_no_item
        linearLayoutSaveCode.setOnClickListener(v -> saveCode());
        linearLayoutEditCode.setOnClickListener(v -> editCode());
        linearLayoutRedialCode.setOnClickListener(v -> redialCode());
        linearLayoutDeleteCode.setOnClickListener(v -> deleteCode());
        linearLayoutShowMeThisCode.setOnClickListener(v -> showCode());


        //Set the close button.
        ImageView closeButton = (ImageView) chatHead.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(v -> {
            //close the service and remove the chat head from the window
            stopSelf();
        });

        //check if the ussdcode already exists in the database
        boolean matches = false;
        boolean is_contained = false;
        String code_name = "";


        String mycode = code.replace("#", "");
        if (allUssdActions != null) {
            for (UssdActionWithSteps a : allUssdActions) {
                code_name = a.action.getName();//the name will not be used if its null;
                if (myequalsIgnoreCase(a.action.getAirtelCode(), mycode)) matches = true;
                if (myequalsIgnoreCase(a.action.getMtnCode(), mycode)) matches = true;
                if (myequalsIgnoreCase(a.action.getAfricellCode(), mycode)) matches = true;
                if (containsIgnoreCase(a.action.getAirtelCode(), (mycode))) is_contained = true;
                if (containsIgnoreCase(a.action.getMtnCode(), mycode)) is_contained = true;
                if (containsIgnoreCase(a.action.getAfricellCode(), mycode)) is_contained = true;
                if (matches || is_contained) {
                    codeId = a.action.getActionId();
                    break;
                }
            }
        } else {
//            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }

        /**
         * if inside the app, only show unsaved codes
         * if outside the app,show both unsaved and saved codes
         */

//        if it matches some item in the database tell the use they could use this
        if (matches) {
            if (MainActivity.accessibilityServiceShouldRun == false) {
                //if inside the app and it matches
                //dont show this overlay
                stopSelf();
            }
            chatHead.findViewById(R.id.linearLayout_buttons).setVisibility(View.GONE);
            chatHead.findViewById(R.id.linearLayout_Already_Exists).setVisibility(View.VISIBLE);
            textViewDesc.setText("This code already saved in quick codes with name: ");
            textViewMenu.setText(code_name);

        } else if (is_contained) {//incase it is just contained in something
            if (MainActivity.accessibilityServiceShouldRun == false) {
                //if inside the app and the code is contained also
                //dont show this overlay
                stopSelf();
            }
            chatHead.findViewById(R.id.linearLayout_buttons).setVisibility(View.GONE);
            chatHead.findViewById(R.id.linearLayout_Already_Exists).setVisibility(View.VISIBLE);
            textViewDesc.setText("This code might be already saved as : ");
            textViewMenu.setText(code_name);
        } else {//if it is not saved save it,
            //save the code to the database
            Random r = new Random();
            codeId = r.nextLong();//TODO change random number generator
            action = new UssdAction(codeId, menuItem, code.replace("#", ""), null, null, Constants.SEC_USER_DIALED);
            List<Step> steps = new ArrayList<>();
            //get any steps if availabe ie telephone and amount
//            Toast.makeText(this, "mobile" + preferences.getInt(STEP_TEL, 0), Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "AMOUNT" + preferences.getInt(STEP_TEL, 0), Toast.LENGTH_SHORT).show();
        String[] stringSteps = preferences.getString(STEP_TEL, "").split(",");


        for (int i = 0; i < stringSteps.length; i++) {
            if (stringSteps[i].equalsIgnoreCase("T")) {
                steps.add(new Step(codeId, Constants.TELEPHONE, 0, "Telephone"));
//                Toast.makeText(this, "MOBILE NUMBER", Toast.LENGTH_SHORT).show();
            }
            if (stringSteps[i].equalsIgnoreCase("A")) {
                steps.add(new Step(codeId, Constants.NUMBER, 0, "Amount"));
//                Toast.makeText(this, "AMOUT", Toast.LENGTH_SHORT).show();
            }

            }
            dataRepository.insertAll(new UssdActionWithSteps(action, steps));

            //make preference null suchthat the same code is not shown again
            preferences.edit()
                .putString("code", "")
                .putString("menuItem", "")
                .putString(STEP_TEL, "")
                .apply();
//            Toast.makeText(this, "values removed"+preferences.getString("code","node"), Toast.LENGTH_SHORT).show();

//            preferences.edit().clear();
//            Toast.makeText(this, "value s removed1"+preferences.getString("code","node"), Toast.LENGTH_SHORT).show();


        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }
}
