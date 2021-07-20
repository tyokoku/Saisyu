package com.example.tyokoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.ar.core.examples.java.helloar.HelloArActivity;
import com.google.ar.core.examples.java.helloar.R;


public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private boolean buttonTap = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*各ボタンの定義*/
        Button Setting = findViewById(R.id.setting_button);
        Button Stamp = findViewById(R.id.button2);
        Button Ar = findViewById(R.id.button);

        /*テスト用　削除！*/
        textView = findViewById(R.id.text_view);

        /*設定画面への遷移*/
        Setting.setOnClickListener( v -> {
            // flagがtrueの時
            if (buttonTap) {
                textView.setText("Hello");
                buttonTap = false;
            }
            // flagがfalseの時
            else {
                textView.setText("World");
                buttonTap = true;
            }
        });

        /*スタンプラリー画面への遷移*/
        Stamp.setOnClickListener( v -> {
            Intent intent = new Intent(getApplication(), StampActivity.class);
            startActivity(intent);
        });

        /*AR画面への遷移*/
        Ar.setOnClickListener( v -> {
            Intent intent = new Intent(getApplication(), com.google.ar.core.examples.java.helloar.HelloArActivity.class);
            startActivity(intent);
        });

    }
}