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
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends Activity {
    public static final String TAG=MainActivity.class.getSimpleName();

    private static final int SELECT_PHOTO = 100;

    private FrameLayout mCroppingContainer;

    private FrameLayout mCropSelectionContainer;

    private ImageView mBackgroundPhoto;

    private ImageView mCropSelection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mBackgroundPhoto= (ImageView) this.findViewById(R.id.background_photo);
        mCropSelection=(ImageView) findViewById(R.id.crop_selection);



        mCroppingContainer= (FrameLayout) findViewById(R.id.cropping_container);
        mCropSelectionContainer=(FrameLayout) findViewById(R.id.crop_selection_container);

        mCroppingContainer.setOnTouchListener(new MyTouchListener(mCropSelectionContainer, mCropSelection, mCroppingContainer));
    }

    private class MyTouchListener implements View.OnTouchListener{

        private FrameLayout mCropSelectionContainer;

        private ImageView mCropSelection;

        private FrameLayout.LayoutParams containerParams;

        private int  maxLeft=0 ;

        private int  maxTop=0;

        private int xDif, yDif;

        private int xStart, yStart;

        public  MyTouchListener(FrameLayout mCropSelectionContainer, ImageView mCropSelection, FrameLayout mCroppingContainer){
            this.mCropSelectionContainer=mCropSelectionContainer;
            this.mCropSelection=mCropSelection;
            containerParams= (FrameLayout.LayoutParams) mCropSelectionContainer.getLayoutParams();
            maxLeft=mCroppingContainer.getWidth()-mCropSelection.getWidth();
            maxTop=mCroppingContainer.getHeight()-mCropSelection.getHeight();
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
                    mCropSelectionContainer.setLayoutParams(containerParams);
                    mCropSelection.scrollTo(x_cord, y_cord);
                    break;
                default:
                    break;
            };
            return true;
        }
    }


    public void choosePhoto(View view){
        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);*/

        String files[]=Environment.getExternalStorageDirectory().list();
        Log.d("Goz", "files="+files);
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/Download/desert.jpg");
        changePhoto(bitmap);

    }

    private void changePhoto(Bitmap bitmap){


        mBackgroundPhoto.setImageBitmap(bitmap);

        ViewTreeObserver vto = mBackgroundPhoto.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                mBackgroundPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                int  finalHeight = mBackgroundPhoto.getMeasuredHeight();
                int finalWidth = mBackgroundPhoto.getMeasuredWidth();
                Log.d(TAG, "onPreDraw= finalHeight="+finalHeight+" finalWidth="+finalWidth);
                Bitmap bitmap =((BitmapDrawable) mBackgroundPhoto.getDrawable()).getBitmap();
               Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
                mCropSelection.setImageBitmap(scaledBitmap);
                mCroppingContainer.setOnTouchListener(new MyTouchListener(mCropSelectionContainer, mCropSelection, mCroppingContainer));
                return true;
            }
        });

    }

    public void cropImage(View view){

        FrameLayout.LayoutParams containerParams= (FrameLayout.LayoutParams) mCropSelectionContainer.getLayoutParams();
        ImageView croppedImg=(ImageView) findViewById(R.id.cropped_img);

        int left, top;
        left=containerParams.leftMargin;
        top=containerParams.topMargin;
        Bitmap originalBitmap= ((BitmapDrawable)mCropSelection.getDrawable()).getBitmap();
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
                        changePhoto(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
}
