package com.quickCodes.quickCodes.modals;

public class Step{
    private  int id;
    private String type;
    private String description =null;
    private int action_id;
    private String defaultValue = null;

    public Step(){

    }

    /**
     *
     * @param id
     * @param type
     * @param description
     * @param action_id
     */
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
