package com.quickCodes.quickCodes.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AdapterUssdCodes extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UssdActionWithSteps> items = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, UssdActionWithSteps obj, int position);

        void onItemDelete(View view, UssdAction obj, int position);

        void onItemEdit(View view, UssdAction obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterUssdCodes(Context context) {
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public LinearLayout linearLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.ImageView_ActionIcon);
            title = (TextView) v.findViewById(R.id.TextView_ActionName);
            linearLayout = v.findViewById(R.id.myAction);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_action, parent, false);
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

                //set title of action
                view.title.setText(p.getName());
//            view.image.setImageDrawable();
                // generate color based on a key (same key returns the same color), useful for list/grid views

                //set image icon of action
                //change the image icon to a letter icon
                TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.valueOf((p.getName()).trim().charAt(0)).toUpperCase(),ctx.getResources().getColor(R.color.colorPrimary));
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
                //get the screen width
                int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
                view.linearLayout.getLayoutParams().width = (int)((widthPixels)/3);



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
    public void setUssdActions(List<UssdActionWithSteps> actions){
        this.items = actions;
        notifyDataSetChanged();
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
