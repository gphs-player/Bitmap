package com.leo.image;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.leo.bitmap.BitmapActivity;
import com.leo.glide.GlideTestActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToGlide(View view) {
        Intent intent = new Intent(this, GlideTestActivity.class);
        startActivity(intent);
    }

    public void goToBitmap(View view) {
        Intent intent = new Intent(this, BitmapActivity.class);
        startActivity(intent);
    }
}
