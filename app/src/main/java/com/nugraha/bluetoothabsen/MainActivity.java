package com.nugraha.bluetoothabsen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    DataAbsenAdapter helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DataAbsenAdapter(this);
        helper.generateDataDummy();
    }

    public void viewdata(View view) {
        String data = helper.getData();
        Message.message(this,data);
    }

    public void viewabsen(View view) {

    }
}
