package client;

import client.UI.UserInterface;
import client.security.Login;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;





public class ClientApp {


    public static void main(String[] args) throws Exception {
        Login login;
        UserInterface.home();
        login = UserInterface.requestLogin();
        UserInterface.welcome(login.getUsername());



        while (true) {
            UserInterface.listCommands();
            UserInterface.parseCommand();
            UserInterface.clearScreen();
        }

    }


}
