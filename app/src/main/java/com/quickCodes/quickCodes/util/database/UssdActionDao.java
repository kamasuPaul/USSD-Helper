package com.quickCodes.quickCodes.util.database;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.List;

@Dao
public abstract class UssdActionDao {
    public void insertUssdActionWithSteps(UssdActionWithSteps a) {
        insert(a.action);
        insertSteps(a.steps, a.action.getActionId());

    }

    /**
     * for inserting steps into the database
     *
     * @param steps
     * @param actionId
     */
    public void insertSteps(List<Step> steps, Long actionId) {
        //if steps = null there are no steps
        if (steps != null) {
            for (Step s : steps) {
                s.setUssd_action_id(actionId);
            }
            insertAll(steps);
        }
    }

    public void updateUssdActionWithSteps(UssdActionWithSteps a) {
        Log.d("WEIGHT", String.valueOf(a.action.getWeight()));
        deleteActionSteps(a.action.actionId);
        update(a.action);
        insertSteps(a.steps, a.action.getActionId());
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)//TODO change replace strategy
    abstract void insertAll(List<Step>steps);

    @Transaction
    @Query("SELECT * FROM ussd_actions WHERE hni IN (:hnis) ORDER BY weight DESC,date_last_accessed")
    public abstract LiveData<List<UssdActionWithSteps>> getActionsWithSteps(List<String> hnis);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(UssdAction ussdAction);

    @Delete
    abstract void delete(UssdAction  ussdActionWithSteps);

    @Update
    abstract void update(UssdAction ussdActionWithSteps);

    @Transaction
    @Query("SELECT * FROM ussd_actions WHERE actionId = :id")
    abstract UssdActionWithSteps get(String id);

    @Transaction
    @Query("DELETE FROM Step WHERE ussd_action_id = :actionId")
    abstract void deleteActionSteps(long actionId);

    @Transaction
    @Query("SELECT * FROM ussd_actions ORDER BY weight DESC,date_last_accessed")
    public abstract List<UssdActionWithSteps> getActionsWithStepsNoLiveData();
}

