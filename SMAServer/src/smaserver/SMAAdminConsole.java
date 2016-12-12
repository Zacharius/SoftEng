package smaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

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
        switch(arguments[0]) {
            case "/help":
                printHelp();
                break;
            case "/adduser":
                if (arguments.length != 2) {
                    // printAddUserUsage();
                } else {
                    String passsword = generatePassword();
                    if (DBAccess.addUser(arguments[1], passsword)) {
                        System.out.println("A new user profile for " + arguments[1] + " was successfully created.");
                        System.out.println("Password: " + passsword);
                    } else {
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
            case "/resetpassword":
                if (arguments.length != 2) {
                    // printChangePasswordUsage();
                } else {
                    String password = generatePassword();
                    if (DBAccess.changePassword(arguments[1], password)) {
                        System.out.println("Password for " + arguments[1] + " updated successfully.");
                        System.out.println("Password: " + password);
                    } else {
                        System.out.println("Could not add user.");
                    }
                }
                break;
            case "/randompassword":
                System.out.println("Here's a random password: " + generatePassword() + "\n");
                break;
            case "/randompasswords":
                if (arguments.length != 2) {
                    // printRandompasswordsUsage();
                } else {
                    int count = Integer.parseInt(arguments[1]);
                    if (count <= 0) {
                        System.out.println("The number of passwords to print must be a positive integer greater than " +
                                "zero.");
                    } else {
                        System.out.println("Here's " + count + " random passwords.");
                        for (int c = 0; c < count; c++) {
                            System.out.println(generatePassword());
                        }
                        System.out.println();
                    }
                }
                break;
            default:
                System.out.println("Invalid console input. Please input a valid command or type /help for more " +
                        "information.\n");
        }
    }

    /**
     * Generate a random password of at least length 10 but not exceeding 32 containing at least one each of an
     * uppercase letter, lowercase letter,
     * and digit.
     * @return a String value containing the new password
     */
    private String generatePassword(){
        int random_length = ThreadLocalRandom.current().nextInt(10, 33);
        StringBuilder pwd = new StringBuilder(random_length);

        boolean contains_upper, contains_lower, contains_digit;
        contains_digit = contains_lower = contains_upper = false;

        int rand;
        for(int length = 0; length < random_length; length++) {
            if (length >= random_length - 2 && (!contains_upper || !contains_lower || !contains_digit)) {
                if (!contains_upper) {
                    contains_upper = true;
                    pwd.append((char) (ThreadLocalRandom.current().nextInt(1, 27) + 64));
                } else if (!contains_lower) {
                    contains_lower = true;
                    pwd.append((char) (ThreadLocalRandom.current().nextInt(1, 27) + 96));
                } else if (!contains_digit) {
                    contains_digit = true;
                    pwd.append((char) (ThreadLocalRandom.current().nextInt(0, 10) + 48));
                }
            } else {
                switch (ThreadLocalRandom.current().nextInt(1, 4)) {
                    case 1:
                        contains_digit = true;
                        pwd.append((char) (ThreadLocalRandom.current().nextInt(0, 10) + 48));
                        break;
                    case 2:
                        contains_upper = true;
                        pwd.append((char) (ThreadLocalRandom.current().nextInt(1, 27) + 64));
                        break;
                    case 3:
                        contains_lower = true;
                        pwd.append((char) (ThreadLocalRandom.current().nextInt(1, 27) + 96));
                        break;
                    default:
                }
            }
        }

        return pwd.toString();
    }


    /**
     * Prints all the help info for admin console.
     */
    private void printHelp(){
        System.out.println("\nSMA Commands:\n" +
                           "    /help                       shows a list of commands and their usage\n" +
                           "    /adduser [user ID]          adds a user with the ID specified\n" +
                           "    /shutdown                   gracefully shut down the server\n" +
                           "    /togglelog                  turn console logging on and off\n" +
                           "    /resetpassword [user ID]    change the password for the given user\n" +
                           "    /randompassword             shows a random password\n" +
                           "    /randompasswords [number]   prints [number] random passwords each on their own line");
    }
}
