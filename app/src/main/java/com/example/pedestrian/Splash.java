package com.example.pedestrian;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.widget.ImageView;

import com.example.pedestrian.utils.GlobalUtils;

import java.io.File;

public class Splash extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    Bitmap bmp;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_splash);

        try {
        /* adapt the image to the size of the display */
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            File file = new File("/sdcard/splashscreen.png");
            if (file != null) {
                bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), size.x, size.y, true);
            }

            //Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.splash_new), size.x, size.y,true);

        /* fill the background ImageView with the resized image */
            ImageView iv_background = (ImageView) findViewById(R.id.image);
            iv_background.setImageBitmap(bmp);
        }catch(Exception e){
            GlobalUtils.writeLogFile("Error loading splash screen " + e.getMessage());
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(Splash.this, MapsActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}