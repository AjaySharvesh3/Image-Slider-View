package com.wolfpack.ajays.librarytest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wolfpack.ajays.slider.Slider;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Slider slider;
    private Button next, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slider = findViewById(R.id.slider);

        int [] id = new int [] {R.drawable.dogone, R.drawable.dogtwo,  R.drawable.dogthree};

        slider.setImageResource(id);

        slider.setSlideListener(new Slider.SlideListener() {
            @Override
            public void onReachedFirst() {
                Toast.makeText(MainActivity.this,"Reached 1st Position!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReachedLast() {
                Toast.makeText(MainActivity.this,"Reached last Position!", Toast.LENGTH_LONG).show();
            }

        });

        next = findViewById(R.id.next);
        previous = findViewById(R.id.back);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.slideLeft();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slider.slideRight();
            }
        });
    }
}
