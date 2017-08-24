package com.diary.ishita.mydiary.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;
/**
 * Created by ISHITA SHARMA on 7/19/2017.
 */

public class DiaryProvider extends ContentProvider {

    private DiaryDbHelper mDbHelper;
    private ImageDbHelper mImageDbHelper;
    //set codes for different tyye of uri paths
    private static final int DIARY=100;
    private static final int DIARY_ITEM = 101;
    private UserDbHelper mUserDbHelper;
    private static final int USER= 102;
    private static final int USER_ITEM=103;
     private static final int IMAGE =104;
    //create a uri matcher
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        mUriMatcher.addURI(DiaryContract.CONTENT_AUTHORITY,DiaryContract.PATH_DIARY,DIARY);

        mUriMatcher.addURI(DiaryContract.CONTENT_AUTHORITY,DiaryContract.PATH_DIARY+"/#",DIARY_ITEM);

        mUriMatcher.addURI(DiaryContract.CONTENT_AUTHORITY,DiaryContract.USER,USER);
        mUriMatcher.addURI(DiaryContract.CONTENT_AUTHORITY,DiaryContract.USER+"/#",USER_ITEM);

        mUriMatcher.addURI(DiaryContract.CONTENT_AUTHORITY,DiaryContract.IMAGE,IMAGE);
    }


    @Override
    public boolean onCreate() {

        mDbHelper = new DiaryDbHelper(getContext());
        mUserDbHelper= new UserDbHelper(getContext());
        mImageDbHelper = new ImageDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query( Uri uri, String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        //get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        //define a cursor which will hold the data
        SQLiteDatabase data = mUserDbHelper.getReadableDatabase();
        SQLiteDatabase image_database = mImageDbHelper.getReadableDatabase();

        Cursor cursor= null;
        //match the uri passed and then identify the case to query
        final int match = mUriMatcher.match(uri);

        switch (match){
            case DIARY:
                cursor= db.query(DiaryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DIARY_ITEM:
                selection= DiaryEntry._ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor= db.query(DiaryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case USER:
                Log.v("before query",uri.toString());
                cursor= data.query(DiaryEntry.USER_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                return cursor;
            case USER_ITEM:
                selection= DiaryEntry._USER_ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor= data.query(DiaryEntry.USER_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case IMAGE:
                cursor=image_database.query(DiaryEntry.IMAGE_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Override
    public String getType( Uri uri) {
        final int match= mUriMatcher.match(uri);
        switch (match){
            case DIARY:
                return DiaryEntry.CONTENT_LIST_TYPE;
            case DIARY_ITEM:
                return DiaryEntry.CONTENT_ITEM_TYPE;
            case USER:
                return DiaryEntry.USER_LIST_TYPE;
            case USER_ITEM:
                return DiaryEntry.USER_ITEM_TYPE;
            case IMAGE:
                return DiaryEntry.IMAGE_LIST_TYPE;
            default:
                throw new IllegalArgumentException("unknown uri "+uri);
        }
    }

    @Override
    public Uri insert( Uri uri, ContentValues values) {
        final int match= mUriMatcher.match(uri);
        switch (match){
            case DIARY:
                return insertNote(uri,values);
            case USER:
                return insertUserNote(uri,values);
            case IMAGE:
                return insertImage(uri,values);
            default:
                throw new IllegalArgumentException("Cannot perform insert on unknown URI "+uri);
        }
    }

    private Uri insertImage(Uri uri,ContentValues values ) {
        SQLiteDatabase db = mImageDbHelper.getReadableDatabase();
        long id= db.insert(DiaryEntry.IMAGE_TABLE_NAME,null,values);
        if(id!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return ContentUris.withAppendedId(uri,id);
    }

    private Uri insertUserNote(Uri uri, ContentValues values) {
        //get readable database
        SQLiteDatabase db = mUserDbHelper.getReadableDatabase();
        long id= db.insert(DiaryEntry.USER_TABLE_NAME,null,values);
        if(id!=0)
            getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    private Uri insertNote(Uri uri, ContentValues values) {
        //get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long id= db.insert(DiaryEntry.TABLE_NAME,null,values);
        if(id!=0)
            getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase data = mUserDbHelper.getWritableDatabase();
        SQLiteDatabase image_database= mImageDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rows;
        switch (match){
            case DIARY:
                rows = db.delete(DiaryEntry.TABLE_NAME,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case DIARY_ITEM:
                selection= DiaryEntry._ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows= db.delete(DiaryEntry.TABLE_NAME,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case USER:
                rows = data.delete(DiaryEntry.USER_TABLE_NAME,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case USER_ITEM:
                selection= DiaryEntry._USER_ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows=data.delete(DiaryEntry.USER_TABLE_NAME,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case IMAGE:
                rows=image_database.delete(DiaryEntry.IMAGE_TABLE_NAME,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            default:
                throw new IllegalArgumentException("cannot perform delete on unknown URI "+ uri);

        }
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.size()==0){
            return 0;
        }
        //get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase data = mUserDbHelper.getWritableDatabase();
        SQLiteDatabase image_database= mImageDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int rows;
        switch (match){
            case DIARY:
                rows=db.update(DiaryEntry.TABLE_NAME,values,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case DIARY_ITEM:
                selection= DiaryEntry._ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows = db.update(DiaryEntry.TABLE_NAME,values,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case USER:

                rows=data.update(DiaryEntry.USER_TABLE_NAME,values,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            case USER_ITEM:
                selection=DiaryEntry._USER_ID+"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rows= data.update(DiaryEntry.USER_TABLE_NAME,values,selection,selectionArgs);
                return rows;
            case IMAGE:
                //selection=DiaryEntry._USER_ID+"=?";
                //selectionArgs= new String[]{"1"};
                    rows= image_database.update(DiaryEntry.IMAGE_TABLE_NAME,values,selection,selectionArgs);
                if(rows!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rows;
            default:
                 throw new IllegalArgumentException("Cannot update the unknown URI "+uri);
        }

    }
}