package com.longx.intelligent.android.ichat2.yier;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

/**
 * Created by LONG on 2024/4/13 at 5:56 AM.
 */
public class AutoCompleteTextViewAutoSelectOnItemClickYier implements AdapterView.OnItemClickListener {
    private AutoCompleteTextView autoCompleteTextView;

    public AutoCompleteTextViewAutoSelectOnItemClickYier(AutoCompleteTextView autoCompleteTextView) {
        this.autoCompleteTextView = autoCompleteTextView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setAutoCompleteTextViewListSelection(autoCompleteTextView, position);
    }

    public void setAutoCompleteTextViewListSelection(AutoCompleteTextView autoCompleteTextView, int position){
        autoCompleteTextView.setOnClickListener(v -> {
            autoCompleteTextView.setListSelection(position);
        });
    }
}
