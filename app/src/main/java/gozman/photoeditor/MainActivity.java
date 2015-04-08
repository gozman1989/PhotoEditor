package gozman.photoeditor;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends Activity {
    public static final String TAG=MainActivity.class.getSimpleName();

    private static final int SELECT_PHOTO = 100;

    private ImageView mPhotoContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        this.mPhotoContainer= (ImageView) this.findViewById(R.id.photoContainer);
        ImageView cropLoopImg=(ImageView) findViewById(R.id.crop_logo_img);

        cropLoopImg.getLayoutParams().height=mPhotoContainer.getLayoutParams().height;
        cropLoopImg.getLayoutParams().width=mPhotoContainer.getLayoutParams().width;

        RelativeLayout croopLoop=(RelativeLayout) findViewById(R.id.crop_loop);
        RelativeLayout cropContainer=(RelativeLayout) findViewById(R.id.crop_container);
        cropContainer.setOnTouchListener(new MyTouchListener(croopLoop, cropContainer));
    }

    private class MyTouchListener implements View.OnTouchListener{

        private RelativeLayout relativeLayout;

        private ImageView imageView;

        private RelativeLayout.LayoutParams containerParams;

        private int  maxLeft=0 ;

        private int  maxTop=0;

        private int xDif, yDif;

        private int xStart, yStart;

        public  MyTouchListener(RelativeLayout frameLayout, RelativeLayout cropContainer){
            this.relativeLayout=frameLayout;
            this.imageView= (ImageView) frameLayout.findViewById(R.id.crop_logo_img);
            containerParams= (RelativeLayout.LayoutParams) frameLayout.getLayoutParams();
            maxLeft=cropContainer.getWidth()-imageView.getWidth();
            maxTop=cropContainer.getHeight()-imageView.getHeight();
            Log.d(TAG, "MyTouchListener::() windowwidth="+maxLeft+" windowheight="+maxTop);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    xDif=(int)event.getX();
                    yDif=(int) event.getY();
                    xStart=containerParams.leftMargin;
                    yStart=containerParams.topMargin;
                    Log.d(TAG, "ACTION_DOWN xDif="+xDif+" yDif="+yDif+" xStart="+xStart+" yStart="+yStart);
                    break;

                case MotionEvent.ACTION_MOVE:
                    int x_cord = (int) event.getX()+xStart-xDif;
                    int y_cord = (int) event.getY()+yStart-yDif;

                    if (x_cord > maxLeft) {
                        x_cord = maxLeft;
                    }
                    if (x_cord<0){
                        x_cord=0;
                    }
                    if (y_cord<0){
                        y_cord=0;
                    }
                    if (y_cord > maxTop) {
                        y_cord = maxTop;
                    }

                    Log.d(TAG, "x_cord="+x_cord+" y_cord="+y_cord);
                    containerParams.leftMargin = x_cord ;
                    containerParams.topMargin = y_cord;
                    relativeLayout.setLayoutParams(containerParams);
                    imageView.scrollTo(x_cord, y_cord);
                    break;
                default:
                    break;
            };
            return true;
        }
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

        Button button= (Button) this.findViewById(R.id.choosePhotoBtn);
        button.setVisibility(View.VISIBLE);
    }

    public void cropImage(View view){
        RelativeLayout croopLoop=(RelativeLayout) findViewById(R.id.crop_loop);
        RelativeLayout.LayoutParams containerParams= (RelativeLayout.LayoutParams) croopLoop.getLayoutParams();
        ImageView croppedImg=(ImageView) findViewById(R.id.cropped_img);

        ImageView originalImage= (ImageView) findViewById(R.id.crop_logo_img);
        int left, top;
        left=containerParams.leftMargin;
        top=containerParams.topMargin;
        Bitmap originalBitmap= ((BitmapDrawable)originalImage.getDrawable()).getBitmap();
        Bitmap croppedBmp = Bitmap.createBitmap(originalBitmap, left, top, containerParams.width, containerParams.height);

        croppedImg.setImageBitmap(croppedBmp);
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
