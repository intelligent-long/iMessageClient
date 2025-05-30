package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.databinding.BottomSheetAuthorAccountBinding;
import com.longx.intelligent.android.imessage.value.Constants;

/**
 * Created by LONG on 2024/12/29 at 下午10:59.
 */
public class AuthorAccountsBottomSheet extends AbstractBottomSheet{
    private BottomSheetAuthorAccountBinding binding;

    public AuthorAccountsBottomSheet(Activity activity) {
        super(activity);
        create();
    }

    @Override
    protected void onCreate() {
        binding = BottomSheetAuthorAccountBinding.inflate(getActivity().getLayoutInflater());
        setContentView(binding.getRoot());
        setupListeners();
    }

    private void setupListeners() {
        binding.imessage.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MY_IMESSAGE_URL));
                getActivity().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), Constants.APP_NAME + " ID: " + Constants.MY_IMESSAGE_ID, MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.email.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Constants.MY_EMAIL));
                getActivity().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "Email: " + Constants.MY_EMAIL, MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.qq.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MY_QQ_URL));
                getActivity().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "QQ: " + Constants.MY_QQ_ID, MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.wechat.setOnClickListener(v -> {
            MessageDisplayer.autoShow(getActivity(), "微信号: " + Constants.MY_WECHAT_ID, MessageDisplayer.Duration.LONG);
            dismiss();
        });
        binding.github.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MY_GITHUB_URL));
                getActivity().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "GitHub: " + Constants.MY_GITHUB_URL, MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
    }
}
