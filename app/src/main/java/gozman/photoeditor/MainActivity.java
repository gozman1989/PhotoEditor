package gozman.photoeditor;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends Activity {
    private static final int SELECT_PHOTO = 100;

    private ImageView mPhotoContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mPhotoContainer= (ImageView) this.findViewById(R.id.photoContainer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void choosePhoto(View view){
       /* Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image*//*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);*/

        String files[]=Environment.getExternalStorageDirectory().list();
        Log.d("Goz", "files="+files);
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/Download/Desert.jpg");
        ImageView imageView= (ImageView) this.findViewById(R.id.photoContainer);
        imageView.setImageBitmap(bitmap);

        Button button= (Button) this.findViewById(R.id.image_crop_form);
        button.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        ImageView imageView= (ImageView) this.findViewById(R.id.photoContainer);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
}
