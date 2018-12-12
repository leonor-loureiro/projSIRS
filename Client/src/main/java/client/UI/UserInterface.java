package client.UI;

import client.CommandExecution;
import client.exception.BadArgument;
import client.exception.InvalidUser;
import client.exception.TokenInvalid;
import client.exception.UserAlreadyExists;
import client.security.Login;
import crypto.exception.CryptoException;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.Console;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.EnumSet;
import java.util.Scanner;


/**
 * User interface class
 * Simple implementation of a console-like app
 * Tries to safely retrieve password, however due to some limitations
 * not all shells support java.io.Console making passwords difficult to obscure
 *
 * Must ensure user is logged in before using operations that require login
 */
public interface UserInterface {

    /**
     * reads input in a safe way, doesn't work with all consoles/terminals
     */
    Console input = System.console();

    /**
     * not as safe input reader but works with all consoles/terminals
     */
    Scanner scan = new Scanner(System.in);

    /**
     *
     */
    CommandExecution commandExec = new CommandExecution();

    /**
     * Starting message upon launch of the program
     */
    static void home(){
        System.out.println("   _____                          _____                _____                                           \n" +
                "  / ____|                        |  __ \\              |  __ \\               /\\                         \n" +
                " | (___   ___  ___ _   _ _ __ ___| |__) |___ _ __ ___ | |  | | ___   ___   /  \\   ___ ___ ___  ___ ___ \n" +
                "  \\___ \\ / _ \\/ __| | | | '__/ _ \\  _  // _ \\ '_ ` _ \\| |  | |/ _ \\ / __| / /\\ \\ / __/ __/ _ \\/ __/ __|\n" +
                "  ____) |  __/ (__| |_| | | |  __/ | \\ \\  __/ | | | | | |__| | (_) | (__ / ____ \\ (_| (_|  __/\\__ \\__ \\\n" +
                " |_____/ \\___|\\___|\\__,_|_|  \\___|_|  \\_\\___|_| |_| |_|_____/ \\___/ \\___/_/    \\_\\___\\___\\___||___/___/\n" +
                "                                                                                                       \n" +
                "                                                                                                       "
                        .replace("\\", "\\\\"));
    }

    /**
     * Requests the user to input login info
     * @return array with username[0] and password [1]
     */
    static Login requestLogin() throws InvalidUser, BadArgument, UserAlreadyExists {
        Login login = new Login();

        System.out.println();
        System.out.println("login || register:");
        String loginOrRegister = requestInput();

        if(!(loginOrRegister.equals("login") || loginOrRegister.equals("register")))
            return null;


        System.out.println();
        System.out.println("Please insert your information:");

        System.out.println("Username: ");

        String username = requestInput();

        if (!username.matches("[a-zA-Z0-9]*")) {
            System.out.println("A username can only contain letters and numbers");
            return null;
        }
        login.setUsername(username);



        System.out.println("Password: ");
        login.setPassword(requestSensibleInput());


        System.out.println();

        if(loginOrRegister.equals("login"))
            commandExec.login(login);
        else{
            if(login.getPassword().length < 6){
                System.out.println("Please use a password with 6 or more characters");
                return null;
            }
            commandExec.register(login);
        }

        return login;
    }

    /**
     * Welcoming message to given user
     * @param username to include in message
     */
    static void welcome(String username){
        System.out.println("*************************************************************************************");
        System.out.println();
        System.out.println("\tWelcome "+ username + " to a super secure remote Doc access");
        System.out.println();
        System.out.println("*************************************************************************************");
    }



    /**
     * Lists all available commands
     */
    static void listCommands(){
        System.out.println();
        System.out.print("Available commands: ");

        System.out.print("  ");
        EnumSet.allOf(Command.class)
                .forEach(command -> System.out.print(" " + command));

        System.out.println();
    }


    /**
     * Parses input from user and associates right command
     */
    static boolean parseCommand(){
        String command;
        String fileName;
        String user;
        System.out.println("Insert command:");

        command = scan.next();
        Command c;
        try{
            c = Command.valueOf(command);
            switch(c){

                case pull:
                    commandExec.pull();
                    break;

                case push:
                    commandExec.push();
                    break;

                case add:
                    System.out.println("Insert name of the file you want to add:");
                    fileName = requestInput();
                    commandExec.add(fileName);
                    break;

                case share:
                    System.out.println("Insert filename:");
                    fileName = requestInput();
                    System.out.println("Share file with user:");
                    user = requestInput();
                    commandExec.share(user, fileName);
                    break;

                case list:
                    commandExec.list();
                    break;
                case getbackup:
                    System.out.println("Insert filename you want to get backup for");
                    fileName = requestInput();
                    commandExec.getBackup(fileName);
                    break;

                case help:
                    help();
                    break;

                case exit:
                    commandExec.exit();
                    return false;

                default:
                    System.out.println();
                    System.out.println("Unknown command");
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown Command!");
        } catch (BadArgument | TokenInvalid e) {
            System.out.println(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println();
        return true;
    }



    /**
     * Command help info
     */
    static void help(){
        System.out.println();
        System.out.println("Command description");
        System.out.println(Command.add + "   - add new file to remote server");
        System.out.println(Command.pull + "  - get files from remote server");
        System.out.println(Command.push + "  - send file to remote server");
        System.out.println(Command.share + " - share file with another user");
        System.out.println(Command.getbackup + "  - replaces file with previous remote version");
        System.out.println(Command.list + "  - lists all files");
        System.out.println(Command.exit + "  - exit program");
        System.out.println();
    }


    /**
     * request the user for input
     * attempts to use advanced input reading line, if not possible in current terminal
     * uses the scanner instead
     * @return string read input
     */
    static String requestInput(){
        String result;
        try{
            result = input.readLine();
        }catch(NullPointerException np){
            result = scan.next();
        }
        return result;
    }

    /**
     * request the user for input
     * attempts to use advanced input reading line, if not possible in current console uses the scanner instead
     * Stores in char[] instead of string due to java's unsafe string storage handling
     * @return char[] readinput
     */
    static char[] requestSensibleInput(){
        char[] result;
        try{
            result = input.readPassword();
        }catch(NullPointerException np){
            result = scan.next().toCharArray();
        }
        return result;
    }

    /**
     * fills the screen with blank spaces
     */
    static void clearScreen(){
        for(int i = 0; i < 2; i++)
            System.out.println();
    }

    static boolean validateInputNames(String input){
        return false;
    }

}
