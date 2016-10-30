package com.example.zacharius.sma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by jakew on 10/29/2016.
 */

public class OptionsActivity extends AppCompatActivity {

    Spinner delete_rec;
    Spinner delete_send;
    ArrayAdapter<CharSequence> delete_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        delete_rec = (Spinner)findViewById(R.id.spinner);
        delete_send = (Spinner)findViewById(R.id.spinner);
        delete_adapter = ArrayAdapter.createFromResource(this, R.array.deleteOptions, android.R.layout.simple_spinner_item);
        delete_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delete_send.setAdapter(delete_adapter);
        delete_rec.setAdapter(delete_adapter);

    }




}
