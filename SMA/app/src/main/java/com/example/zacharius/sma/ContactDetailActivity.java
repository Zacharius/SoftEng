package com.example.zacharius.sma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.zacharius.sma.Contact;


public class ContactDetailActivity extends AppCompatActivity
{
    TextView contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");

        Contact con = new Contact("this is the id", name);
        contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText(name);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, con.getMessage());
        ListView LV = (ListView)findViewById(R.id.contactMessages);
        LV.setAdapter(adapter);


    }
}
