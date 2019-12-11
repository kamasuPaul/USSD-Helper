package com.quickCodes.quickCodes.modals;

import java.util.Arrays;

public class UssdAction {
    private int id;
    private String name;
    private String code;
    private String network;
    private Step[] steps = null;

    public UssdAction(){

    }

    public UssdAction(int id, String name, String code, String network,Step[]steps) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.network = network;
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "UssdAction{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", network='" + network + '\'' +
            ", steps=" + Arrays.toString(steps) +
            '}';
    }

}
