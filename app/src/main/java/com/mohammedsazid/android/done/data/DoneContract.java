package com.mohammedsazid.android.done.data;

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
         * Description of the task done<br>
         * <i>(not used currently)</i><br>
         * <b>[not required]</b>
         */
        public static final String COLUMN_DESCRIPTION = "description";

    }
}
