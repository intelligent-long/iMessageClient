package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatFileBinding;
import com.longx.intelligent.android.ichat2.util.FileUtil;

public class ChatFileActivity extends BaseActivity {
    private ActivityChatFileBinding binding;
    private ChatMessage chatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        chatMessage = getIntent().getParcelableExtra(ExtraKeys.CHAT_MESSAGE);
        showContent();
    }

    private void showContent() {
        binding.fileName.setText(chatMessage.getFileName());
        binding.fileSize.setText(FileUtil.formatFileSize(FileUtil.getFileSize(chatMessage.getFileFilePath())));
    }
}