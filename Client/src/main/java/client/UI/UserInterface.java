package client.UI;

import client.security.Login;

import java.io.Console;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;


/**
 * User interface class
 * Simple implementation of a console-like app
 * Tries to safely retrieve password, however due to some limitations
 * not all shells support java.io.Console making passwords difficult to obscure
 */
public interface UserInterface {

    Console input = System.console();
    Scanner scan = new Scanner(System.in);

    /**
     * Requests the user to input login info
     * @return array with username[0] and password [1]
     */
    static Login requestLogin(){
        Login login = new Login();

        System.out.println();
        System.out.println("Please insert login information:");
        System.out.println("Username: ");

        try{
            login.setUsername( input.readLine());
        }catch(NullPointerException np){
            login.setUsername(scan.next());
        }

        System.out.println("Password: ");

        try{
            login.setPassword(input.readPassword());
        }catch(NullPointerException np){
            login.setPassword(scan.next().toCharArray());
        }

        System.out.println();

        return login;
    }

    static void welcome(String username){
        System.out.println("********************************************************");
        System.out.println();
        System.out.println("Welcome "+ username + " to a super secure remote Doc access");
        System.out.println();
        System.out.println("********************************************************");
    }

    static void listCommands(){
        System.out.println();
        System.out.println("Available commands:");

        EnumSet.allOf(Command.class)
                .forEach(command -> System.out.println("\t" + command));

        System.out.println();
    }

    static void help(){
        System.out.println();
        System.out.println("Command description");
        System.out.println("add   - add new file to remote server");
        System.out.println("pull  - get files from remote server");
        System.out.println("push  - send file to remote server");
        System.out.println("share - share file with another user");
        System.out.println("list  - lists current tracked files");
        System.out.println("exit  - exit program");
        System.out.println();
    }

    static void parseCommand(){
        String command;

        System.out.println();
        System.out.println("Insert command:");

        command = scan.next();

        switch(command){

            case("pull"):
                CommandExecution.pull();
                break;

            case ("push"):
                CommandExecution.push();
                break;

            case("add"):
                CommandExecution.add();
                break;

            case ("share"):
                CommandExecution.share();
                break;

            case ("list"):
                CommandExecution.list();
                break;

            case ("help"):
                help();
                break;

            case("exit"):
                CommandExecution.exit();
                break;

            default:
                System.out.println();
                System.out.println("Unknown command");
                break;
        }

        System.out.println();
    }

    static void clearScreen(){
        for(int i = 0; i < 30; i++)
            System.out.println();
    }
}
