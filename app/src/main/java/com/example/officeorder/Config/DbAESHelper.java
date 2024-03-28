package com.example.officeorder.Config;

import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class DbAESHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "db_aes";
    private static final int DATABASE_VERSION = 2;

    // Table name and column names
    public static final String TABLE_NAME = "tb_aes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_KEY = "keyaes";
        // SQL statement to create the table
        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_KEY + " BLOB)";

        public DbAESHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // You can implement upgrade logic here if needed
        }

        // Add a new record
        public long addRecord(String name, byte[] key) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_KEY, key);
            long newRowId = db.insert(TABLE_NAME, null, values);
            db.close();
            return newRowId;
        }

        // Get records by name
        public Cursor getRecordsByName(String name) {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] projection = {COLUMN_ID, COLUMN_NAME, COLUMN_KEY};
            String selection = COLUMN_NAME + " = ?";
            String[] selectionArgs = {name};
            Cursor cursor = db.query(
                    TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );
            return cursor;
        }

    // Thêm hàm mới trong lớp DatabaseHelper
    public byte[] getKeyByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {COLUMN_KEY};
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        byte[] key = null;

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy giá trị từ cột COLUMN_KEY
            int columnIndex = cursor.getColumnIndex(COLUMN_KEY);
            key = cursor.getBlob(columnIndex);
            cursor.close();
        }

        return key;
    }

}
