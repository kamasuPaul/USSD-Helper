package com.quickCodes.quickCodes.util;

import android.content.Context;

import com.quickCodes.quickCodes.modals.CustomAction;
import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {CustomAction.class, UssdAction.class, Step.class},version = 1)
public abstract class MyRoomDatabase extends RoomDatabase {
    private UssdActionsViewModel viewModel;

    private static MyRoomDatabase INSTANCE;
   public static synchronized MyRoomDatabase getDatabase(Context context){
       if(INSTANCE==null){
          INSTANCE = Room.databaseBuilder(context,MyRoomDatabase.class,"custom_actions_db")
               .fallbackToDestructiveMigration()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        //getDatabase(context).ussdActionDao().insertStepsForAction();
                    }
                })
               .build();
       }
       return INSTANCE;
   }
   public abstract CustomActionDao customActionDao();
   public abstract UssdActionDao ussdActionDao();

}
