package com.example.ussdhelper.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.ussdhelper.R;
import com.example.ussdhelper.modals.UssdAction;
import com.example.ussdhelper.util.SQLiteDatabaseHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterGridCustomCodes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UssdAction> items = new ArrayList<>();
    SQLiteDatabaseHandler db;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, UssdAction obj, int position);

        void onItemDelete(View view, UssdAction obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridCustomCodes(Context context, List<UssdAction> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public View lyt_parent;
        public TextView optionsMenu;
        public RelativeLayout relativeLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.title);
            relativeLayout = v.findViewById(R.id.RelativLyt_root);
            lyt_parent = (View) relativeLayout.findViewById(R.id.lyt_parent);
            optionsMenu = (TextView) v.findViewById(R.id.textView_optionsMenu);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_ussd_actions, parent, false);
        vh = new OriginalViewHolder(v);
        db = new SQLiteDatabaseHandler(ctx);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final UssdAction p = items.get(position);
            view.title.setText(p.getName());
//            view.image.setImageDrawable();
            // generate color based on a key (same key returns the same color), useful for list/grid views

            int color1 = ColorGenerator.MATERIAL.getRandomColor();

            // declare the builder object once.
            TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(2)
                .endConfig()
                .round();

            TextDrawable drawable = builder.build(String.valueOf(p.getName().trim().toUpperCase().charAt(0)), color1);
            view.image.setImageDrawable(drawable);


            view.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
            view.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createOptionsMenu(v, view, p, position);
                    return true;
                }
            });
            ;
            //add aclick listener to the textview
            view.optionsMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    createOptionsMenu(v, view, p, position);
                }
            });
        }
    }

    private void createOptionsMenu(final View v, OriginalViewHolder view, final UssdAction p, final int position) {
        //inflate options menu
        PopupMenu popupMenu = new PopupMenu(ctx, view.optionsMenu);
        //inflate the menu from layout resource file
        popupMenu.inflate(R.menu.action_card_menu);
        //handle menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.edit_menu:
//                        //edit clicked
//                        editAction(p.getId());
//                        break;
                    case R.id.delete_menu:
                        //delete clicked
                        deleteAction(p);
                        mOnItemClickListener.onItemDelete(v, items.get(position), position);

                        break;
                }
                return false;
            }
        });
        //show the menu
        popupMenu.show();
    }

    /**
     * this methods deletes a given action from the custom codes activity
     *
     * @param
     */
    private void editAction(int id) {

    }

    private void deleteAction(UssdAction action) {
        db.deleteOne(action);


    }


    @Override
    public int getItemCount() {
        return items.size();
    }

}
