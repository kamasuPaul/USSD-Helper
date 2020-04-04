package com.quickCodes.quickCodes.adapters;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.ArrayList;
import java.util.List;

public class AdapterWithFilter extends AdapterGridCustomCodes implements Filterable {
    List<UssdActionWithSteps> ussdActionWithSteps = new ArrayList<>();
    List<UssdActionWithSteps> ussdActionWithStepsFiltered;

    public AdapterWithFilter(Context context) {
        super(context);
    }
    public  AdapterWithFilter(Context context, List<UssdActionWithSteps> actionWithSteps){
        super(context);
        ussdActionWithSteps = actionWithSteps;

    }
    public void setCustomActions(List<UssdActionWithSteps>actions){
        super.setCustomActions(actions);
        ussdActionWithSteps = actions;

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String words = constraint.toString();
                if(words.isEmpty()){
                    ussdActionWithStepsFiltered = ussdActionWithSteps;
                }else {
                    List<UssdActionWithSteps> l = new ArrayList<>();
                    for(UssdActionWithSteps action: ussdActionWithSteps){
                        if(action.action.getAirtelCode().toLowerCase().contains(words)
                            ||action.action.getMtnCode().toLowerCase().contains(words)
                            ||action.action.getAfricellCode().toLowerCase().contains(words)
                        ){
                            l.add(action);
                        }
                    }
                    ussdActionWithStepsFiltered = l;
                }
                FilterResults results = new FilterResults();
                results.values = ussdActionWithStepsFiltered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                    ussdActionWithStepsFiltered = (List<UssdActionWithSteps>) results.values;
                    notifyDataSetChanged();
            }
        };
    }
}
