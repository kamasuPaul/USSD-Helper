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
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.SimCard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdapterSimCards extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<String> namelist;
    ArrayList<String> numberlist;
    private List<SimCard> simCards = new ArrayList<>();
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AdapterSimCards(Context context) {
        ctx = context;

    }

    /*
 adapted from mkyong.com
 */
    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().trim().replaceAll(" ", "").contains(subString.toLowerCase().trim().replaceAll(" ", ""));
    }

    // Replace the contents of a view (invoked by the layout_no_item manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (simCards != null) {
            if (holder instanceof OriginalViewHolder) {
                final OriginalViewHolder rootView = (OriginalViewHolder) holder;


                final SimCard simCard = simCards.get(position);

                //set title of action

                rootView.title.setText(simCard.getNetworkName());

                String codeString = simCard.getHni();

                rootView.simcardIndex.setText("SIM " + (simCard.getSlotIndex() + 1));

                String icon_letter = String.valueOf((simCard.getNetworkName()).trim().charAt(0)).toUpperCase();
                if (!icon_letter.matches("[a-z]")) {//if the first character is not alphabetic
                    //find the first alphabetic character in the name of this code
                    Pattern pattern = Pattern.compile("\\p{Alpha}");
                    Matcher matcher = pattern.matcher(simCard.getNetworkName());
                    if (matcher.find()) {
                        icon_letter = matcher.group();
                    }
                }
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(icon_letter, ctx.getResources().getColor(R.color.colorPrimary));
                if (null != rootView.image) {
                    if (drawable != null) {
                        try {
                            rootView.image.setImageDrawable(drawable);
                        } catch (Exception e) {
                            //Toast.makeText(get, "Some features may not work", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                rootView.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, simCards.get(position), position);
                        }
                    }
                });
                rootView.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onLongClick(v, simCards.get(position), position);
                        }
                        return true;
                    }
                });

            }
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simcard, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    public void setSimcards(List<SimCard> simcards) {
        this.simCards = simcards;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (simCards != null) {
            return simCards.size();
        } else {
            return 0;
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, SimCard obj, int position);

        void onLongClick(View v, SimCard simCard, int position);

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, simcardIndex;
        public LinearLayout linearLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.ImageView_ActionIcon);
            title = (TextView) v.findViewById(R.id.TextView_ActionName);
            simcardIndex = (TextView) v.findViewById(R.id.text_view_simcard_index);
            linearLayout = v.findViewById(R.id.myAction);
        }
    }
}
