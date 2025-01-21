package com.longx.intelligent.android.imessage.activity.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.OpenSourceLicensesActivity;
import com.longx.intelligent.android.imessage.activity.VersionActivity;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.bottomsheet.AuthorAccountsBottomSheet;
import com.longx.intelligent.android.imessage.databinding.ActivityVersionSettingsBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.fragment.settings.BasePreferenceFragmentCompat;
import com.longx.intelligent.android.imessage.ui.glide.GlideApp;
import com.longx.intelligent.android.imessage.util.AppUtil;
import com.longx.intelligent.android.imessage.util.ErrorLogger;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.value.Constants;
import com.longx.intelligent.android.lib.materialyoupreference.preferences.Material3Preference;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class VersionSettingsActivity extends BaseActivity {
    private ActivityVersionSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVersionSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        setupPreferenceFragment(savedInstanceState);
    }

    private void setupPreferenceFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.settings.getId(), new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Material3Preference preferenceAuthor;
        private Material3Preference preferenceVersionName;
        private Material3Preference preferenceVersionCode;
        private Material3Preference preferenceOpenSourceLicenses;
        private Material3Preference preferenceUserGuide;

        @Override
        protected void init(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_version, rootKey);
            doDefaultActions();
        }

        @Override
        protected void bindPreferences() {
            preferenceAuthor = findPreference(getString(R.string.preference_key_author));
            preferenceVersionName = findPreference(getString(R.string.preference_key_version_name));
            preferenceVersionCode = findPreference(getString(R.string.preference_key_version_code));
            preferenceOpenSourceLicenses = findPreference(getString(R.string.preference_key_open_source_licenses));
            preferenceUserGuide = findPreference(getString(R.string.preference_key_user_guide));
        }

        @Override
        protected void showInfo() {
            preferenceAuthor.setSummary(Constants.AUTHOR);
            preferenceVersionName.setSummary(AppUtil.getVersionName(requireContext()));
            preferenceVersionCode.setSummary(String.valueOf(AppUtil.getVersionCode(requireContext())));
        }

        @Override
        protected void setupYiers() {
            preferenceAuthor.setOnPreferenceClickListener(this);
            preferenceOpenSourceLicenses.setOnPreferenceClickListener(this);
            preferenceUserGuide.setOnPreferenceClickListener(this);
            preferenceVersionName.setOnPreferenceClickListener(this);
            preferenceVersionCode.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            return false;
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if (preference.equals(preferenceAuthor)) {
                AtomicReference<Drawable> authorDrawable = new AtomicReference<>();
                CountDownLatch countDownLatch = new CountDownLatch(1);
                FutureTarget<Drawable> futureTarget = GlideApp.with(requireContext().getApplicationContext())
                        .asDrawable()
                        .load(R.drawable.default_avatar)
                        .override(UiUtil.dpToPx(requireContext(), 27), UiUtil.dpToPx(requireContext(), 27))
                        .submit();
                new Thread(() -> {
                    try {
                        authorDrawable.set(futureTarget.get());
                        countDownLatch.countDown();
                    } catch (ExecutionException | InterruptedException e) {
                        ErrorLogger.log(e);
                    }
                }).start();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    ErrorLogger.log(e);
                }
                ConfirmDialog dialog = new ConfirmDialog(getActivity(), R.style.AuthorDialog, null, "作者 " + Constants.AUTHOR, true)
                        .setNeutralButton("账号", (d, which) -> {
                            new AuthorAccountsBottomSheet(getActivity()).show();
                        })
                        .setPositiveButton()
                        .setIcon(authorDrawable.get())
                        .create()
                        .show();
                ImageView iconView = dialog.getDialog().findViewById(android.R.id.icon);
                if (iconView != null) {
                    iconView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            int width = iconView.getWidth();
                            if (width > 0) {
                                ViewGroup.LayoutParams params = iconView.getLayoutParams();
                                params.height = width;
                                iconView.setLayoutParams(params);
                            }
                            iconView.getViewTreeObserver().removeOnPreDrawListener(this);
                            return false;
                        }
                    });
                }
//                Snackbar snackbar = MessageDisplayer.showSnackbar(getActivity(), "作者 " + Constants.AUTHOR, Snackbar.LENGTH_INDEFINITE);
//                snackbar.setAction("账号", v -> new AuthorAccountsBottomSheet(getActivity()).show());
            } else if (preference.equals(preferenceOpenSourceLicenses)) {
                startActivity(new Intent(requireContext(), OpenSourceLicensesActivity.class));
            }else if(preference.equals(preferenceUserGuide)){
                new CustomViewMessageDialog((AppCompatActivity) requireActivity(), getString(R.string.user_guide_info)).create().show();
            }else if(preference.equals(preferenceVersionCode) || preference.equals(preferenceVersionName)){
                startActivity(new Intent(requireContext(), VersionActivity.class));
            }
            return true;
        }
    }
}