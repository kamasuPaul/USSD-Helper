package com.quickCodes.quickCodes.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.amulyakhare.textdrawable.TextDrawable;
import com.quickCodes.quickCodes.HelpActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.SettingsActivity;
import com.quickCodes.quickCodes.util.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        ImageView iconView = (ImageView) root.findViewById(R.id.image_view_user_icon);
        setUserIcon(iconView, Tools.getDeviceName(), getActivity());

        TextView device_name = root.findViewById(R.id.device_name);
        device_name.setText(Tools.getDeviceName());

        TextView device_id = root.findViewById(R.id.device_id);
        device_id.setText("Device id : " + Tools.getDeviceID(getActivity()));

        View settings = root.findViewById(R.id.linear_layout_settings);
        settings.setOnClickListener(view -> startActivity(new Intent(getActivity(), SettingsActivity.class)));

        View help = root.findViewById(R.id.linear_layout_help);
        help.setOnClickListener(view -> startActivity(new Intent(getActivity(), HelpActivity.class)));
        View send_feeback = root.findViewById(R.id.linear_layout_send_feeback);
        send_feeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@kamasupaul.com"});
                Email.putExtra(Intent.EXTRA_SUBJECT, "Quick codes Feedback");
                Email.putExtra(Intent.EXTRA_TEXT, "" + "");
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
            }
        });

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    private void setUserIcon(ImageView imageView, String name, Context ctx) {
        String icon_letter = String.valueOf((name).trim().charAt(0)).toUpperCase();
        if (!icon_letter.matches("[a-z]")) {//if the first character is not alphabetic
            //find the first alphabetic character in the name of this code
            Pattern pattern = Pattern.compile("\\p{Alpha}");
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                icon_letter = matcher.group();
            }
        }
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(icon_letter, ctx.getResources().getColor(R.color.colorPrimary));
        if (null != imageView) {
            if (drawable != null) {
                try {
                    imageView.setImageDrawable(drawable);
                } catch (Exception e) {
                    //Toast.makeText(get, "Some features may not work", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}