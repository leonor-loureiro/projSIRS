package client;

import client.UI.UserInterface;
import client.security.Login;

public class ClientApp {

    public static void main(String[] args) {
        Login login;
        login = UserInterface.requestLogin();
        UserInterface.welcome(login.getUsername());

        while(true) {
            UserInterface.listCommands();
            UserInterface.parseCommand();
        }

    }
}
