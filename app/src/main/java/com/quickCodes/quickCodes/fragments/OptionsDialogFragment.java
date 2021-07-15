package com.quickCodes.quickCodes.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.quickCodes.quickCodes.EditActionActivity;
import com.quickCodes.quickCodes.R;
import com.quickCodes.quickCodes.modals.UssdActionWithSteps;
import com.quickCodes.quickCodes.util.database.UssdActionsViewModel;

import static com.quickCodes.quickCodes.modals.Constants.SEC_CUSTOM_CODES;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ItemListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class OptionsDialogFragment extends BottomSheetDialogFragment {
    UssdActionsViewModel viewModel;
    UssdActionWithSteps action;

    public OptionsDialogFragment(UssdActionsViewModel viewModel, UssdActionWithSteps action) {
        this.viewModel = viewModel;
        this.action = action;
    }

    public static OptionsDialogFragment newInstance(UssdActionsViewModel viewModel, UssdActionWithSteps action) {

        final OptionsDialogFragment fragment = new OptionsDialogFragment(viewModel, action);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sheet_list, container, false);
        View edit = root.findViewById(R.id.edit_action);
        View star = root.findViewById(R.id.star_action);
        View delete = root.findViewById(R.id.delete_action);
        View copy = root.findViewById(R.id.copy_action);

        edit.setOnClickListener(view -> {
            editAction();
        });
        star.setOnClickListener(view -> {
            starAction();
        });
        delete.setOnClickListener(view -> {
            deleteAction();
        });
        copy.setOnClickListener(view -> {
            copyAction();
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    private void editAction() {
        this.dismiss();
        Intent i = new Intent(getActivity(), EditActionActivity.class);
        i.putExtra("action_id", String.valueOf(action.action.getActionId()));
        i.putExtra("section", SEC_CUSTOM_CODES);
        startActivity(i);
    }

    public void deleteAction() {
        this.dismiss();
        viewModel.delete(this.action);
        Toast.makeText(getActivity(), "Deleted successfully", Toast.LENGTH_SHORT).show();
    }

    public void starAction() {
        this.dismiss();
        this.action.action.setStarred(!this.action.action.isStarred());
        viewModel.update(this.action);
        Toast.makeText(getActivity(), "starred/unstarred successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyAction() {
        String text = this.action.action.getCode();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Ussd code", text);
            clipboard.setPrimaryClip(clip);
        }
        this.dismiss();
        Toast.makeText(getActivity(), "copied successfully", Toast.LENGTH_SHORT).show();
    }


}