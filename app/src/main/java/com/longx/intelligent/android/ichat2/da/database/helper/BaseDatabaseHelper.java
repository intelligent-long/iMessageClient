package com.longx.intelligent.android.ichat2.da.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.longx.intelligent.android.ichat2.da.DataPaths;
import com.longx.intelligent.android.ichat2.da.database.DatabaseFilePaths;

import java.io.File;
import java.util.Objects;

/**
 * Created by LONG on 2024/1/30 at 3:56 AM.
 */
public abstract class BaseDatabaseHelper extends SQLiteOpenHelper {

    public BaseDatabaseHelper(Context context, String databaseFileName, SQLiteDatabase.CursorFactory factory, int version, String ichatId) {
        super(context, DatabaseFilePaths.getDatabaseFilePath(context, ichatId, databaseFileName), factory, version);
        Objects.requireNonNull(new File(DatabaseFilePaths.getDatabaseFilePath(context, ichatId, databaseFileName)).getParentFile()).mkdirs();
    }
}
