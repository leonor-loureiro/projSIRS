package authserver;

import authserver.db.DBConnection;
import crypto.Crypto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

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
    static Console coninput = System.console();
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws KeyStoreException, FileNotFoundException {



        String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
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

        System.out.println("Please introduce the password");


        KeyStore ks = KeyStore.getInstance("jks");

        char[] pw = requestSensibleInput();

        Boolean wrong = true;
        while(wrong) {
            try {
                ks.load(new FileInputStream(keystoreFile), pw);
                wrong = false;

            } catch (IOException e) {
                System.out.println("Wrong password try again");
                pw = requestSensibleInput();


            } catch (NoSuchAlgorithmException e) {

            } catch (CertificateException e) {

            }

        }


        Crypto.init();

        SpringApplication.run(Application.class, args);
    }

    public static char[] requestSensibleInput(){
        char[] result;
        try{
            result = coninput.readPassword();
        }catch(NullPointerException np){
            result = scan.next().toCharArray();
        }
        return result;
    }
}