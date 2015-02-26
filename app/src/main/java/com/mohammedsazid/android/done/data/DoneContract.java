package com.mohammedsazid.android.done.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by MohammedSazid on 2/26/2015.
 * <br><br>
 * Each inner class represents a single table
 */
public class DoneContract {

    /**
     * The stats table which stores all the task related information
     */
    public static class StatsTable implements BaseColumns {

        /**
         * Name of the table
         */
        public static final String TABLE_NAME = "stats";

        /* Column names */

        /**
         * Label of the task<br>
         * <b>[required]</b>
         */
        public static final String COLUMN_TASK_NAME = "task_label";

        /**
         * Time taken to complete the task<br>
         * <b>[required, but default is "Task"]</b>
         */
        public static final String COLUMN_TASK_TIME = "task_time";

        /**
         * Timestamp of the moment when the task completed<br>
         * <b>[required]</b>
         */
        public static final String COLUMN_DATETIME = "datetime";

        /**
         * Status of the task (indicates whether the task is complete or not)
         * <b>[required]</b>
         */
        public static final String COLUMN_TASK_STATUS = "task_status";

        /**
         * Description of the task done<br>
         * <i>(not used currently)</i><br>
         * <b>[not required]</b>
         */
        public static final String COLUMN_DESCRIPTION = "description";

        /**
         * The SQL statement for creating the table
         */
        private static final String DATABASE_CREATE_SQL =
                "CREATE TABLE " + StatsTable.TABLE_NAME
                        + " ("
                        + StatsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + StatsTable.COLUMN_TASK_NAME + " TEXT NOT NULL, "
                        + StatsTable.COLUMN_TASK_TIME + " REAL NOT NULL, "
                        + StatsTable.COLUMN_DATETIME + " TEXT NOT NULL,"
                        + StatsTable.COLUMN_TASK_STATUS + " TEXT NOT NULL,"
                        + StatsTable.COLUMN_DESCRIPTION + " TEXT"
                        + ");";


        /**
         * @param db The SQLiteDatabase object to create the new table
         */
        public static void onCreate(SQLiteDatabase db) {
            db.execSQL(StatsTable.DATABASE_CREATE_SQL);
        }

        /**
         * Upgrades the existing database schema of the table
         */
        public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + StatsTable.TABLE_NAME);
            StatsTable.onCreate(db);
        }
    }
}