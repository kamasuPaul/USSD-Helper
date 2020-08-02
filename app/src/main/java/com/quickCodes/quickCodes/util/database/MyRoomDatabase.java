package com.quickCodes.quickCodes.util.database;

import android.content.Context;
import android.util.Log;

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.Arrays;
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

import static com.quickCodes.quickCodes.modals.Constants.NUMBER;
import static com.quickCodes.quickCodes.modals.Constants.SEC_AIRTIME;
import static com.quickCodes.quickCodes.modals.Constants.SEC_DATA;
import static com.quickCodes.quickCodes.modals.Constants.SEC_MMONEY;
import static com.quickCodes.quickCodes.modals.Constants.TELEPHONE;


@Database(entities = {UssdAction.class, Step.class}, version = 3)
public abstract class MyRoomDatabase extends RoomDatabase {
    private static final String TAG = "MyRoomDatabase";
    private UssdActionsViewModel viewModel;


    private static MyRoomDatabase INSTANCE;
    static final Migration Migration_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //add stepsAfter column
            database.execSQL("ALTER TABLE Step "
                + "ADD COLUMN stepsAfter TEXT"

            );
        }
    };

    private static void addUssdCodes(Context context) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        executors.execute(new Runnable() {
            @Override
            public void run() {
                addAirtimeCode(context);
                addDataCode(context);
                addMobileMoneyCodes(context);

            }
        });
    }

    public abstract UssdActionDao ussdActionDao();

   public  static void addAirtimeCode(Context context){
       UssdActionDao dao = getDatabase(context).ussdActionDao();
       UssdAction action = new UssdAction(0, "Buy Airtime", "*185*2*1*1", "*185*2*1*1","*144",SEC_AIRTIME);
       UssdAction action1 = new UssdAction(1, "Check Balance", "*131", "*131","*131",SEC_AIRTIME);
       UssdAction action2 = new UssdAction(2, "PakaLast", "*100*2*1", "*160*1","*134",SEC_AIRTIME);
       UssdAction action3 = new UssdAction(3, "Buy For Another", "*185*2*1*2", "*185*2*1*2","not",SEC_AIRTIME);
       UssdAction action4 = new UssdAction(4, "Borrow Airtime", "*100*4*1", "*160","not",SEC_AIRTIME);
       UssdAction action5 = new UssdAction(5, "Call Me Back", "*100*7*7",null,"*100*8",SEC_AIRTIME);

       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, Arrays.asList(new Step(0, NUMBER, 0, "Amount"))));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, Arrays.asList(new Step(3, TELEPHONE, 0, "Telephone"),
           new Step(3,NUMBER,1,"Amount") )));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action4, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action5, Arrays.asList(new Step(5, TELEPHONE, 0, "Telephone"))));

   }

   public  static void addDataCode(Context context){
        UssdActionDao dao = getDatabase(context).ussdActionDao();

        UssdAction action = new UssdAction(100, "Data Bundles","*175*2","*160*2*2*1","*133",SEC_DATA);
        UssdAction action1 = new UssdAction(101, "Check Balance", "*175*4", "*131","*100*9*2",SEC_DATA);
        UssdAction action2 = new UssdAction(102, "Data OTT", "*185*2*5*1", "*165*2*6*1","*133*8",SEC_DATA);
        UssdAction action3 = new UssdAction(103, "Data PakaLast", "*175*3", "*160*1","not",SEC_DATA);
        UssdAction action4 = new UssdAction(104, "Free Monthly", "*175*9*2", null,"not",SEC_DATA);
        UssdAction action5 = new UssdAction(105, "Send Data", "*175*5*2",null,"not",SEC_DATA);

       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action4, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action5, Arrays.asList(new Step(105, TELEPHONE, 0, "Telephone"),
            new Step(105,NUMBER,1,"Mbs(50 to 2000)") )));

    }

   private static void addMobileMoneyCodes(Context context) {
       UssdActionDao dao = getDatabase(context).ussdActionDao();

       UssdAction action = new UssdAction(200, "Check Balance", "*185*10*1","*185*10*1","*144",SEC_MMONEY);
        UssdAction action1 = new UssdAction(201,  "Send Money", "*185*1*1","*185*1*1","*144",SEC_MMONEY);

       UssdAction action2 = new UssdAction(202, "Withdraw Cash", "*185*3",null,null,SEC_MMONEY);
       UssdAction action3 = new UssdAction(203, "Get a loan", "*185*8","*185*5*1*2",null,SEC_MMONEY);

       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action, null));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action1, Arrays.asList(new Step(201, TELEPHONE, 0, "Telephone"),
           new Step(201,NUMBER,1,"Amount") )));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action2, Arrays.asList(new Step(202, NUMBER, 0, "Amount"))));
       dao.insertUssdActionWithSteps(new UssdActionWithSteps(action3, null));


   }
    private void addOtherCodes() {
//        superActionsOthers = new ArrayList<>();
//        superActionsOthers.add(simpleAction("Pay Umeme", "*175*2", "*131"));
//        superActionsOthers.add(simpleAction("Pay Tv", "*175*4", "*131"));
//        superActionsOthers.add(simpleAction("School Fees", "*175*9*2", "*131"));
//        superActionsOthers.add(simpleAction("Sports Betting", "*175*9*2", "*131"));
    }
    static final Migration Migration_1_2 = new Migration(0,1) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //table not altered nothing to do,its just not to delete tables on update
        }
    };

    public static synchronized MyRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, MyRoomDatabase.class, "custom_actions_db")
                .addMigrations(Migration_1_2)
                .addMigrations(Migration_2_3)
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
        return INSTANCE;
    }
}
