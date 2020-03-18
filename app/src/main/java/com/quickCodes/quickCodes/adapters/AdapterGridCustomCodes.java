package com.quickCodes.quickCodes.adapters;

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
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterGridCustomCodes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UssdActionWithSteps> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, UssdActionWithSteps obj, int position);

        void onItemDelete(View view, UssdActionWithSteps obj, int position);

        void onItemEdit(View view, UssdActionWithSteps obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridCustomCodes(Context context) {
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
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(items!=null) {
            if (holder instanceof OriginalViewHolder) {
                final OriginalViewHolder view = (OriginalViewHolder) holder;


                final UssdAction p = items.get(position).action;
                view.title.setText(p.getName());
//            view.image.setImageDrawable();
                // generate color based on a key (same key returns the same color), useful for list/grid views
                ColorGenerator generator = ColorGenerator.MATERIAL;
//                int color1 = ColorGenerator.MATERIAL.getRandomColor();//generate random color
                int color1 = generator.getColor(p.getName());

                // declare the builder object once.
                TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(2)
                    .endConfig()
                    .round();

                TextDrawable drawable = builder.build(String.valueOf(p.getName().trim().toUpperCase().charAt(0)), color1);
                if (null != view.image) {
                    if (drawable != null) {
                        try {
                            view.image.setImageDrawable(drawable);
                        } catch (Exception e) {
                            //Toast.makeText(get, "Some features may not work", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //view.image.setImageDrawable(drawable);


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
    }
    public void setCustomActions(List<UssdActionWithSteps>actions){
        this.items = actions;
        notifyDataSetChanged();
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
                    case R.id.edit_menu:
                        //edit clicked
                        mOnItemClickListener.onItemEdit(v,items.get(position),position);
                        break;
                    case R.id.delete_menu:
                        //delete clicked

                        mOnItemClickListener.onItemDelete(v, items.get(position), position);

                        break;
                }
                return false;
            }
        });
        //show the menu
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        if(items!=null){
            return items.size();
        }else{
            return 0;
        }
    }

}
