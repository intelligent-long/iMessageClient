package com.longx.intelligent.android.ichat2.da.database.manager;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LONG on 2024/1/30 at 4:24 AM.
 */
public abstract class BaseDatabaseManager {
    private final AtomicInteger openCounter = new AtomicInteger();
    private final SQLiteOpenHelper helper;
    private SQLiteDatabase database;

    public BaseDatabaseManager(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    public synchronized void openDatabaseIfClosed() {
        if(openCounter.incrementAndGet() == 1) {
            database = helper.getWritableDatabase();
        }
    }

    public synchronized void releaseDatabaseIfUnused() {
        if(openCounter.decrementAndGet() == 0) {
            database.close();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }
}
