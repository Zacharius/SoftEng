package com.example.zacharius.sma;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity
{
    private final int MAX_LOGIN_ATTEMPTS = 5; //max number of times a user can attempt to login
    private final int LOGOUT_TIME = 5*60;  //number of seconds user is logged out of system after exceeding logout timer

    private int loginAttempts; //number of times user has tried to login
    private static boolean logIn;//tells us whether user is currently able to attempt login
    private static int secondsLeft_logout;//how many more seconds will user be logged out

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public static Context context;

    private ServerComm server;

    public  static volatile int credentials = 0;//0 - waiting for auth response
                               //1 - auth came back true
                               //2 - auth came back false
    public static String errMsg;

    private ServiceConnection connector = new ServiceConnection()
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        context = getApplicationContext();

        loginAttempts = 0;
        logIn = true;

        Intent intent = new Intent(this, ServerComm.class);
        intent.putExtra("address", "198.27.65.177");
        intent.putExtra("port", 4269);
        startService(intent);
        bindService(intent, connector, Context.BIND_AUTO_CREATE);

        /*server = new ServerComm("198.27.65.177", 4269);
        Intent serverListener = new Intent(getApplicationContext(), ServerComm.ServerListener.class);
        getApplicationContext().startService(serverListener);*/
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

        Log.d("Login activity", "beggining login action");

        Login login = new Login();
        login.execute(id, password);


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

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class Login extends AsyncTask<String, Void, Integer>
    {


        String loginMsg;

        private Login(){}

        @Override
        protected Integer doInBackground(String... strings)
        {
            String id = strings[0];
            String password = strings[1];
            
            //ensure we are connected to server
            if(server.getServer() != null)
            {
                //ensure user is not currently locked out
                if(logIn){

                    Log.d("login", "checking credentials");
                    server.checkCredentials(id, password);

                    //wait for response from server
                    while(credentials == 0)
                    {

                    };

                    Log.d("Login", "Credentials returned " + credentials);

                    if(credentials == 1)
                    {
                        Log.d("Login", "login passed");

                        //check if keys have been generated yet
                        helper = new DatabaseHelper(getApplicationContext());
                        db = helper.getReadableDatabase();
                        Cursor cursor = db.query(true,
                                DatabaseContract.ContactTable.TABLE_NAME,
                                new String[]{DatabaseContract.ContactTable.COLUMN_KEY},
                                null,null,null, null, null, null);

                        //generate keys because they havent been generated yet
                        if(!cursor.moveToFirst())
                        {
                            Log.d("Login", "generating intial keys");

                            KeyPair keyPair = Crypto.keygen();

                            PublicKey pub = keyPair.getPublic();
                            PrivateKey pri = keyPair.getPrivate();

                            String pubString = Crypto.publicKeyToString(pub);
                            String priString = Crypto.privateKeyToString(pri);

                            //write keys to local database as strings
                            db.close();
                            db = helper.getWritableDatabase();

                            ContentValues value = new ContentValues();
                            value.put(DatabaseContract.ContactTable.COLUMN_USERID, "USER_PUB");
                            value.put(DatabaseContract.ContactTable.COLUMN_KEY, pubString);
                            if(db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, value) == -1)
                            {
                                Log.d("Login", "Trouble inserting into database");
                            }


                            value = new ContentValues();
                            value.put(DatabaseContract.ContactTable.COLUMN_USERID, "USER_PRI");
                            value.put(DatabaseContract.ContactTable.COLUMN_KEY, priString);
                            if(db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, value) == -1)
                            {
                                Log.d("Login", "Trouble inserting into database");
                            }

                            db.close();

                            //give public key to server
                            server.pushPublicKey(pubString);

                            return 2;

                        }
                        else
                        {
                            return 1;
                        }




                    } else if(++loginAttempts > MAX_LOGIN_ATTEMPTS)
                    {
                        logIn = false;
                        secondsLeft_logout = LOGOUT_TIME;
                        (new Thread(new LogoutTimer())).start();
                        loginMsg = "Invalid login, you have exceeded max login attempt limit and will be logged out for: " + (LOGOUT_TIME/60) + " minutes";
                    }
                    //ask user to try login attempt again
                    else
                    {
                        loginMsg = "Invalid login, try again\n Attempts Left: " + (MAX_LOGIN_ATTEMPTS - loginAttempts );
                        publishProgress();
                    }
                }
                //tell user how long it is till he can login again
                else
                {
                    loginMsg = "You are currently suspended from system\n Seconds Left : " + secondsLeft_logout;
                    publishProgress();
                }
            }
            else
            {
                loginMsg = "can't login; not connected to server";
                publishProgress();
            }


            credentials = 0;
            return -1;

        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);
            Log.d("Login", loginMsg);
            if(LoginActivity.context != null)
            {
                Toast.makeText(context,
                        loginMsg ,
                        Toast.LENGTH_SHORT)
                        .show();
            }

        }

        @Override
        /*parameter meaning:
                -1: login fail
                 1: login pass, go to ContactListActivity
                 2: login pass, go to PasswordResetActivity*/
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);

            Log.d("login", "doInBackground returned " + result);

            if(result == 1)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent i = new Intent(context, ContactListActivity.class);
                        startActivity(i);
                    }
                });

            }
            else if(result == 2)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent i = new Intent(context, ResetPasswordActivity.class);
                        startActivity(i);
                    }
                });
            }

        }
    }
}








