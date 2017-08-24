package com.diary.ishita.mydiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.diary.ishita.mydiary.data.DiaryContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.diary.ishita.mydiary.MainActivity.user_nav_image;

public class UserImageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private ImageView imageView ;

    private static final int LOADER_ID= 5;
    public Uri insert_uri;
    private static boolean has_set_image=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);
        imageView=(ImageView)findViewById(R.id.add_user_image);
        String[] projection= new String[]{DiaryContract.DiaryEntry._IMAGE_ID,DiaryContract.DiaryEntry.COLUMN_USER_IMAGE_DATA};
        Cursor c = getContentResolver().query(DiaryContract.DiaryEntry.IMAGE_URI,projection,null,null,null);
            if (c.getCount()!=0) {
                has_set_image=true;
                 setTitle("Change photo");
                getLoaderManager().initLoader(LOADER_ID,null,this);
                c.moveToFirst();
                if(c.getBlob(1)!=null&&c!=null) {
                    byte[] image_byte = c.getBlob(1);
                    Bitmap bitmap = DbBitmapUtils.getImage(image_byte);
                    imageView.setImageBitmap(bitmap);
                }
            }
            else {
            setTitle("Add photo");
            invalidateOptionsMenu();
            imageView.setImageResource(R.mipmap.person_image);
             }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu,menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!has_set_image) {
            MenuItem menuItem = menu.findItem(R.id.delte_image);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.image_edit:
                if(!has_set_image){
                    addImage();
                }
                else {
                    imageEdit();
                }
                return true;
            case R.id.save_image:
                saveImage();
                finish();
                return true;
            case R.id.delte_image:
                deleteImage();
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap= drawable.getBitmap();
        byte[] byte_for_image = DbBitmapUtils.getBytes(bitmap);
        ContentValues values = new ContentValues();
        values.put(DiaryContract.DiaryEntry.COLUMN_USER_IMAGE_DATA,byte_for_image);
        if(!has_set_image) {
            insert_uri = getContentResolver().insert(DiaryContract.DiaryEntry.IMAGE_URI, values);
            Toast toast = Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT);
            if (insert_uri != null)
                toast.show();
            has_set_image=true;
        }
        else{
            int rows = getContentResolver().update(DiaryContract.DiaryEntry.IMAGE_URI,values,null,null);
            Toast toast = Toast.makeText(this, "Image updated!", Toast.LENGTH_SHORT);
            if(rows!=0)
                toast.show();
            has_set_image=true;
        }
        user_nav_image.setImageBitmap(bitmap);
      // Log.v("insert uri",insert_uri.toString());
    }

    private void deleteImage() {
        imageView.setImageResource(R.mipmap.person_image);
        getContentResolver().delete(DiaryContract.DiaryEntry.IMAGE_URI,null,null);
        insert_uri=null;
        MainActivity.user_nav_image.setImageResource(R.mipmap.person_image);
        has_set_image=false;
    }

    private void imageEdit() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(UserImageActivity.this);
        builder.setTitle("Change Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(UserImageActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void addImage() {
            final CharSequence[] items = { "Take Photo", "Choose from Library",
                    "Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder(UserImageActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    boolean result=Utility.checkPermission(UserImageActivity.this);
                    if (items[item].equals("Take Photo")) {
                        userChoosenTask="Take Photo";
                        if(result)
                            cameraIntent();
                    } else if (items[item].equals("Choose from Library")) {
                        userChoosenTask="Choose from Library";
                        if(result)
                            galleryIntent();
                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void galleryIntent()
    {
        /**Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

         **/
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, SELECT_FILE);
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG,90,bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(thumbnail);
    }

    public static Bitmap compressInputImage(Context context,Bitmap bitmapInputImage)
    {
        Bitmap bitmap=null;
        //Uri inputImageData = data.getData();
        try
        {
            if (bitmapInputImage.getWidth() > 2048 && bitmapInputImage.getHeight() > 2048)
            {
                bitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1024, 1280, true);

            }
            else if (bitmapInputImage.getWidth() > 2048 && bitmapInputImage.getHeight() < 2048)
            {
                bitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1920, 1200, true);
            }
            else if (bitmapInputImage.getWidth() < 2048 && bitmapInputImage.getHeight() > 2048)
            {
                bitmap = Bitmap.createScaledBitmap(bitmapInputImage, 1024, 1280, true);

            }
            else if (bitmapInputImage.getWidth() < 2048 && bitmapInputImage.getHeight() < 2048)
            {
                bitmap = Bitmap.createScaledBitmap(bitmapInputImage, bitmapInputImage.getWidth(), bitmapInputImage.getHeight(), true);

            }
        } catch (Exception e)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap bitmap=compressInputImage(this,bm);
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projecton = {DiaryContract.DiaryEntry._IMAGE_ID, DiaryContract.DiaryEntry.COLUMN_USER_IMAGE_DATA};
        return new CursorLoader(this, DiaryContract.DiaryEntry.IMAGE_URI,projecton,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount()==0)
            return;

        data.moveToFirst();
        byte[] byte_image = data.getBlob(data.getColumnIndex(DiaryContract.DiaryEntry.COLUMN_USER_IMAGE_DATA));
        Bitmap bitmap = DbBitmapUtils.getImage(byte_image);
        imageView.setImageBitmap(bitmap);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}