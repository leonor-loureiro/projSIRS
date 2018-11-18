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
        login = UserInterface.requestLogin();
        UserInterface.welcome(login.getUsername());

     //   login = UserInterface.requestLogin();
     //   UserInterface.welcome(login[0]);
        Communication comms = new Communication();
        RestTemplate Rest = comms.restTemplate();
        ResponseEntity<String> suptofs = Rest.getForEntity("https://localhost:8080/operations/test",String.class);
        System.out.println(suptofs.getStatusCode());
        ResponseEntity<String> suptoauth = Rest.getForEntity("https://localhost:8081/auth/test",String.class);
        System.out.println(suptoauth.getStatusCode());
    /* while(true) {
            UserInterface.listCommands();
            UserInterface.parseCommand();
        }
            UserInterface.clearScreen();
        } */

    }


}
