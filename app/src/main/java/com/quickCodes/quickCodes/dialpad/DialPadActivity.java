package com.quickCodes.quickCodes.dialpad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.quickCodes.quickCodes.MainActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.adapters.AdapterDialer;
import com.quickCodes.quickCodes.modals.Constants;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.ui.main.CustomCodesFragment;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.quickCodes.quickCodes.fragments.MainFragment.simcardsSlots;
import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;
import static com.quickCodes.quickCodes.modals.Constants.TEXT;


public class DialPadActivity extends AppCompatActivity {
    private static final int CONTACT_PICKER_REQUEST = 200;
    String num;
    TextView edit_text, tname, tnumber;
    ImageView one, two, three, four, five, six, seven, eight, nine, zero, star, hash, sim1, sim2, clear;

    BottomSheetBehavior bottomSheetBehavior;


    ArrayList<String> namelist = MainActivity.namelist;
    ArrayList<String> numberlist = MainActivity.namelist;
    UssdActionsViewModel ussdActionsViewModel;
    private RecyclerView recyclerView;
    private AdapterDialer mAdapter;
    private EditText phoneNumber;
    private android.widget.SearchView searchView;
    String search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get intent to determine if intent is from click of search icon,serch that
        //we hide the dialpad
        Intent intent = getIntent();
        search = intent.getStringExtra("search");
        setContentView(R.layout.activity_dialpad);


        Toolbar toolbar = findViewById(R.id.toolbar_dialer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new AdapterDialer(this);


        ussdActionsViewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
        ussdActionsViewModel.getAllCustomActions().observe(this, ussdActionWithSteps -> {
            List<UssdActionWithSteps> airtimeCodes = new ArrayList<>();
            for (UssdActionWithSteps us : ussdActionWithSteps) {
                airtimeCodes.add(us);
            }
            mAdapter.setUssdActions(airtimeCodes);

        });

        getContactList();
        initComponent();
        initalizeDialerButtons();


        // get the bottom sheet view
        LinearLayout BottomSheet = findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(BottomSheet);

        // change the state of the bottom sheet
        bottomSheetBehavior.setPeekHeight(300);
        //if the intent is from search icon, hide the bottom sheet
        if (search != null) {
            bottomSheetBehavior.setPeekHeight(0);
            bottomSheetBehavior.setHideable(true);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


        // set callback for changes
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        edit_text = (TextView) findViewById(R.id.edit_text);
        edit_text.setOnClickListener(null);

    }

    private void initalizeDialerButtons() {
        one = (ImageView) findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // edit_text.setTextColor(getResources().getColor(red));
                edit_text.setText(edit_text.getText().toString() + "1");
                matchContact(edit_text.getText().toString());


            }
        });

        two = (ImageView) findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_text.setText(edit_text.getText().toString() + "2");
                matchContact(edit_text.getText().toString());


            }
        });

        three = (ImageView) findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "3");
                matchContact(edit_text.getText().toString());

            }
        });

        four = (ImageView) findViewById(R.id.four);
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "4");
                matchContact(edit_text.getText().toString());

            }
        });

        five = (ImageView) findViewById(R.id.five);
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "5");
                matchContact(edit_text.getText().toString());

            }
        });

        six = (ImageView) findViewById(R.id.six);
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "6");
                matchContact(edit_text.getText().toString());

            }
        });

        seven = (ImageView) findViewById(R.id.seven);
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "7");
                matchContact(edit_text.getText().toString());

            }
        });

        eight = (ImageView) findViewById(R.id.eight);
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "8");
                matchContact(edit_text.getText().toString());

            }
        });

        nine = (ImageView) findViewById(R.id.nine);
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "9");
                matchContact(edit_text.getText().toString());

            }
        });

        zero = (ImageView) findViewById(R.id.zero);
        zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "0");
                matchContact(edit_text.getText().toString());

            }
        });
        zero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "+");
                matchContact(edit_text.getText().toString());

                return true;
            }
        });

        star = (ImageView) findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text.setText(edit_text.getText().toString() + "*");
                matchContact(edit_text.getText().toString());

            }
        });
        star.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + ",");
                return true;
            }
        });


        hash = (ImageView) findViewById(R.id.hash);
        hash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_text.setText(edit_text.getText().toString() + "#");
                matchContact(edit_text.getText().toString());

