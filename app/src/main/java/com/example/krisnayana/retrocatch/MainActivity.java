package com.example.krisnayana.retrocatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Mencari referensi imageView di dalam folder layout dalam res*/
        ImageView ourFrame = (ImageView) findViewById(R.id.imageView);

        /*Mendeklarasikan bitmap*/
        Bitmap ourBitmap = Bitmap.createBitmap(300, 600, Bitmap.Config.ARGB_8888);
        /*Bitmap yang sebelumnya telah dibuat akan dijadikan canvas dengan
        * memasukkan parameter bitmap dalam new Canvas()*/
        Canvas ourCanvas = new Canvas(ourBitmap);

        Paint paint = new Paint();

        /*Mengganti warna canvas*/
        ourCanvas.drawColor(Color.BLACK);
        /*Menambahkan warna untuk paint
        * paint ini akan digunakan sebagai warna untuk setiap kali variabel paint ini dipanggil*/
        paint.setColor(Color.argb(255, 255, 255, 255));

        /*Membuat teks, dengan warna dari variabel paint*/
        ourCanvas.drawText("Score: ??, Lives: ?, HiScore: ??", 10, 10, paint);
        /*Membuat garis sederhana*/
        ourCanvas.drawLine(10, 50, 200, 50, paint);
        /*Membuat lingkaran*/
        ourCanvas.drawCircle(110, 160, 100, paint);
        /*Membuat drawPoint/sebuah titik*/
        ourCanvas.drawPoint(10, 260, paint);

        /*Memasang bitmap pada frame yang telah direferensikan di layout sebelumnya*/
        ourFrame.setImageBitmap(ourBitmap);
    }
}
