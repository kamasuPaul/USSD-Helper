package com.example.ussdhelper.modals;

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

    public static class Step{
        private  int id;
        private String type;
        private String description =null;
        private int action_id;

        public Step(){

        }

        public Step(int id, String type, String description, int action_id) {
            this.id = id;
            this.type = type;
            this.description = description;
            this.action_id = action_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getAction_id() {
            return action_id;
        }

        public void setAction_id(int action_id) {
            this.action_id = action_id;
        }

        @Override
        public String toString() {
            return "Step{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", action_id=" + action_id +
                '}';
        }
    }
}
