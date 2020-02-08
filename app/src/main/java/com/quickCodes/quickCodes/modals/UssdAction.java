package com.quickCodes.quickCodes.modals;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ussd_actions")
public class UssdAction {

    @PrimaryKey public long actionId;
    private String name;
    private String airtelCode,mtnCode,africellCode;
    public int section;

    @Ignore
    public UssdAction(){

    }

    public UssdAction(long actionId, String name, String airtelCode, String mtnCode, String africellCode, int section) {
        this.actionId = actionId;
        this.name = name;
        this.airtelCode = airtelCode;
        this.mtnCode = mtnCode;
        this.africellCode = africellCode;
        this.section = section;
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

    public String getAirtelCode() {
        return airtelCode;
    }

    public void setAirtelCode(String airtelCode) {
        this.airtelCode = airtelCode;
    }

    public String getMtnCode() {
        return mtnCode;
    }

    public void setMtnCode(String mtnCode) {
        this.mtnCode = mtnCode;
    }

    public String getAfricellCode() {
        return africellCode;
    }

    public void setAfricellCode(String africellCode) {
        this.africellCode = africellCode;
    }
    @Override
    public String toString() {
        return "UssdAction{" +
            "actionId=" + actionId +
            ", name='" + name + '\'' +
            ", airtelCode='" + airtelCode + '\'' +
            ", mtnCode='" + mtnCode + '\'' +
            ", africellCode='" + africellCode + '\'' +
            ", section=" + section +
            '}';
    }


}
