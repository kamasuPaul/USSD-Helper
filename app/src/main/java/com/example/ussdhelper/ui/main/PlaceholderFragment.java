package com.example.ussdhelper.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.ussdhelper.R;
import com.example.ussdhelper.modals.UssdAction;
import com.example.ussdhelper.util.SQLiteDatabaseHandler;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    SQLiteDatabaseHandler db;
    ListView list;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blank, container, false);

        db = new SQLiteDatabaseHandler(getActivity());

        // list all usdActions
        final List<UssdAction> ussdActions = db.allUssdActions();

        if (ussdActions != null) {
            String[] itemsNames = new String[ussdActions.size()];

            for (int i = 0; i < ussdActions.size(); i++) {
                itemsNames[i] = ussdActions.get(i).toString();
            }

            // display like string instances
             list = (ListView) root.findViewById(R.id.list);
            list.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, itemsNames));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String ussdCode = "*131"+ Uri.encode("#");
                    String uscode = ussdActions.get(position).getCode();
                    String cd = uscode+ Uri.encode("#");
//                    String code = uscode.substring(uscode.lastIndexOf("#"));
//                    Toast.makeText(AddYourOwnActionActivity.this,uscode, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+cd)));
                }
            });

        }

        return root;
    }
}
