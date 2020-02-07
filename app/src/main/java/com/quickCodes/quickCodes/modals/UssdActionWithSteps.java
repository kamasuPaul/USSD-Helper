package com.quickCodes.quickCodes.modals;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;


public class UssdActionWithSteps {
    @Embedded public UssdAction action;
    @Relation(parentColumn = "actionId", entityColumn = "ussd_action_id",entity =Step.class)
    public List<Step> steps;

    public UssdActionWithSteps(UssdAction action, List<Step> steps) {
        this.action = action;
        this.steps = steps;
    }

    @Override
    public String toString() {
        return "UssdActionWithSteps{" +
            "action=" + action +
            ", steps=" + steps +
            '}';
    }
}
