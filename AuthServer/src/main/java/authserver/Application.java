package authserver;

import authserver.db.DBConnection;
import crypto.Crypto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
/**
 * Main App of AuthServer
 * @author Andre Fonseca 84698
 * @author Leonor Loureiro 84736
 * @author Sebastiao Amaro 84767
 *
 * Responsible for registering and authentication a user and
 * sharing public keys among users.
 *
 * Authentication server side of the Remote Document Access SIRS project
 *
 *
 */
@SpringBootApplication
public class Application {

    public static Properties properties;

    public static void main(String[] args) {

        Crypto.init();

        properties = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
            properties.load(input);

            System.out.println(properties.getProperty("database"));
            System.out.println(properties.getProperty("dbuser"));
            System.out.println(properties.getProperty("dbpassword"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}