package com.example.ussdhelper.modals;

public class UssdAction {
    private int id;
    private String name;
    private String code;
    private String network;

    public UssdAction(){

    }

    public UssdAction(int id, String name, String code, String network) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.network = network;
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
    @Override
    public String toString() {
        return network + " - " + name + " - " + code;
    }
}
