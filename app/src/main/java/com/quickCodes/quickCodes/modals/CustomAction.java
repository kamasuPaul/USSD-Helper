package com.quickCodes.quickCodes.modals;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "custom_actions")
public class CustomAction {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String name;
    public String code = "*131";

    public CustomAction() {
    }
    @Ignore
    public CustomAction(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
