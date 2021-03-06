package com.mohammedsazid.android.done.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mohammedsazid.android.done.data.DoneContract.TasksTable;

/**
 * Created by MohammedSazid on 2/26/2015.
 */
public class DoneDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "done.db";
    private static final int DATABASE_VERSION = 1;

    public DoneDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database with the required table(s)
        TasksTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // for now, just drop the table if any upgrade is made to the database
        // and create a new one
        TasksTable.onUpgrade(db, oldVersion, newVersion);
    }

}
