package com.longx.intelligent.android.ichat2.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.content.ContextCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.databinding.ActivityOpenSourceLicensesBinding;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;

public class OpenSourceLicensesActivity extends BaseActivity {
    private ActivityOpenSourceLicensesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOpenSourceLicensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        showLicensesMessage();
    }

    private void showLicensesMessage() {
        binding.licensesMessage.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        binding.licensesMessage.setVerticalScrollBarEnabled(false);
        String html = buildHtml();
        html = changeUiMode(html);
        binding.licensesMessage.loadDataWithBaseURL("file:///android_res/font/", html, "text/html", "utf-8", null);
    }

    private String changeUiMode(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                html = html.replace("<textColor>", "color: " + ColorUtil.colorToRGB(ColorUtil.getAttrColor(this, android.R.attr.textColorPrimary)) + ";");
                binding.licensesMessage.getSettings().setForceDark(WebSettings.FORCE_DARK_ON);
            } else {
                html = html.replace("<textColor>", "");
                binding.licensesMessage.getSettings().setForceDark(WebSettings.FORCE_DARK_OFF);
            }
        }
        return html;
    }

    private String buildHtml() {
        return new StringBuilder()
                .append(getText(R.string.html_text_prefix))
                .append(getText(R.string.all_list))
                .append(getText(R.string.androidx))
                .append(getText(R.string.material_components_android))
                .append(getText(R.string.glide))
                .append(getText(R.string.subsampling_scale_image_view))
                .append(getText(R.string.retrofit))
                .append(getText(R.string.retrofit_helper))
                .append(getText(R.string.persistent_cookie_jar))
                .append(getText(R.string.okhttps_stomp))
                .append(getText(R.string.badge_view))
                .append(getText(R.string.android_image_cropper))
                .append(getText(R.string.apache_commons))
                .append(getText(R.string.hutool))
                .append(getText(R.string.tika))
                .append(getText(R.string.exo_player))
                .append(getText(R.string.flow_layout))
                .append(getText(R.string.html_text_suffix))
                .toString();
    }
}
