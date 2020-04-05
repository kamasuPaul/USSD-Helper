package com.quickCodes.quickCodes.util.database;

import com.quickCodes.quickCodes.modals.Step;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class UssdActionDao {
    public void insertStepsForAction(UssdActionWithSteps a){
        List<Step> steps = a.steps;
        insert(a.action);
        //if steps = null there are no steps
        if(steps!=null){
            for(Step s: steps){
                s.setUssd_action_id(a.action.getActionId());
            }
            insertAll(steps);
        }
    }
    public void updateActionWithSteps(UssdActionWithSteps a){
        deleteActionSteps(a.action.actionId);
        insertStepsForAction(a);
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)//TODO change replace strategy
    abstract void insertAll(List<Step>steps);
    @Transaction
    @Query("SELECT * FROM ussd_actions")
    public abstract LiveData<List<UssdActionWithSteps>> getActionsWithSteps();

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
}

