package com.longx.intelligent.android.ichat2.da.database;

import android.annotation.SuppressLint;
import android.database.Cursor;

/**
 * Created by LONG on 2024/1/30 at 5:56 AM.
 */
public class DatabaseUtil {

    @SuppressLint("Range")
    public static String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

}
