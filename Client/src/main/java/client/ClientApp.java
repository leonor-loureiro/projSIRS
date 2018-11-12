package client;

import client.UI.UserInterface;

public class ClientApp {

    public static void main(String[] args) {
        String[] login;
        login = UserInterface.requestLogin();
        UserInterface.welcome(login[0]);

        while(true) {
            UserInterface.listCommands();
            UserInterface.parseCommand();
            UserInterface.clearScreen();
        }

    }
}
