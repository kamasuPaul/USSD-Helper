package com.quickCodes.quickCodes.modals;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = UssdAction.class,
        parentColumns = "actionId",
        childColumns ="ussd_action_id",
        onDelete = ForeignKey.CASCADE),
        indices = {@Index("ussd_action_id")}
)
public class Step{
    @PrimaryKey(autoGenerate = true) private  long stepId;
    private long ussd_action_id;
    private int type;
    private String description =null;
    private String defaultValue = null;
    private int weight;
    @Ignore
    public Step(){

    }

    /**
     *
     * @param ussd_action_id
     * @param type Telephone or text or number
     * @param weight
     * @param description decription of the step
     */
    @Ignore
    public Step(long ussd_action_id, int type, int weight,String description) {
        this.ussd_action_id = ussd_action_id;
        this.type = type;
        this.weight = weight;
        this.description = description;
    }

    public Step(long ussd_action_id, int type, int weight,String description, String defaultValue) {
        this.ussd_action_id = ussd_action_id;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
        this.weight = weight;
    }

    public long getStepId() {
        return stepId;
    }

    public void setStepId(long stepId) {
        this.stepId = stepId;
    }

    public long getUssd_action_id() {
        return ussd_action_id;
    }

    public void setUssd_action_id(long ussd_action_id) {
        this.ussd_action_id = ussd_action_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Step{" +
            "stepId=" + stepId +
            ", ussd_action_id=" + ussd_action_id +
            ", type='" + type + '\'' +
            ", description='" + description + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            ", weight=" + weight +
            '}';
    }
}