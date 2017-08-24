package com.diary.ishita.mydiary;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;

public class UserProfileActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static EditText  user_name;
    private static EditText user_email;
    private static EditText user_note;
    private static boolean has_saved= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("My Profile");

        user_name=(EditText)findViewById(R.id.user_name_edit_text_view);
        user_email=(EditText)findViewById(R.id.user_email_edit_text_view);
        user_note =(EditText)findViewById(R.id.user_notes_edit_text_view);
        String[] projection = {DiaryEntry._USER_ID, DiaryEntry.USER_COLUMN_NAME, DiaryEntry.USER_COLUMN_EMAIL, DiaryEntry.USER_COLUMN_NOTES};
        Cursor cursor =getContentResolver().query(DiaryEntry.USER_CONTENT_URI,projection,null,null,null);
        if(cursor.getCount()!=0) {
            getLoaderManager().initLoader(URL_LOADER, null, this);
            has_saved = true;
        }
        else{
            invalidateOptionsMenu();
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (!has_saved) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }

        else if (id == R.id.action_save){
                save();
            finish();
            return true;
        }

        else if(id==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void save() {


        ContentValues values = new ContentValues();
        String name = user_name.getText().toString();
        String email = user_email.getText().toString();
        String notes = user_note.getText().toString();

        values.put(DiaryEntry.USER_COLUMN_NAME,name);
        values.put(DiaryEntry.USER_COLUMN_EMAIL,email);
        values.put(DiaryEntry.USER_COLUMN_NOTES,notes);
        Toast toast;
        String message=null;
        if(!has_saved) {
            Uri uri = getContentResolver().insert(DiaryEntry.USER_CONTENT_URI, values);
            if(uri!=null)
                has_saved=true;
            message="profile saved!!";
        }
        else{
            int rows=getContentResolver().update(DiaryEntry.USER_CONTENT_URI,values,null,null);
            if(rows!=0)
                message="profile updated!!";
        }
              toast= Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.show();
        MainActivity.user_nav_name.setText(name);
        MainActivity.user_nav_email.setText(email);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder= new  AlertDialog.Builder(this);
        builder.setMessage("Delete profile?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();
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

    private void delete() {

        getContentResolver().delete(DiaryEntry.USER_CONTENT_URI,null,null);

        Toast toast= Toast.makeText(this,"Profile deleted!!",Toast.LENGTH_SHORT);
        toast.show();

        MainActivity.user_nav_name.setText("unknown");
        MainActivity.user_nav_email.setText("email");
        has_saved=false;
        finish();
    }

    private final static int URL_LOADER= 2;
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DiaryEntry._USER_ID, DiaryEntry.USER_COLUMN_NAME, DiaryEntry.USER_COLUMN_EMAIL, DiaryEntry.USER_COLUMN_NOTES};
        switch (id){
            case URL_LOADER:
                return new CursorLoader(this,DiaryEntry.USER_CONTENT_URI,projection,null,null,null);
            default:
                //invalid id passed
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount()==0){
            return;
        }
        data.moveToFirst();
        user_name.setText(data.getString(data.getColumnIndex(DiaryEntry.USER_COLUMN_NAME)));
        user_email.setText(data.getString(data.getColumnIndex(DiaryEntry.USER_COLUMN_EMAIL)));
        user_note.setText(data.getString(data.getColumnIndex(DiaryEntry.USER_COLUMN_NOTES)));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
