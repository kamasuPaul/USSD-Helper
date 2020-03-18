package com.quickCodes.quickCodes.modals;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ussd_actions")
public class UssdAction {
    @SerializedName("id")
    @PrimaryKey public long actionId;
    private String name;
    private String airtelCode;
    private String mtnCode;
    private String africellCode;
    public int section;
    private String network = ""; //this is to be used for custom codes to define which network they run on

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
    @Ignore
    public UssdAction(long actionId, String name, String airtelCode, String mtnCode, String africellCode, int section,String network) {
        this.actionId = actionId;
        this.name = name;
        this.airtelCode = airtelCode;
        this.mtnCode = mtnCode;
        this.africellCode = africellCode;
        this.section = section;
        this.network = network;
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

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
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
