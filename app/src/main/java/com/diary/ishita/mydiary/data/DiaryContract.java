package com.diary.ishita.mydiary.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ishita sharma on 7/19/2017.
 */

public final class DiaryContract {

    public static final String CONTENT_AUTHORITY = "com.diary.ishita.mydiary";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY );
    public static final String PATH_DIARY = "diary";

    public static final String USER ="user";
    public static final String IMAGE ="image";
    private DiaryContract(){

    }

    public static final class DiaryEntry implements BaseColumns{

        public static final Uri CONTENT_URI= Uri.withAppendedPath(BASE_CONTENT_URI,PATH_DIARY);
        public static final String TABLE_NAME= "diary";

        //mime type for content uris
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + CONTENT_AUTHORITY+ "/" +PATH_DIARY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ PATH_DIARY;

        //particulars for table
        public static final String _ID= BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE ="date";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_DATA ="image_data";

        public static final Uri USER_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,USER);
        public static final String USER_TABLE_NAME ="user";
        public static final String USER_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY+"/"+USER;
        public static final String USER_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ USER;
        public static final String _USER_ID= BaseColumns._ID;
        public static final String USER_COLUMN_NAME = "title";
        public static final String USER_COLUMN_EMAIL ="date";
        public static final String USER_COLUMN_NOTES = "description";

        public static final Uri IMAGE_URI =Uri.withAppendedPath(BASE_CONTENT_URI,IMAGE);
        public static final String IMAGE_TABLE_NAME="image";
        public static final String IMAGE_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE +"/"+ CONTENT_AUTHORITY+"/"+IMAGE;
        public static final String IMAGE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/"+ CONTENT_AUTHORITY +"/"+ IMAGE;
        public static final String _IMAGE_ID= BaseColumns._ID;
        public static final String COLUMN_USER_IMAGE_DATA = "image_data";

    }
}
