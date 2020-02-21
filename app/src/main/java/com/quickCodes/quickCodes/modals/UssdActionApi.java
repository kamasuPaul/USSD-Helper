package com.quickCodes.quickCodes.modals;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Ignore;

public class UssdActionApi {
    @SerializedName("id")
    public long actionId;
    private String name;
    private String airtelCode;
    private String mtnCode;
    private String africellCode;
    public int section;

    @SerializedName("steps")
    List<Step>steps;

    @Ignore
    public UssdActionApi(){

    }

    public UssdActionApi(long actionId, String name, String airtelCode, String mtnCode, String africellCode, int section) {
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
