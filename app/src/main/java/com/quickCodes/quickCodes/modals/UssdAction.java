package com.quickCodes.quickCodes.modals;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "ussd_actions")
public class UssdAction {
    @SerializedName("id")
    @PrimaryKey
    public long actionId;
    private String name;
    private String code;
    private String hni;
    public int section;
    public int weight = 0;
    private Date date_last_accessed;
    private boolean isStarred;
    @Ignore
    public UssdAction(){

    }

    public UssdAction(long actionId, String name, String code, String hni, int section, int weight) {
        this.actionId = actionId;
        this.name = name;
        this.code = code;
        this.hni = hni;
        this.section = section;
        this.weight = weight;
    }


    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHni() {
        return hni;
    }

    public void setHni(String hni) {
        this.hni = hni;
    }

    public Date getDate_last_accessed() {
        return date_last_accessed;
    }

    public void setDate_last_accessed(Date date_last_accessed) {
        this.date_last_accessed = date_last_accessed;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    @Override
    public String toString() {
        return "UssdAction{" +
                "actionId=" + actionId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", hni='" + hni + '\'' +
                ", section=" + section +
                ", weight=" + weight +
            '}';
    }
}
