package com.example.tyokoku;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.examples.java.helloar.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class StampActivity extends AppCompatActivity {
    static List<String> items = new ArrayList<String>();
    static ArrayAdapter<String> adapter;
    static MyDbHelper dbAdapter;


    ListView listView1;
    ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);

        /*ホーム画面に戻る*/
        Button returnButton = findViewById(R.id.returnbutton);
        returnButton.setOnClickListener(v -> finish());


        listView1 = (ListView) findViewById(R.id.listview1);

        //db
        MyDbHelper mDbHelper = new MyDbHelper(this);
        try {
            mDbHelper.createEmptyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //select
        Cursor c = db.rawQuery("select * from sculputure", null);

        //adapterの準備
        //表示するカラム名
        String[] from = {"_id", "name"};
        //バインドするViewリソース
        //int[] to = {android.R.id.text1, android.R.id.text2};

        //adapter生成
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, from);

        listView1.setAdapter(adapter);

    }
}




