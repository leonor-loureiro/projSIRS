package client;

import client.UI.UserInterface;
import client.security.Login;

import crypto.Crypto;
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


/**
 * Main App Client Class
 * @author Andre Fonseca 84698
 * @author Leonor Loureiro 84736
 * @author Sebastiao Amaro 84767
 *
 * Runs the operations through a terminal interface
 *
 * Client side of the Remote Document Access SIRS project
 *
 *
 */
public class ClientApp {


    public static void main(String[] args) {
        // Initializes all the used cryptography
        Crypto.init();

        Login login = null;
        boolean running = true;

        while(running) {
            UserInterface.home();

            try{
                while(login == null)
                    login = UserInterface.requestLogin();

                UserInterface.welcome(login.getUsername());

                while (running) {
                    UserInterface.listCommands();
                    running = UserInterface.parseCommand();
                    UserInterface.clearScreen();
                }

            }catch(Exception e){
                //e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

    }


}
