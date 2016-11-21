package com.example.zacharius.sma;

import java.util.concurrent.TimeUnit;

/**
 * Created by zacharius on 10/23/16.
 */


//wait for the number of seconds specified in LoginActivity.LOGOUT_TIME
public class LogoutTimer implements Runnable
{


    public void run()
    {
        long startTime = System.currentTimeMillis();

        do
        {
            //wait 5 seconds
            try
            {
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
            }

            //check time
            long currentTime = System.currentTimeMillis();

            //see how much time is left
            LoginActivity.setSecondsLeft((int) (startTime - currentTime)/1000);
        }while(LoginActivity.getSecondsLeft() <= 0);

        LoginActivity.setLogin(true);
    }
}
