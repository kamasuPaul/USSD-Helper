package com.quickCodes.quickCodes.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StepArrayAdapter extends ArrayAdapter<String> {
    String selected;
    int selected_pos;

    public StepArrayAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
        super(context, resource, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public int getSelected_pos() {
        return selected_pos;
    }

    public void setSelected_pos(int selected_pos) {
        this.selected_pos = selected_pos;
    }
}
