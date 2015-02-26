package com.mohammedsazid.android.done.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import static com.mohammedsazid.android.done.data.DoneContract.StatsTable;

/**
 * Created by MohammedSazid on 2/26/2015.
 */
public class DoneProvider extends ContentProvider {

    // used for the Uri matcher
    private static final int TASKS = 10;
    private static final int TASK_ID = 20;
    // content authority
    private static final String CONTENT_AUTHORITY = "com.mohammedsazid.android.done.provider";
    // com.mohammedsazid.android.done.provider/tasks
    private static final String BASE_PATH = "tasks";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY + "/" + BASE_PATH);
    // vnd.android.cursor.dir/tasks
    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/tasks";
    // vnd.android.cursor.item/task
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/task";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH, TASKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, BASE_PATH + "/#", TASK_ID);
    }

    // database
    private DoneDbHelper dbHelper;

    @Override
    synchronized public boolean onCreate() {
        dbHelper = new DoneDbHelper(getContext());
        return false;
    }

    @Override
    synchronized public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        // Using SQLiteQueryBuilder instead of query method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the requested caller has requested a column which does not exist
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(StatsTable.TABLE_NAME);

        int uriType = sUriMatcher.match(uri);
        switch (uriType) {
            case TASKS:
                break;
            case TASK_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(StatsTable._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor =
                queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    synchronized public Uri insert(Uri uri, ContentValues values) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;

        switch (uriType) {
            case TASKS:
                id = db.insert(StatsTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    synchronized public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = 0;

        switch (uriType) {
            case TASKS:
                deletedRows = db.delete(StatsTable.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                String task_id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    db.delete(
                            StatsTable.TABLE_NAME,
                            StatsTable._ID + "=" + task_id,
                            null);
                } else {
                    db.delete(StatsTable.TABLE_NAME,
                            StatsTable._ID + "=" + task_id + " AND " + selection,
                            selectionArgs);
                }

                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    synchronized public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update of values not allowed");
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case TASKS:
                return CONTENT_TYPE;
            case TASK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                StatsTable._ID,
                StatsTable.COLUMN_TASK_NAME,
                StatsTable.COLUMN_TASK_TIME,
                StatsTable.COLUMN_DATETIME,
                StatsTable.COLUMN_TASK_STATUS,
                StatsTable.COLUMN_DESCRIPTION
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown column(s) in projection");
            }
        }
    }
}
