package smaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by elijah on 12/1/2016.
 *
 * The SMAAdminConsole class is responsible for handling all administrator commands. It is responsible for stopping the
 * server and is the only way User records should be added or modified other than via client connections.
 *
 * Commands added to this file should be in the form
 *
 * /command arg1 arg2 arg3....
 *
 * These should be added as case statements in the processCommand() method. Their proper usage should be documented
 * in their own printCommandUsage() method where `Command` is replaced with the command to be added. They should
 * also be documented briefly in the printHelp() method.
 */
 class SMAAdminConsole implements Runnable{
    SMAAdminConsole(){};

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                processCommand(in.readLine());
            }
        }catch(IOException e){
            //
        }
        // We now have a runaway server. It's time to do an emergency shutdown.
        System.exit(1);
    }

    /**
     * Takes a String representing the most recent console input, parses the command, and
     * performs the requested action.
     */
    private void processCommand(String input){
        String[] arguments = input.split("\\s+");
        switch(arguments[0]){
            case "/help":
                printHelp();
                break;
            case "/adduser":
                if(arguments.length != 3){
                    // printAddUserUsage();
                }else{
                    if(DBAccess.addUser(arguments[1], arguments[2])){
                        System.out.println("A new user profile for " + arguments[1] + " was successfully created.");
                    }else{
                        System.out.println("Could not create a new user profile.");
                    }
                }
                break;
            case "/togglelog":
                // This is where code should go to turn console logging on and off.
                break;
            case "/shutdown":
                System.out.println("System is shutting down.");
                System.exit(0);
                break;
            case "/changepassword":
                if(arguments.length != 3){
                    // printChangePasswordUsage();
                }else{
                    if(DBAccess.changePassword(arguments[1], arguments[2])){
                        System.out.println("Password for " + arguments[1] + " updated successfully.");
                    }else{
                        System.out.println("Could not add user.");
                    }
                }
                break;
            default:
                System.out.println("Hello admin");
        }
    }

    /**
     * Prints all the help info for admin console.
     */
    private void printHelp(){
        System.out.println("\nSMA Commands:\n" +
                           "    /help                                       shows a list of commands and their usage\n" +
                           "    /adduser [user ID] [password]               adds a user with the ide and password specified\n" +
                           "    /shutdown                                   gracefully shut down the server\n" +
                           "    /togglelog                                  turn console logging on and off\n" +
                           "    /changepassword [user ID] [new password]    change the password for the given user\n");
    }
}
