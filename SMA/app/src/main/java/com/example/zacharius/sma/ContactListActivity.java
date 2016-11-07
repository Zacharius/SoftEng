package com.example.zacharius.sma;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    ListView contactsView;
    ArrayAdapter<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //grab Contacts ListView
        contactsView = (ListView) findViewById(R.id.contacts);

        //sample contancts
        final ArrayList<String> contactList = new ArrayList<String>();
        contactList.add("Zach");
        contactList.add("Elijah");
        contactList.add("Brad");
        contactList.add("Jake");
        contactList.add("Nick");
        contactList.add("Carlos");
        contactList.add("Enrique");
        contactList.add("Peju");
        contactList.add("Alex");
        contactList.add("Connor");

        //bind contactList to list, an ArrayAdapter
        list = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, contactList);

        //bind list(arrayadapter) to contactView
        contactsView.setAdapter(list);

        //determine what will happen when a user presses an item in the list
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("NAME", contactList.get(position));
                startActivity(intent);

            }
        });
    }

    public void onClickOptions(View v)
    {
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivity(intent);
    }





}
