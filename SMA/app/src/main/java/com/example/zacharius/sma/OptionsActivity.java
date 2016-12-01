package com.example.zacharius.sma;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by jakew on 10/29/2016.
 */

public class OptionsActivity extends AppCompatActivity {

    Spinner delete_rec;
    Spinner delete_send;
    ArrayAdapter<CharSequence> delete_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);
        delete_rec = (Spinner) findViewById(R.id.spinner);
        delete_send = (Spinner) findViewById(R.id.spinner);
        delete_adapter = ArrayAdapter.createFromResource(this, R.array.deleteOptions, android.R.layout.simple_spinner_item);
        delete_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delete_send.setAdapter(delete_adapter);
        delete_rec.setAdapter(delete_adapter);

        Button showDialog = (Button) findViewById(R.id.addContactButton);
        //TextView enterUserID = new (TextView) findViewById(R.id.contactAdd);

        showDialog.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                View view = (LayoutInflater.from(OptionsActivity.this)).inflate(R.layout.activity_addcontact, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OptionsActivity.this);
                alertBuilder.setView(view);
                final EditText addContact = (EditText) view.findViewById(R.id.enterUserID);
                alertBuilder.setCancelable(true).setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String contact = addContact.getText().toString();
                        Log.d("OptionsActivity", "Requesting to add contact " + contact );
                    }
                });
                Dialog dialog = alertBuilder.create();
                dialog.show();

            }


        });
    }

    public void onClickDelete(View v) {
        Intent intent = new Intent(getApplicationContext(), DeleteContactActivity.class);
        startActivity(intent);
    }


    /*

    New code...
     */



}
