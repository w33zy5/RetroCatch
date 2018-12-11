package com.example.krisnayana.retrocatch;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //region Deklarasi
    Canvas canvas;
    SquashCourtView squashCourtView;

    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    Display display;
    Point size;
    int screenWidth;
    int screenHeight;

    //Deklarasi objek permainan untuk menangkap bola
    int racketWidth;
    int racketHeight;
    Point racketPosition;

    /*Deklarasi bola untuk permainan*/
    Point ballPosition;
    int ballWidth;

    /*Deklarasi variabel pergerakan bola*/
    boolean ballIsMovingDown;
    boolean ballIsMovingUp;
    boolean ballIsMovingLeft;
    boolean ballIsMovingRight;

    /*Deklarasi pergerakan variabel penangkap*/
    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    long lastFrameTime;
    int fps, score, lives;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Pembuatan variabel untuk memanggil kelas SuqashCourtView untuk memulai game*/
        squashCourtView = new SquashCourtView(this);
        /*Memasang view untuk variabel yang mengandung kelas SquashCourtView*/
        setContentView(squashCourtView);
        /*Memanggil kelas suara*/
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        try {
            //Create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            //create our three fx in memory ready for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Menyimpan variabel untuk mengatur ukuran layar*/
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        /*Pembuatan pemain*/
        racketPosition = new Point();
        racketPosition.x = screenWidth / 2;
        racketPosition.y = screenHeight - 20;
        racketWidth = screenWidth / 8;
        racketHeight = 10;

        ballWidth = screenWidth / 35;
        ballPosition = new Point();
        ballPosition.x = screenWidth/2;
        ballPosition.y = 1 + ballWidth;

        lives = 3;
    }

    class SquashCourtView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSquash;
        Paint paint;

        public SquashCourtView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            ballIsMovingDown = true;

            Random randomNumber = new Random();
            int ballDirection = randomNumber.nextInt(3);
            switch (ballDirection){
                case 0:
                    ballIsMovingLeft = true;
                    ballIsMovingRight = false;
                    break;
                case 1:
                    ballIsMovingRight = true;
                    ballIsMovingLeft = false;
                    break;
                case 2:
                    ballIsMovingLeft = false;
                    ballIsMovingRight = false;
                    break;
            }

        }

        @Override
        public void run() {
            while(playingSquash){
                updateCourt();
                drawCourt();
                controlFPS();
            }
        }

        public void pause(){
            playingSquash = false;
            try{
                ourThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void resume(){
            playingSquash = true;
            ourThread = new Thread(this);
            ourThread.start();
        }

        private void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 15 - timeThisFrame;
            if(timeThisFrame > 0){
                fps = (int)(1000/timeThisFrame);
            }
            if(timeToSleep > 0){
                try{
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            lastFrameTime = System.currentTimeMillis();
            //git push
        }

        private void drawCourt() {
            if(ourHolder.getSurface().isValid()){
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255,255,255,255));

                paint.setTextSize(45);
                canvas.drawText("Score: " + score + ", Lives: " + lives + ", fps: " + fps, 20, 40, paint);

                canvas.drawRect(racketPosition.x - (racketWidth/2), racketPosition.y - (racketHeight/2), racketPosition.x + (racketWidth/2), racketPosition.y + racketHeight, paint);
                Log.e("racketposition.y, ", String.valueOf(racketPosition.y));

                canvas.drawRect(ballPosition.x, ballPosition.y, ballPosition.x + ballWidth, ballPosition.y + ballWidth, paint);

                ourHolder.unlockCanvasAndPost(canvas);
                //Test
            }
        }

        private void updateCourt() {
            if(racketIsMovingRight){
                racketPosition.x = racketPosition.x + 10;
            }
            if(racketIsMovingLeft){
                racketPosition.x = racketPosition.x - 10;
            }

            /*Apabila terkena dinding di bagian kanan*/
            if(ballPosition.x + ballWidth > screenWidth){
                ballIsMovingLeft = true;
                ballIsMovingRight = false;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }
            /*Apabila terkena dinding di bagian kiri*/
            if(ballPosition.x < 10){
                ballIsMovingLeft = false;
                ballIsMovingRight = true;
                soundPool.play(sample1, 1, 1, 0, 0, 1);
            }

            if(ballPosition.y > screenHeight - ballWidth){
                lives = lives - 1;
                if(lives == 0){
                    lives = 3;
                    score = 0;
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                }
                ballPosition.y = 1 + ballWidth;

                Random randomNumber = new Random();
                int startX = randomNumber.nextInt(screenWidth - ballWidth) + 1;
                ballPosition.x = startX + ballWidth;

                int ballDirection = randomNumber.nextInt(3);
                switch (ballDirection){
                    case 0:
                        ballIsMovingLeft = true;
                        ballIsMovingRight = false;
                        break;
                    case 1:
                        ballIsMovingLeft = false;
                        ballIsMovingRight = true;
                        break;
                    case 2:
                        ballIsMovingLeft = false;
                        ballIsMovingRight = false;
                }
            }
            if(ballPosition.y <= 0){
                ballIsMovingDown = true;
                ballIsMovingUp = false;
                ballPosition.y = 1;
                soundPool.play(sample2, 1,1,0,0,1);
            }

            if(ballIsMovingDown){
                ballPosition.y += 6;
            }

            if(ballIsMovingUp){
                ballPosition.y -= 10;
            }

            if(ballIsMovingLeft){
                ballPosition.x -= 12;
            }

            if(ballIsMovingRight){
                ballPosition.x += 12;
            }

            if(ballPosition.y + ballWidth >= (racketPosition.y - racketHeight/2)){
                int halfRacket = racketHeight/2;
                if(ballPosition.x + ballWidth > (racketPosition.x - halfRacket) && ballPosition.x - ballWidth < (racketPosition.x + halfRacket)){
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    score++;
                    ballIsMovingUp = true;
                    ballIsMovingDown = false;
                    if(ballPosition.x > racketPosition.x){
                        ballIsMovingRight = true;
                        ballIsMovingLeft = false;
                    }else {
                        ballIsMovingRight = false;
                        ballIsMovingLeft = true;
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent){
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    if(motionEvent.getX() >= screenWidth/2){
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    }else{
                        racketIsMovingLeft = true;
                        racketIsMovingRight = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    racketIsMovingRight = false;
                    racketIsMovingLeft = false;
                    break;
            }
            return true;
        }
    }

    @Override
    protected  void onStop(){
        super.onStop();

        while(true){
            squashCourtView.pause();
            break;
        }

        finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        squashCourtView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        squashCourtView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            squashCourtView.pause();
            finish();
            return true;
        }
        return false;
    }

}
