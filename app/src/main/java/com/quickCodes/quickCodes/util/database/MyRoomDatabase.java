package com.quickCodes.quickCodes.util.database;

import android.content.Context;
import android.util.Log;

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


@Database(entities = {UssdAction.class, Step.class}, version = 4)
public abstract class MyRoomDatabase extends RoomDatabase {
    private static final String TAG = "MyRoomDatabase";
    private UssdActionsViewModel viewModel;
    static final Migration Migration_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //add stepsAfter column
            database.execSQL("ALTER TABLE Step "
                + "ADD COLUMN stepsAfter TEXT"

            );


        }
    };
    static final Migration Migration_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //change the table structure
            //delete airtel,mtn,africell columns and add two column code and hn,
            //but to do that you have to drop the table and recreate it.

            //create temporary table
            database.execSQL("ALTER  TABLE ussd_actions RENAME TO ussd_actions_tmp");
            database.execSQL("CREATE TABLE ussd_actions ("
                + "actionId INTEGER NOT NULL PRIMARY KEY,"
                + "name TEXT, "
                + "code TEXT, "
                + "hni TEXT, "
                + "section INTEGER NOT NULL,"
                + "weight INTEGER NOT NULL"
                + ")"
            );
            //copy data into temporary table
            database.execSQL("INSERT INTO ussd_actions (actionId,name,code,hni,section,weight)"
                //only select user saved data
                + "SELECT actionId,name,airtelCode,network,section,weight FROM ussd_actions_tmp WHERE section = 5"
            );
            //delete temporary table
            database.execSQL("DROP  TABLE  ussd_actions_tmp");


        }
    };
    static final Migration Migration_1_2 = new Migration(0, 1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //table not altered nothing to do,its just not to delete tables on update
        }
    };
    private static MyRoomDatabase INSTANCE;
    Context context;

    private static void addUssdCodes(Context context) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                addAirtimeCode(context);
                addDataCode(context);
                addMobileMoneyCodes(context);
                addCodes(context);

            }
        });
    }

    public static void addAirtimeCode(Context context) {
//       UssdActionDao dao = getDatabase(context).ussdActionDao();
//       UssdAction action = new UssdAction(0, "Buy Airtime", "*185*2*1*1", "*185*2*1*1","*144",SEC_AIRTIME);
//       UssdAction action1 = new UssdAction(1, "Check Balance", "*131", "*131","*131",SEC_AIRTIME);
//       UssdAction action2 = new UssdAction(2, "PakaLast", "*100*2*1", "*160*1","*134",SEC_AIRTIME);
//       UssdAction action3 = new UssdAction(3, "Buy For Another", "*185*2*1*2", "*185*2*1*2","not",SEC_AIRTIME);
//       UssdAction action4 = new UssdAction(4, "Borrow Airtime", "*100*4*1", "*160","not",SEC_AIRTIME);
//       UssdAction action5 = new UssdAction(5, "Call Me Back", "*100*7*7",null,"*100*8",SEC_AIRTIME);
//
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, Arrays.asList(new Step(0, NUMBER, 0, "Amount"))));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, Arrays.asList(new Step(3, TELEPHONE, 0, "Telephone"),
//           new Step(3,NUMBER,1,"Amount") )));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action4, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action5, Arrays.asList(new Step(5, TELEPHONE, 0, "Telephone"))));

    }

    public static void addDataCode(Context context) {
        UssdActionDao dao = getDatabase(context).ussdActionDao();

//        UssdAction action = new UssdAction(100, "Data Bundles","*175*2","*160*2*2*1","*133",SEC_DATA);
//        UssdAction action1 = new UssdAction(101, "Check Balance", "*175*4", "*131","*100*9*2",SEC_DATA);
//        UssdAction action2 = new UssdAction(102, "Data OTT", "*185*2*5*1", "*165*2*6*1","*133*8",SEC_DATA);
//        UssdAction action3 = new UssdAction(103, "Data PakaLast", "*175*3", "*160*1","not",SEC_DATA);
//        UssdAction action4 = new UssdAction(104, "Free Monthly", "*175*9*2", null,"not",SEC_DATA);
//       UssdAction action5 = new UssdAction(105, "Send Data", "*175*5*2", null, "not", SEC_DATA);
//       UssdAction action6 = new UssdAction(106, "Lyca Mobile Data", "*252*6*2*3", "*252*6*2*3", "*252*6*2*3", SEC_DATA);
//
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action4, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action5, Arrays.asList(new Step(105, TELEPHONE, 0, "Telephone"),
//           new Step(105, NUMBER, 1, "Mbs(50 to 2000)"))));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action6, Arrays.asList(new Step(106, TELEPHONE, 0, "lyca mobile number"),
//           new Step(106, CHOICE, 1, "Choose package", "1:20GB@10K<>2:50GB@25k<>100GB@45K", "1"))));

    }

    private static void addMobileMoneyCodes(Context context) {
        UssdActionDao dao = getDatabase(context).ussdActionDao();

//       UssdAction action = new UssdAction(200, "Check Balance", "*185*10*1","*185*10*1","*144",SEC_MMONEY);
//        UssdAction action1 = new UssdAction(201,  "Send Money", "*185*1*1","*185*1*1","*144",SEC_MMONEY);
//
//       UssdAction action2 = new UssdAction(202, "Withdraw Cash", "*185*3",null,null,SEC_MMONEY);
//       UssdAction action3 = new UssdAction(203, "Get a loan", "*185*8","*185*5*1*2",null,SEC_MMONEY);
//
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, null));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, Arrays.asList(new Step(201, TELEPHONE, 0, "Telephone"),
//           new Step(201,NUMBER,1,"Amount") )));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, Arrays.asList(new Step(202, NUMBER, 0, "Amount"))));
//       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, null));


    }

    private static void addCodes(Context context) {
        UssdActionDao dao = getDatabase(context).ussdActionDao();
        try {
            JSONArray networks = new JSONArray(loadJsonFromAsset(context));
            for (int i = 0; i < networks.length(); i++) {
                Log.d(TAG, "size" + networks.length());
                JSONObject network = networks.getJSONObject(i);
                String hni = network.optString("hni", "");
                JSONArray ussd_actions = network.optJSONArray("ussd_actions");
                Log.d(TAG, "size_actions" + ussd_actions.length());

                for (int j = 0; j < ussd_actions.length(); j++) {

                    JSONObject code = ussd_actions.getJSONObject(j);

                    int id = code.optInt("id");
                    String name = code.optString("name");
                    String airtelCode = code.optString("code");
                    int section = code.optInt("section");
                    int weight = code.optInt("weight");
                    UssdAction ussdAction = new UssdAction(id, name, airtelCode, hni, section, weight);

                    //get the local object and get its weight,and set this objects weight
                    //to that of the local object so its not overriden
//                   UssdActionWithSteps localAction = dao.getUssdAction(String.valueOf(id));
//                   if (localAction != null) {
//                       ussdAction.setWeight(localAction.action.getWeight());
//                   }

                    //TODO check if, it was deleted by checking is it has a flag of deleted set to true
                    JSONArray steps = code.getJSONArray("steps");
                    List<Step> stepList = new ArrayList<>();
                    for (int n = 0; n < steps.length(); n++) {
                        JSONObject step = steps.getJSONObject(n);
                        int type = step.optInt("type");
                        int step_weight = step.optInt("weight");
                        String desc = step.optString("description");
                        String defaultValue = step.optString("defaultValue");
                        int ussd_action_id = step.optInt("ussd_action_id");
                        Step step1 = new Step(ussd_action_id, type, step_weight, desc, defaultValue);
                        stepList.add(step1);
                    }
                    Log.d(TAG, ussdAction.toString());

                    //insert the code into the local database
                    dao.insertUssdActionWithSteps(new UssdActionWithSteps(ussdAction, stepList));
                }
            }

        } catch (JSONException e) {
            Log.d("HOME", "error" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized MyRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, MyRoomDatabase.class, "custom_actions_db")
                .addMigrations(Migration_1_2)
                .addMigrations(Migration_2_3)
                .addMigrations(Migration_3_4)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        Log.d(TAG, "After creating database");
//                        super.onCreate(db);
                        addUssdCodes(context);
                        //schedule a job schedular to request for codes from the database every 24 hours
                        //constraints
                        Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                        //Worker
                        PeriodicWorkRequest workRequest =
                            new PeriodicWorkRequest.Builder(DownloadWorker.class, 24, TimeUnit.HOURS)
                                .setConstraints(constraints)
                                .build();
                        WorkManager.getInstance(context).enqueue(workRequest);
                    }
                })
                .addCallback(new Callback() {
                    @Override
                    public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                        Log.d(TAG, "After destructive migration");
                        addUssdCodes(context);

                    }
                })
                .build();
        }
        //add new data to db,only if has not been added already
        if (!Tools.dataWasAdded(context)) {
            addUssdCodes(context);
            Log.d(TAG, "ADD DATA");

        } else {
            Log.d(TAG, "DIDNT ADD DATA");

        }

        return INSTANCE;
    }

    public static String loadJsonFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("response_quickcodes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.d("HOME", "error" + e.getMessage());
            e.printStackTrace();
        }
        return json;
    }

    private void addOtherCodes() {
//        superActionsOthers = new ArrayList<>();
//        superActionsOthers.add(simpleAction("Pay Umeme", "*175*2", "*131"));
//        superActionsOthers.add(simpleAction("Pay Tv", "*175*4", "*131"));
//        superActionsOthers.add(simpleAction("School Fees", "*175*9*2", "*131"));
//        superActionsOthers.add(simpleAction("Sports Betting", "*175*9*2", "*131"));
    }

    public abstract UssdActionDao ussdActionDao();
}
