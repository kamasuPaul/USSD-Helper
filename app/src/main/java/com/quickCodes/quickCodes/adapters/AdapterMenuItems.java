package com.quickCodes.quickCodes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.quickCodes.quickCodes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdapterMenuItems extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> items = new ArrayList<>();
    private Map<String, String> kamasuMenu;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AdapterMenuItems(Context context) {
        ctx = context;
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_menu, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout_no_item manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (items != null) {
            if (holder instanceof OriginalViewHolder) {
                final OriginalViewHolder view = (OriginalViewHolder) holder;


                final String key = items.get(position);
                TextView textView = view.description;
                String description = kamasuMenu.get(key);
                textView.setText(description);
                // generate color based on a key (same key returns the same color)
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color1 = generator.getColor(String.valueOf(key));
                // declare the builder object once.
                TextDrawable.IBuilder builder = TextDrawable.builder()
                        .beginConfig()
                        .withBorder(2)
                        .endConfig()
                        .round();
                TextDrawable drawable = builder.build(String.valueOf(key), color1);
                ImageView imageView = view.image;
                if (drawable != null) {
                    try {
                        imageView.setImageDrawable(drawable);
                    } catch (Exception e) {
                        //Toast.makeText(get, "Some features may not work", Toast.LENGTH_SHORT).show();
                    }
                }

                view.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, items.get(position), position);
                        }
                    }
                });
                view.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        createOptionsMenu(v, view, p, position);
                        return true;
                    }
                });

            }
        }
    }

    public void setUssdActions(List<String> menues, Map<String, String> kamasuMenu) {
        this.items = menues;
        this.kamasuMenu = kamasuMenu;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        //        public TextView title;
        public LinearLayout linearLayout;
        public TextView description;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
//            title = (TextView) v.findViewById(R.id.TextView_ActionName);
            linearLayout = v.findViewById(R.id.linearLayout_root);
            description = v.findViewById(R.id.description);
        }
    }

}
