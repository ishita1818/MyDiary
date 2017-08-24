package com.diary.ishita.mydiary.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ishita sharma on 7/24/2017.
 */
import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;

public class ImageDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="imagebase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+DiaryEntry.IMAGE_TABLE_NAME+
            "("+ DiaryEntry._IMAGE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            DiaryEntry.COLUMN_USER_IMAGE_DATA+" BLOB )";

    public ImageDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("create table",CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
