package com.diary.ishita.mydiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;

/**
 * Created by ishita sharma on 7/21/2017.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="base.db";
    private static final int DATABASE_VERSION = 1;
    public static final String USER_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+DiaryEntry.USER_TABLE_NAME+
            "("+ DiaryEntry._USER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DiaryEntry.USER_COLUMN_NAME+ " TEXT DEFAULT UNKNOWN," +
            DiaryEntry.USER_COLUMN_EMAIL+" TEXT, "+
            DiaryEntry.USER_COLUMN_NOTES+" TEXT )";

    public UserDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String Query = "DROP TABLE IF EXISTS "+ DiaryContract.USER;
        db.execSQL(Query);
        onCreate(db);
    }
}