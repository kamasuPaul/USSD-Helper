package com.quickCodes.quickCodes.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.UssdAction;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.recyclerview.widget.RecyclerView;

import static com.quickCodes.quickCodes.modals.Constants.SEC_AIRTIME;
import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;
import static com.quickCodes.quickCodes.modals.Constants.SEC_DATA;
import static com.quickCodes.quickCodes.modals.Constants.SEC_MMONEY;
import static com.quickCodes.quickCodes.modals.Constants.SEC_USER_DIALED;

public class AdapterDialer extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<UssdActionWithSteps> items = new ArrayList<>();
    private Context ctx;
    ArrayList<String> namelist;
    ArrayList<String> numberlist;
    private OnItemClickListener mOnItemClickListener;
    private List<UssdActionWithSteps> ussdActionWithStepsFiltered;

    // Replace the contents of a view (invoked by the layout_no_item manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (ussdActionWithStepsFiltered != null) {
            if (holder instanceof OriginalViewHolder) {
                final OriginalViewHolder rootView = (OriginalViewHolder) holder;


                final UssdAction ussdAction = ussdActionWithStepsFiltered.get(position).action;

                //set title of action

                rootView.title.setText(ussdAction.getName());

                String codeString = ussdAction.getAirtelCode();
                if (ussdAction.getMtnCode() != null) {
                    if (!ussdAction.getMtnCode().isEmpty())
                        codeString += "\t\t|" + ussdAction.getMtnCode();

                }
                if (ussdAction.getAfricellCode() != null) {
                    if (!ussdAction.getAfricellCode().isEmpty())
                        codeString += "\t\t|" + ussdAction.getAfricellCode();
                }
                rootView.code.setText(codeString);
                if (String.valueOf(ussdAction.getActionId()) != null) {
                    rootView.section.setText(getSectionFromId(ussdAction.getSection()));
                } else {
                    rootView.section.setText("TELPHONE");
                }
//            view.image.setImageDrawable();
                // generate color based on a key (same key returns the same color), useful for list/grid views

                //set image icon of action
                //change the image icon to a letter icon
                TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.valueOf((ussdAction.getName()).trim().charAt(0)).toUpperCase(), ctx.getResources().getColor(R.color.colorPrimary));
                if (null != rootView.image) {
                    if (drawable != null) {
                        try {
                            rootView.image.setImageDrawable(drawable);
                        } catch (Exception e) {
                            //Toast.makeText(get, "Some features may not work", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //view.image.setImageDrawable(drawable);
                //get the screen width
                int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
//                view.linearLayout.getLayoutParams().width = (int)((widthPixels)/3);


                rootView.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(view, ussdActionWithStepsFiltered.get(position), position);
                        }
                    }
                });
                rootView.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onLongClick(v, ussdActionWithStepsFiltered.get(position), position);
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

    public AdapterDialer(Context context) {
        ctx = context;

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title, code, section;
        public LinearLayout linearLayout;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.ImageView_ActionIcon);
            title = (TextView) v.findViewById(R.id.TextView_ActionName);
            code = (TextView) v.findViewById(R.id.TextView_ActionCode);
            section = (TextView) v.findViewById(R.id.TextView_Action_section);
            linearLayout = v.findViewById(R.id.myAction);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, UssdActionWithSteps obj, int position);
        void onLongClick(View v, UssdActionWithSteps ussdActionWithSteps, int position);

    }


    public void setUssdActions(List<UssdActionWithSteps> actions) {
        this.items = actions;
        this.ussdActionWithStepsFiltered = actions;
        notifyDataSetChanged();
    }

    public void setContactList(ArrayList<String> nameList, ArrayList<String> numberList) {
        this.namelist = nameList;
        this.numberlist = numberList;
        //add the telephone numbers to the list of ussd codes
        Random r = new Random();
        for (int i = 0; i < numberlist.size(); i++) {
            Long codeId = r.nextLong();
            UssdAction ussdAction = new UssdAction(codeId, namelist.get(i),
                numberlist.get(i), null, null, SEC_CUSTOM_CODES);
            items.add(new UssdActionWithSteps(ussdAction, null));
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (ussdActionWithStepsFiltered != null) {
            return ussdActionWithStepsFiltered.size();
        } else {
            return 0;
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                String words = constraint.toString().replace("#", "");
                List<UssdActionWithSteps> l = new ArrayList<>();

                if (words.isEmpty()) {
                    l = items;

                } else {
                    Log.d("QUERY", words);
                    //loop through ussd codes
                    for (UssdActionWithSteps action : items) {

                        if(containsIgnoreCase(action.action.getName(),words)){
                            l.add(action);
                            continue;
                        }

                        if (action.action.getAirtelCode() != null) {
                            if (action.action.getAirtelCode().contains(words)) {
                                l.add(action);
                                continue;
                            }
                        }

                        if (action.action.getMtnCode() != null) {
                            if (action.action.getMtnCode().contains(words)) {
                                l.add(action);
                                continue;
                            }
                        }
                        if (action.action.getAfricellCode() != null) {
                            if (action.action.getAfricellCode().contains(words)) {
                                l.add(action);
                                continue;
                            }
                        }

                    }
//                    //loop through contacts
//                    for (int i = 0; i <numberlist.size() ; i++) {
//                        if(numberlist.get(i).contains(words)){
//                            UssdAction ussdAction = new UssdAction(-1, namelist.get(i).toString(),
//                                numberlist.get(i).toString(),null,null, SEC_CUSTOM_CODES);
//
//                            l.add(new UssdActionWithSteps(ussdAction,null));
//                        }
//                    }

                }
                FilterResults results = new FilterResults();
                results.values = l;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ussdActionWithStepsFiltered = (List<UssdActionWithSteps>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    public String getSectionFromId(int id) {
        if (id == SEC_AIRTIME) {
            return "AIRTIME";
        }
        if (id == SEC_DATA) {
            return "DATA";
        }
        if (id == SEC_MMONEY) {
            return "MOBILE MONEY";
        }
        if (id == SEC_USER_DIALED) {
            return "ME";
        }
        return "";//TODO change the ifs to a switch
    }

    /*
 adapted from mkyong.com
 */
    public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().trim().replaceAll(" ","").contains(subString.toLowerCase().trim().replaceAll(" ",""));
    }
}
