package com.example.officeorder.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB";
    private static final String TABLE_NAME = "user_table";
    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUserId(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USER_ID, userId);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean deleteUserId() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public boolean isUserIdEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        boolean isEmpty = true;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COL_USER_ID);
                if (columnIndex != -1) {
                    String userId = cursor.getString(columnIndex);
                    isEmpty = userId == null || userId.isEmpty();
                }
            }
            cursor.close();
        }

        return isEmpty;
    }
    public String getUserId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        String userId = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COL_USER_ID);
                if (columnIndex != -1) {
                    userId = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }

        return userId;
    }

}
