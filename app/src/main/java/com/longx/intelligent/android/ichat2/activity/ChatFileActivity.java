package com.longx.intelligent.android.ichat2.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.documentfile.provider.DocumentFile;

import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.da.FileAccessHelper;
import com.longx.intelligent.android.ichat2.data.ChatMessage;
import com.longx.intelligent.android.ichat2.databinding.ActivityChatFileBinding;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChatFileActivity extends BaseActivity {
    private ActivityChatFileBinding binding;
    private ChatMessage chatMessage;
    private ActivityResultLauncher<Intent> folderPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        chatMessage = getIntent().getParcelableExtra(ExtraKeys.CHAT_MESSAGE);
        showContent();
        setupYiers();
        folderPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri folderUri = data.getData();
                            getContentResolver().takePersistableUriPermission(folderUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            InputStream is = FileAccessHelper.streamOf(chatMessage.getFileFilePath());
                            String fileName = chatMessage.getFileName();
                            String mimeType = FileAccessHelper.getMimeType(chatMessage.getFileFilePath());
                            saveFileInFolder(folderUri, fileName, is, mimeType);
                        }
                    }
                }
        );
    }

    private void showContent() {
        binding.fileName.setText(chatMessage.getFileName());
        binding.fileSize.setText(FileUtil.formatFileSize(FileUtil.getFileSize(chatMessage.getFileFilePath())));
    }

    private void setupYiers() {
        binding.open.setOnClickListener(v -> {
            FileAccessHelper.openFile(this, chatMessage.getFileFilePath());
        });
        binding.save.setOnClickListener(v -> {
            selectFolder();
        });
    }

    public void selectFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        folderPickerLauncher.launch(intent);
    }

    public void saveFileInFolder(Uri folderUri, String fileName, InputStream is, String mimeType) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, folderUri);
        if (pickedDir != null && pickedDir.isDirectory()) {
            DocumentFile newFile = pickedDir.createFile(mimeType, fileName);
            try (OutputStream os = getContentResolver().openOutputStream(newFile.getUri())) {
                FileUtil.transfer(is, os);
                MessageDisplayer.autoShow(this, "文件已保存", MessageDisplayer.Duration.LONG);
            } catch (IOException e) {
                ErrorLogger.log(e);
                MessageDisplayer.autoShow(this, "文件保存失败", MessageDisplayer.Duration.LONG);
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    ErrorLogger.log(e);
                }
            }
        } else {
            MessageDisplayer.autoShow(this, "选择的文件夹无效", MessageDisplayer.Duration.LONG);
        }
    }
}