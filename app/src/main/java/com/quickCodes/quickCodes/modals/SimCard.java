package com.quickCodes.quickCodes.modals;

public class SimCard {
    private String networkName;
    private String hni;//HNI is home network identity,
    // a combination of Mobile country code(MCC) and Mobile Network code(MNC) ie HNI = MCC+MNC
    private int slotIndex;
    private int subscriptionId;

    public SimCard(String networkName, String hni, int slotIndex, int subscriptionId) {
        this.networkName = networkName;
        this.hni = hni;
        this.slotIndex = slotIndex;
        this.subscriptionId = subscriptionId;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public String getHni() {
        return hni;
    }

    public void setHni(String hni) {
        this.hni = hni;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }
}
