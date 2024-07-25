package com.longx.intelligent.android.ichat2.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.databinding.DialogFastLocateBinding;

/**
 * Created by LONG on 2024/7/25 at 下午12:28.
 */
public class FastLocateDialog extends AbstractDialog{
    public static char[] LOCATE_CHANNEL = {'.', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'};

    private final char[] locateChars;

    public FastLocateDialog(Activity activity, char[] locateChars) {
        super(activity);
        this.locateChars = locateChars;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        return builder.create();
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        DialogFastLocateBinding binding = DialogFastLocateBinding.inflate(layoutInflater);
        return binding.getRoot();
    }
}
