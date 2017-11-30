package com.diary.ishita.mydiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RESULT_TITLE_SPEECH = 100;
    private static final int RESULT_DESCRIPTION_TEXT = 101;
    private static ImageView image_view_detail_activity;
    private static EditText title_text_view;
    private static TextView date_text_view;
    private static EditText description_text_view;
    private static Uri CURRENT_DIARY_URI;
    private static final int LOADER_ID= 1;
    private static Button date_range;
    private static Button title_mic_button;
    private static Button description_mic_button;
    private static Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent =getIntent();
        CURRENT_DIARY_URI=intent.getData();
        if(CURRENT_DIARY_URI==null){
            setTitle("Add note");
            invalidateOptionsMenu();
        }
        else {
            setTitle("Edit note");
            getLoaderManager().initLoader(LOADER_ID,null,this);
        }

        image_view_detail_activity= (ImageView)findViewById(R.id.detail_activity_image_view);
        title_text_view=(EditText) findViewById(R.id.title_note);
        date_text_view= (TextView)findViewById(R.id.date_detail_activity);
        description_text_view=(EditText)findViewById(R.id.description_detail_Activity);
        date_range =(Button)findViewById(R.id.date_selector);
        title_mic_button=(Button)findViewById(R.id.title_mic_button);
        description_mic_button=(Button)findViewById(R.id.description_mic_button);
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        date_range.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(DetailActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        image_view_detail_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,ImageActivity.class);
                intent.setData(CURRENT_DIARY_URI);
                startActivity(intent);
            }
        });
        image_view_detail_activity.setOnTouchListener(mTouchListener);
        date_range.setOnTouchListener(mTouchListener);
        title_text_view.setOnTouchListener(mTouchListener);
        description_text_view.setOnTouchListener(mTouchListener);
        title_mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, new String[]{"hin-IND"});
                try{
                    startActivityForResult(intent,RESULT_TITLE_SPEECH);
                }catch (ActivityNotFoundException a){
                    Toast toast = Toast.makeText(getApplicationContext(),"Your device dosen't support speech recognisation!",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        description_mic_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,new String[]{"en"});
                try{
                    startActivityForResult(intent,RESULT_DESCRIPTION_TEXT);
                }catch (ActivityNotFoundException a){
                    Toast toast = Toast.makeText(getApplicationContext(),"Your device dosen't support speech recognisation!",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case RESULT_TITLE_SPEECH:
                if(resultCode==RESULT_OK&& data!=null){
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    title_text_view.setText(text.get(0));
                }
                break;
            case RESULT_DESCRIPTION_TEXT:
                if(resultCode==RESULT_OK && data!=null){
                    ArrayList<String> text= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    description_text_view.setText(text.get(0));
                }
                 break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new diary, hide the "Delete" menu item.
        if (CURRENT_DIARY_URI == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note,menu);
        return true;
    }
private boolean has_filled_diary= false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            has_filled_diary = true;
            return false;
        }
    };



    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changes may not be saved!");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the diary.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the diary hasn't changed, continue with handling back button press
        if (!has_filled_diary) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder= new  AlertDialog.Builder(this);
        builder.setMessage("Delete this diary?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteDiary();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_save:
                if(CURRENT_DIARY_URI==null)
                saveDiary(DiaryEntry.CONTENT_URI);
                else
                    saveDiary(CURRENT_DIARY_URI);
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    private static byte[] image_view_set_byte;
    private static boolean has_set_image_byte= false;

    public static void setImagebyte(byte[] image_byte){
        image_view_set_byte=image_byte;
        has_set_image_byte=true;
        Bitmap bitmap = DbBitmapUtils.getImage(image_byte);
        image_view_detail_activity.setImageBitmap(bitmap);
    }

    private void saveDiary(Uri saveUri) {
        ContentValues values = new ContentValues();

        String title= title_text_view.getText().toString();
        String date = date_text_view.getText().toString();
        String description = description_text_view.getText().toString();
        byte[] image_bytes;
        //if(has_set_image_byte) {
            BitmapDrawable drawable = (BitmapDrawable) image_view_detail_activity.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            image_bytes= DbBitmapUtils.getBytes(bitmap);
        /**}
        else{
            image_view_detail_activity.setImageResource(R.mipmap.person_image);
            BitmapDrawable drawable = (BitmapDrawable) image_view_detail_activity.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            image_bytes= DbBitmapUtils.getBytes(bitmap);
        }
**/

        values.put(DiaryEntry.COLUMN_TITLE,title);
        values.put(DiaryEntry.COLUMN_DATE,date);
        values.put(DiaryEntry.COLUMN_DESCRIPTION,description);
        values.put(DiaryEntry.COLUMN_IMAGE_DATA, image_bytes);

        Toast toast;
        String message;

        if(saveUri.equals(DiaryEntry.CONTENT_URI)){
            Uri uri =getContentResolver().insert(DiaryEntry.CONTENT_URI,values);
            if(uri!=null)
                message="Note saved!";
            else
                message="Error saving note";
        }
        else{

            int rows= getContentResolver().update(CURRENT_DIARY_URI,values,null,null);
            if(rows!=0)
                message="Note updated!";
            else
                message="Error updating note";
        }
        toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
        has_set_image_byte=false;
        MainActivity.has_diary=true;
        finish();
    }

    private void deleteDiary() {

        if(CURRENT_DIARY_URI!=null){
            int rows= getContentResolver().delete(CURRENT_DIARY_URI,null,null);
            Toast toast;
            String message;
            if(rows!=0){
                message="Note deleted";
            }
            else
                message="Error deleting note";
            toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection= {DiaryEntry._ID,DiaryEntry.COLUMN_TITLE,DiaryEntry.COLUMN_DATE,DiaryEntry.COLUMN_IMAGE_DATA,DiaryEntry.COLUMN_DESCRIPTION};
        return new CursorLoader(this,CURRENT_DIARY_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        update(data);
    }

    private void update(Cursor data) {
        if(data.getCount()==0)
            return;
        else {
            data.moveToFirst();
            String title = data.getString(data.getColumnIndex(DiaryEntry.COLUMN_TITLE));
            String date = data.getString(data.getColumnIndex(DiaryEntry.COLUMN_DATE));
            byte[] image_byte = data.getBlob(data.getColumnIndex(DiaryEntry.COLUMN_IMAGE_DATA));
            if (image_byte == null) {
                image_view_detail_activity.setImageResource(R.mipmap.person_image);
            } else {
                Bitmap bitmap = DbBitmapUtils.getImage(image_byte);
                image_view_detail_activity.setImageBitmap(bitmap);
            }

            String description = data.getString(data.getColumnIndex(DiaryEntry.COLUMN_DESCRIPTION));
            Log.v("DetailAvtivity", description);
            title_text_view.setText(title);
            date_text_view.setText(date);
            description_text_view.setText(description);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void updateLabel() {

        String myFormat = "MMM dd yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        date_text_view.setText(sdf.format(myCalendar.getTime()));
    }

}
