package com.example.zacharius.sma;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity
{
    private final int MAX_LOGIN_ATTEMPTS = 5; //max number of times a user can attempt to login
    private final int LOGOUT_TIME = 5*60;  //number of seconds user is logged out of system after exceeding logout timer

    private int loginAttempts; //number of times user has tried to login
    private static boolean logIn;//tells us whether user is currently able to attempt login
    private static int secondsLeft_logout;//how many more seconds will user be logged out

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginAttempts = 0;
        logIn = true;
    }


    //executed when user presses SMA button on Login Page
    public void onEnterCredentials(View v)
    {

        //grab EditTexts from Login Page
        EditText idView =  (EditText) findViewById(R.id.ID);
        EditText passwordView = (EditText) findViewById(R.id.password);

        //grab user entered id and password
        String id = idView.getText().toString();
        String password = passwordView.getText().toString();

        //clear fields in both EditTexts
        idView.setText("");
        passwordView.setText("");

        //check if user is able to attempt login
        if(logIn)
        {

            //will eventually check user's entered credentials.
            //currently just returns true
            if(ServerComm.checkCredentials(id, password))
            {
                //if pass credential check, go to contact list page
                Intent i = new Intent(v.getContext(), ContactListActivity.class);
                startActivity(i);

            }
            //if fail credential check
            //increments loginAttempt, and checks whether user can attempt login again
            //if not, prevent user from logging in for set amount of time.
            else if(++loginAttempts > MAX_LOGIN_ATTEMPTS)
            {
                logIn = false;
                secondsLeft_logout = LOGOUT_TIME;
                (new Thread(new LogoutTimer())).start();
                Toast.makeText(v.getContext(),
                        "Invalid login, you have exceeded max login attempt limit and will be logged out for: " + (LOGOUT_TIME/60) + " minutes",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            //ask user to try login attempt again
            else
            {
            Toast.makeText(v.getContext(),
                    "Invalid login, try again\n Attempts Left: " + (loginAttempts - MAX_LOGIN_ATTEMPTS),
                    Toast.LENGTH_SHORT)
                    .show();
            }
        }
        //tell user how long it is till he can login again
        else
        {
            Toast.makeText(v.getContext(),
                    "You are currently suspended from system\n Seconds Left : " + secondsLeft_logout ,
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }

    //allow timer to check seconds left
    public static int getSecondsLeft()
    {
        return secondsLeft_logout;
    }

    //allow timer to change seconds left
    public static void setSecondsLeft(int secondsLeft)
    {
        secondsLeft_logout = secondsLeft;
    }

    //allow timer to change logIn
    public static void setLogin(boolean login)
    {
        LoginActivity.logIn = login;
    }







}
