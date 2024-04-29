package com.longx.intelligent.android.lib.materialyoupreference.preferences;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import com.longx.intelligent.android.lib.materialyoupreference.R;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.divider.DividerHelper;

public class Material3SwitchPreference extends SwitchPreference {
    TextView mTitleTextView;
    TextView mSummaryTextView;
    private DividerHelper.DividerAllowRules dividerAllowRules;

    public Material3SwitchPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public Material3SwitchPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        dividerAllowRules = DividerHelper.parseDividerAllowRules(context, attrs);
        setLayoutResource(R.layout.material_preference_switch);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setDividerAllowedAbove(dividerAllowRules.isAllowDividerAbove());
        holder.setDividerAllowedBelow(dividerAllowRules.isAllowDividerBelow());

        mTitleTextView = (TextView) holder.itemView.findViewById(android.R.id.title);
        mSummaryTextView = (TextView) holder.itemView.findViewById(android.R.id.summary);

        if(mSummaryTextView != null) {

            if(mSummaryTextView.getText() == null || mSummaryTextView.getText().toString().isEmpty()) {
                mTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
            }

            mSummaryTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence != null && !charSequence.toString().isEmpty()) {
                        mTitleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}
