package com.diary.ishita.mydiary;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diary.ishita.mydiary.data.DiaryContract.DiaryEntry;
import com.diary.ishita.mydiary.data.DiaryDbHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor>{

    private  DiaryDbHelper mDbHelper;
    private ListView listView;
    private DiaryCursorAdapter mAdapter;
    private View EmptyView;
    public static TextView user_nav_name;
    public static TextView user_nav_email;
    public static ImageView user_nav_image;
    public static boolean has_set_image = false;
    TextToSpeech textToSpeech;

    public void toSpeak(String s){
        textToSpeech.speak(s,TextToSpeech.QUEUE_FLUSH,null);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDbHelper = new DiaryDbHelper(this);
        // Create and/or open a database to read from it
        listView = (ListView)findViewById(R.id.list_view);

        EmptyView = (View)findViewById(R.id.empty_view);
        listView.setEmptyView(EmptyView);

        mAdapter = new DiaryCursorAdapter(this,null);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri= ContentUris.withAppendedId(DiaryEntry.CONTENT_URI,id);

                Intent intent= new Intent(MainActivity.this,DetailActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(URL_LOADER,null,this);
        View header=navigationView.getHeaderView(0);
        user_nav_name = (TextView)header.findViewById(R.id.user_name);
        user_nav_email = (TextView)header.findViewById(R.id.user_email);
        user_nav_image = (ImageView)header.findViewById(R.id.User_photo);

        user_nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this,UserImageActivity.class);
                   startActivity(intent);
            }
        });

        String[] projrction =new String[]{DiaryEntry._USER_ID,DiaryEntry.USER_COLUMN_NAME,DiaryEntry.USER_COLUMN_EMAIL};

        Cursor cur = getContentResolver().query(DiaryEntry.USER_CONTENT_URI,projrction,null,null,null);
        if(cur.getCount()!=0) {
            cur.moveToFirst();
            user_nav_name.setText(cur.getString(cur.getColumnIndex(DiaryEntry.USER_COLUMN_NAME)));
            user_nav_email.setText(cur.getString(cur.getColumnIndex(DiaryEntry.USER_COLUMN_EMAIL)));
        }

        String[] projection = new String[]{ DiaryEntry._IMAGE_ID,DiaryEntry.COLUMN_USER_IMAGE_DATA};
        Cursor c= getContentResolver().query(DiaryEntry.IMAGE_URI,projection,null,null,null);
        if((c.getCount()!=0)&&(c!=null)){
            c.moveToFirst();
            if(c.getBlob(1)!=null) {
                byte[] image = c.getBlob(1);
                Bitmap b = DbBitmapUtils.getImage(image);
                user_nav_image.setImageBitmap(b);
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public static boolean has_diary= true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            if(has_diary)
            showDeleteConfirmationDialog();
            else{
                Toast toast= Toast.makeText(this,"No entries to delete!",Toast.LENGTH_SHORT);
                toSpeak("No entries to delete");
                toast.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder= new  AlertDialog.Builder(this);
        builder.setMessage("Delete all diaries?");
        toSpeak("Delete all diaries?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAll();
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


    private void deleteAll() {

        getContentResolver().delete(DiaryEntry.CONTENT_URI,null,null);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.user_profile:
             Intent intent = new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.nav_send:
               emailIntent();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void emailIntent() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback regarding myDiary app");
        intent.putExtra(Intent.EXTRA_EMAIL,new String[]{"ishitasharma12699@gmail.com"});
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }

    }

    private final static int URL_LOADER= 0;
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DiaryEntry._ID, DiaryEntry.COLUMN_TITLE, DiaryEntry.COLUMN_DATE, DiaryEntry.COLUMN_IMAGE_DATA};
        switch (id){
            case URL_LOADER:
                return new CursorLoader(this,DiaryEntry.CONTENT_URI,projection,null,null,null);
            default:
                //invalid id passed
                return null;
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if(data.getCount()==0){
            has_diary=false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
         mAdapter.swapCursor(null);
    }
    }