package com.quickCodes.quickCodes.util;

import com.quickCodes.quickCodes.modals.CustomAction;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CustomActionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomAction customAction);

    @Delete
    void delete(CustomAction customAction);

    @Update
    void update(CustomAction customAction);

    @Query("SELECT * FROM custom_actions")
    LiveData<List<CustomAction>>getAll();

    @Query("SELECT * FROM custom_actions WHERE id =:id")
    CustomAction get(String id);
}
