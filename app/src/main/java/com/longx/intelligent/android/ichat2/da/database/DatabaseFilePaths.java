package com.longx.intelligent.android.ichat2.da.database;

import android.content.Context;

import com.longx.intelligent.android.ichat2.da.DataPaths;

import java.io.File;

/**
 * Created by LONG on 2024/4/30 at 12:30 AM.
 */
public class DatabaseFilePaths {

    public static String getDatabaseFilePath(Context context, String ichatId, String databaseFileName) {
        return DataPaths.getDataFolderPath(context) +
                File.separator + "database" +
                File.separator + ichatId +
                File.separator + databaseFileName;
    }
}
