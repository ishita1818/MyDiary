package com.diary.ishita.mydiary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;
/**
 * Created by Ishita Sharma on 7/19/2017.
 */

public class DiaryCursorAdapter extends CursorAdapter {

    public DiaryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor.getCount() == 0) {
            return;
        } else {
            ImageView notes_image = (ImageView) view.findViewById(R.id.image_view_list_item);
            TextView title_notes = (TextView) view.findViewById(R.id.title_list_view);
            TextView date_notes = (TextView) view.findViewById(R.id.date_list_view);


            String title = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_TITLE));
            String date = cursor.getString(cursor.getColumnIndex(DiaryEntry.COLUMN_DATE));

            byte[] image_byte = cursor.getBlob(cursor.getColumnIndex(DiaryEntry.COLUMN_IMAGE_DATA));
            Bitmap bitmap = DbBitmapUtils.getImage(image_byte);
            notes_image.setImageBitmap(bitmap);

            title_notes.setText(title);
            date_notes.setText(date);
        }
    }
}