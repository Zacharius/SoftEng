package com.example.zacharius.sma;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.zacharius.sma.LoginActivity.context;

/**
 * Created by jakew on 11/30/2016.
 */

public class ResetPasswordActivity extends AppCompatActivity {
    private String newPassword;
    private String passwordConfirm;
    public ServerComm server;
    ServiceConnection connector = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            ServerComm.ServerBinder binder = (ServerComm.ServerBinder) iBinder;
            server = binder.getServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Log.d("ResetPasswordActivity", "Activity starting");



        Intent intent = new Intent(this, ServerComm.class);
        bindService(intent, connector, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(connector);
    }

    public void onClickResetPW(View view){

        context = getApplicationContext();

        EditText nPassword = (EditText)findViewById(R.id.newPassword);
        EditText cnPassword = (EditText)findViewById(R.id.confirmPassword);
        newPassword = nPassword.getText().toString();
        passwordConfirm = cnPassword.getText().toString();
        Pattern pattern = Pattern.compile("\\s");
        Pattern lowAlph = Pattern.compile("[a-z]+");
        Pattern upAlph = Pattern.compile("[A-Z]+");
        Pattern num = Pattern.compile("[0-9]+");
        Pattern invalidChar = Pattern.compile("[',/.<>\\[\\]:;`~+=\\\\()_\"{}|-]");
        Pattern symbol = Pattern.compile("[!@#$%^&*?]+");
        Matcher invalidMatch = invalidChar.matcher(newPassword);
        Matcher symbolMatch = symbol.matcher(newPassword);
        Matcher lowAlphaMatch = lowAlph.matcher(newPassword);
        Matcher upAlphaMatch = upAlph.matcher(newPassword);
        Matcher matcher = pattern.matcher(newPassword);
        Matcher numMatch = num.matcher(newPassword);
        boolean lowAlphaOK = lowAlphaMatch.find();
        boolean upAlphOK = upAlphaMatch.find();
        boolean numOK = numMatch.find();
        boolean found = matcher.find();
        boolean symbolOK = symbolMatch.find();
        boolean invalidOK = invalidMatch.find();



        if(newPassword.equals(passwordConfirm) &&
                newPassword.length() <= 32 &&
                newPassword.length() >= 8  &&
                 !found && numOK && lowAlphaOK && upAlphOK && symbolOK && !invalidOK){
            server.changePassword(newPassword);
            Intent i = new Intent(view.getContext(), ContactListActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(view.getContext(), "Password must be at least 8 characters and contain one Uppercase, one Lowercase, one Number and " +
                    "one of the following symbols !@#$%^&*?\n",
                    Toast.LENGTH_SHORT).show();
            nPassword.setText("");
            cnPassword.setText("");

        }
        
    }
}
