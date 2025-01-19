package com.longx.intelligent.android.imessage.util;

import android.annotation.SuppressLint;
import android.database.Cursor;

import java.util.Date;

/**
 * Created by LONG on 2024/1/30 at 5:56 AM.
 */
public class DatabaseUtil {

    @SuppressLint("Range")
    public static String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    @SuppressLint("Range")
    public static Integer getInteger(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        if (cursor.isNull(columnIndex)) {
            return null;
        } else {
            return cursor.getInt(columnIndex);
        }
    }

    @SuppressLint("Range")
    public static Long getLong(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        if (cursor.isNull(columnIndex)) {
            return null;
        } else {
            return cursor.getLong(columnIndex);
        }
    }

    @SuppressLint("Range")
    public static Boolean getBoolean(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if(cursor.isNull(columnIndex)){
            return null;
        }else {
            int value = cursor.getInt(cursor.getColumnIndex(columnName));
            return value == 1;
        }
    }

    @SuppressLint("Range")
    public static byte[] getBlob(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if(cursor.isNull(columnIndex)){
            return null;
        }else {
            return cursor.getBlob(cursor.getColumnIndex(columnName));
        }
    }

    public static Date getTime(Cursor cursor, String columnName){
        int columnIndex = cursor.getColumnIndex(columnName);
        if(cursor.isNull(columnIndex)){
            return null;
        }else {
            @SuppressLint("Range") long value = cursor.getLong(cursor.getColumnIndex(columnName));
            return new Date(value);
        }
    }

}
