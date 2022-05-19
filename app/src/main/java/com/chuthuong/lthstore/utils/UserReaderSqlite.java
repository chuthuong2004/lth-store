package com.chuthuong.lthstore.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chuthuong.lthstore.model.User;

public class UserReaderSqlite extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "User";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_ACCESS_TOKEN = "accessToken";
    public static final String COLUMN_REFRESH_TOKEN = "refreshToken";

    public UserReaderSqlite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " VARCHAR PRIMARY KEY," + COLUMN_USERNAME + " VARCHAR," + COLUMN_ACCESS_TOKEN + " VARCHAR, " + COLUMN_REFRESH_TOKEN + " VARCHAR)";
        Log.e("Sql", createTable);
        db.execSQL(createTable);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        // đối tượng này giúp định nghĩa các cặp giá trị tương ứng với tên cột
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, user.getId());
        contentValues.put(COLUMN_USERNAME, user.getUsername());
        contentValues.put(COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(COLUMN_REFRESH_TOKEN, user.getRefreshToken());
        long result = database.insert(TABLE_NAME, null, contentValues);
        return result;
    }

    public long updateUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();

        // đối tượng này giúp định nghĩa các cặp giá trị tương ứng với tên cột
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, user.getId());
        contentValues.put(COLUMN_USERNAME, user.getUsername());
        contentValues.put(COLUMN_ACCESS_TOKEN, user.getAccessToken());
        contentValues.put(COLUMN_REFRESH_TOKEN, user.getRefreshToken());
        long result = database.update(TABLE_NAME, contentValues, COLUMN_ID + "=?", new String[]{user.getId()});
        return result;
    }

    public long deleteUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{user.getId()});
    }

    public User getUser() {
        User user = new User();
        String sql = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, null);
        Log.e("SIze", cursor.getCount() + "");
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            @SuppressLint("Range") String accessToken = cursor.getString(cursor.getColumnIndex(COLUMN_ACCESS_TOKEN));
            @SuppressLint("Range") String refreshToken = cursor.getString(cursor.getColumnIndex(COLUMN_REFRESH_TOKEN));
            user.setId(id);
            user.setUsername(username);
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            cursor.close();
            return user;
        }
        return null;
    }
}
