package com.example.redbird;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SearchFragment extends DialogFragment {

    EditText confirm;
    Button cancelButton;
    Button searchButton;
    OnMyDialogResult mDialogResult;

    public SearchFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SearchFragment newInstance(String title) {
        SearchFragment frag = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return inflater.inflate(R.layout.searchable, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new OKListener());

        confirm = view.findViewById(R.id.confirmField);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // search();


    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (confirm.getText() != null) {
                mDialogResult.finish(String.valueOf(confirm.getText()));
            }
            SearchFragment.this.dismiss();
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult {
        void finish(String result);
    }

}























