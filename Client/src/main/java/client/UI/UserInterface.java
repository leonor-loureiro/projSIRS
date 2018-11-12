package client.UI;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;

public interface UserInterface {

    static Scanner input = new Scanner(System.in);

    /**
     * Requests the user to input login info
     * @return array with username[0] and password [1]
     */
    static String[] requestLogin(){
        String login[] = new String[2];

        String username;
        String password;

        System.out.println();
        System.out.println("Please insert login information:");
        System.out.println("Username: ");
        username = input.next(); // TODO: validate input

        System.out.println("Password: ");
        password = input.next(); // TODO: validate input
        System.out.println();

        login[0] = username;
        login[1] = password;

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
        System.out.println("pull  - get files from remote server");
        System.out.println("push  - send file to remote server");
        System.out.println("add   - add new file to remote server");
        System.out.println("share - share file with another user");
        System.out.println("exit  - exit program");
        System.out.println();
    }

    static boolean parseCommand(){
        String command;

        System.out.println();
        System.out.println("Insert command:");

        command = input.next();

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

            case ("help"):
                help();
                break;

            case("exit"):
                CommandExecution.exit();
                break;

            default:
                System.out.println("Unknown command");
                return false;
        }

        System.out.println();
        return true;
    }

    static void clearScreen(){
        for(int i = 0; i < 30; i++)
            System.out.println();
    }
}