//
//                if (edit_text.length() == 5) {
//
//                    num = edit_text.getText().toString();
//                    if (num.contains("#")) {
//                        num = num.replace("#", "%23");
//                    }
//
//                    if (num.contains("*") && num.charAt(0) == '*') {
//                        makePhoneCall();
//                    } else {
//                        Toast.makeText(MainActivity.this, "Star is missing", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(MainActivity.this, "Wrong Number", Toast.LENGTH_SHORT).show();
//                }


            }
        });
        hash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText(edit_text.getText().toString() + ";");

                return true;
            }
        });

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text.length() > 0) {

                    //to remove last added char or digit
                    String num1 = edit_text.getText().toString().substring(0, edit_text.length() - 1);
                    edit_text.setText(num1);
                    matchContact(edit_text.getText().toString());
                }
            }
        });
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                edit_text.setText("");
                return true;
            }
        });

        sim1 = (ImageView) findViewById(R.id.sim1);
        sim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = edit_text.getText().toString();
                if (num.contains("#")) {
                    num = num.replace("#", "%23");
                }

                makePhoneCall();
            }
        });
    }

    public void makePhoneCall() {

        //if there is already number we have to call and if the is no number we have to ask permmision
        if (num.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(DialPadActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                //ask for permission
                ActivityCompat.requestPermissions(DialPadActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

            } else {
                String dial = "tel:" + num;
                //save code
                Log.d("CODE", dial);
                UssdActionsViewModel viewModel = ViewModelProviders.of(this).get(UssdActionsViewModel.class);
                Random r = new Random();
                Long codeId = r.nextLong();//TODO change random number generator
                UssdAction action = new UssdAction(codeId, "Recent", num.replace("%23", ""), null, null, Constants.SEC_USER_DIALED);
                viewModel.insert(action, null);
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(this, "Enter the PhoneNumber", Toast.LENGTH_SHORT).show();
        }
    }

    private void getContactList() {
        //add contatcts to list of searchable items
        mAdapter.setContactList(namelist, numberlist);
    }

    public void matchContact(String contact) {

        mAdapter.getFilter().filter(contact);
    }

    private void initComponent() {

        recyclerView = (RecyclerView) findViewById(R.id.matched_items_recylerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new CustomCodesFragment.MyItemDecorator(2, 5));
//        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //scrolling up
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {
                    //scrolling down
                }
            }
        });

        //set data and list adapter
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterDialer.OnItemClickListener() {
            @Override
            public void onItemClick(View view, UssdActionWithSteps obj, int position) {
                //TODO first look into custom codes
                String initialCode = obj.action.getAirtelCode();
                String cd = initialCode + Uri.encode("#");
                createDialog(obj);
            }
            @Override
            public void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position) {

            }
        });


    }

    public void createDialog(UssdActionWithSteps ussdActionWithSteps) {

        //use codes for the selected network mode
//        UssdAction ussdAction = null;
//        String mode1 = "AIRTEL";//TODO get mode in a better way
//        UssdAction ussdAction = ussdActionWithSteps.action;
//        String code = "";
        String uscode1 = ussdActionWithSteps.action.getAirtelCode();//airtel code is default code

        //check for phone number clicks
        if (!uscode1.contains("*")) {//if it doesnt contain a * its aphone number, exececute it immediately
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + uscode1)));
            return;
        }

        UssdAction action = ussdActionWithSteps.action;

        if (action.getAirtelCode() != null) {
            if (!action.getAirtelCode().isEmpty()) {
                uscode1 = action.getAirtelCode();
            }
        }
        if (action.getMtnCode() != null) {
            if (!action.getMtnCode().isEmpty()) {
                uscode1 = action.getMtnCode();
            }
        }
        if (action.getAfricellCode() != null) {
            if (!action.getAfricellCode().isEmpty()) {
                uscode1 = action.getAfricellCode();
            }
        }
        final String uscode = uscode1;

        if (ussdActionWithSteps.steps == null || ussdActionWithSteps.steps.size() == 0) {
            //execute the code immediately
            //TODO execute code with selected simcard instead for prompting the user to select sim
            String code = uscode1 + Uri.encode("#");
            executeUssd(code, ussdActionWithSteps.action);


        } else {

            final Dialog customDialog;
            //inflate the root dialog
            LayoutInflater inflater = getLayoutInflater();
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.dialog_root, null);
            LinearLayout root = (LinearLayout) cardView.findViewById(R.id.linearLayout_root);
            //else check for steps and construct the layout
            for (Step step : ussdActionWithSteps.steps) {
                if (step.getType() == TEXT) {

                    View rowText = inflater.inflate(R.layout.row_text, null);
                    rowText.setId((int) step.getStepId());
                    final EditText editText = rowText.findViewById(R.id.editText_text);
                    editText.setHint(step.getDescription());
                    root.addView(rowText);


                }
                if (step.getType() == TELEPHONE) {
                    View rowTelephone = inflater.inflate(R.layout.row_telephone, null);

                    ImageButton imageButton = rowTelephone.findViewById(R.id.selec_contact_ImageBtn);
                    final EditText editText = rowTelephone.findViewById(R.id.edit_text_mobileNumber);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            phoneNumber = editText;
                            Intent i = new Intent(Intent.ACTION_PICK);
                            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(i, CONTACT_PICKER_REQUEST);
                        }
                    });

                    rowTelephone.setId((int) step.getStepId());
                    root.addView(rowTelephone);


                }
                if (step.getType() == NUMBER) {
                    View rowAmount = inflater.inflate(R.layout.row_amount, null);
                    rowAmount.setId((int) step.getStepId());
                    final EditText editText = rowAmount.findViewById(R.id.edit_text_amount);
                    editText.setHint(step.getDescription());
                    root.addView(rowAmount);

                }

            }

            //inflate each row that should be contained in the dialog box
            View rowButtons = inflater.inflate(R.layout.row_buttons, null);
            //add each row to the root
            root.addView(rowButtons);
            cardView.setPadding(5, 5, 5, 5);

            customDialog = new Dialog(this);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            customDialog.setContentView(cardView);
            customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customDialog.setCancelable(true);


            final UssdActionWithSteps finalUssdAction = ussdActionWithSteps;
            ((Button) customDialog.findViewById(R.id.bt_okay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder stringBuilder = new StringBuilder(uscode);
                    //get all the user entered values
                    for (Step step : finalUssdAction.steps) {
                        //get the user entered value of each step using its id
                        LinearLayout linearLayout = (LinearLayout) customDialog.findViewById((int) step.getStepId());

                        String value = ((EditText) linearLayout.findViewWithTag("editText")).getText().toString();
                        if (value != null && !value.isEmpty()) {
                            stringBuilder.append("*" + value);
                        }

                    }
                    //generate the code with the values inserted
                    //run the code
                    String fullCode = stringBuilder.toString() + Uri.encode("#");
                    customDialog.dismiss();
                    //execute the ussd
                    executeUssd(fullCode, ussdActionWithSteps.action);
//                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));


                }


            });

            ((Button) customDialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });
            customDialog.show();
        }
    }

    @SuppressLint("MissingPermission")
    public void executeUssd(String fullCode, UssdAction action) {
        String hnc = action.getNetwork();
        String simcardSlot = simcardsSlots.get(hnc);

        TelecomManager telecomManager = null;
        List<PhoneAccountHandle> phoneAccountHandleList = null;
        // if the users phone is android is  Marshmallow or above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (simcardsSlots.containsKey(hnc)) {//if the network choosen by user when saving code is still available in simcard
                //TODO if api level is greater than 26 do background codes,do this later,not important right now
                telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
                for (int i = 0; i < phoneAccountHandleList.size(); i++) {
                    PhoneAccountHandle phoneAccountHandle = phoneAccountHandleList.get(i);
                    if (i == Integer.valueOf(simcardSlot)) {
                        Uri uri = Uri.parse("tel:" + fullCode);
                        Bundle extras = new Bundle();
                        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle);
                        telecomManager.placeCall(uri, extras);
                        break;//break out of the loop
                    }
                }
            } else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

            }

        } else {
            //use normal way of dialing ussd code,because their is not an easy way of getting user selected simcard
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + fullCode)));

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int numberIdex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberIdex);
                    if (phoneNumber != null) {
                        if (number.startsWith("+256")) {
                            number = number.replace("+256", "0");
                        }
                        number = number.replace(" ", "");
                        phoneNumber.setText(number);
                    }


                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No contact selected", Toast.LENGTH_SHORT).show();
                System.out.println("User closed the picker without selecting items.");
            }
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            String[] sessionTextArr = data.getStringArrayExtra("ussd_messages");
            String uuid = data.getStringExtra("uuid");
            Toast.makeText(this, sessionTextArr.toString(), Toast.LENGTH_LONG).show();

        } else if (requestCode == 0 && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Error: " + data.getStringExtra("error"), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(DialPadActivity.this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                makePhoneCall();
            }


        } else {
            Toast.makeText(this, "need Permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dialpad_menu, menu);
        //associate the searchable configuration with the search view
        SearchManager searchM = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (android.widget.SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchM.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search...");
        if (search != null) {
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                matchContact(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                matchContact(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new android.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.app_bar_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        } else {
            finish();
        }
        super.onBackPressed();
    }
}
