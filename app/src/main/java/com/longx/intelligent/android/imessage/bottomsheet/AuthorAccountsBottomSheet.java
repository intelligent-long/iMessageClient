package com.longx.intelligent.android.imessage.bottomsheet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("imessage://channel/LONG"));
                getActivity().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), Constants.APP_NAME + " ID: LONG", MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.email.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:tx.long@outlook.com"));
                getActivity().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "Email: tx.long@outlook.com", MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.qq.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mqq://im/chat?chat_type=wpa&uin=909691944&version=1&src_type=web"));
                getActivity().startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "QQ: 909691944", MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
        binding.wechat.setOnClickListener(v -> {
            MessageDisplayer.autoShow(getActivity(), "微信号: _909691944", MessageDisplayer.Duration.LONG);
            dismiss();
        });
        binding.github.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/intelligent-long"));
                getActivity().startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDisplayer.autoShow(getActivity(), "GitHub: https://github.com/intelligent-long", MessageDisplayer.Duration.LONG);
            }
            dismiss();
        });
    }
}
