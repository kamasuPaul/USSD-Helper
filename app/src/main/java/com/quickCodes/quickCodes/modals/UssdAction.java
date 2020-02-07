package com.quickCodes.quickCodes.modals;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ussd_actions")
public class UssdAction {

    @PrimaryKey public long actionId;
    private String name;
    private String code;
    private String network;
    public int section;

    @Ignore
    public UssdAction(){

    }
    @Ignore
    public UssdAction(String name, String code, String network, int section) {
        this.name = name;
        this.code = code;
        this.network = network;
        this.section = section;
    }

    public UssdAction(long actionId, String name, String code, String network, int section) {
        this.actionId = actionId;
        this.name = name;
        this.code = code;
        this.network = network;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
            ", code='" + code + '\'' +
            ", network='" + network + '\'' +
            ", section=" + section +
            '}';
    }
}
