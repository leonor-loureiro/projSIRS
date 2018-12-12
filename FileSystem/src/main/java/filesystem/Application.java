package filesystem;

import crypto.Crypto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class Application {

    public static Properties properties;
    static Console coninput = System.console();
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) throws KeyStoreException, FileNotFoundException {


        properties = new Properties();
        InputStream input = null;
        try{
            input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
            properties.load(input);

        }catch (IOException e){
            e.printStackTrace();
        }
        //keystore directory
        String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";

        System.out.println("Please introduce the password");


        KeyStore ks = KeyStore.getInstance("jks");

        //get input from user
        char[] pw = requestSensibleInput();

        Boolean wrong = true;
        while(wrong) {
            try {
                ks.load(new FileInputStream(keystoreFile), pw);
                wrong = false;
                properties.setProperty("keystorepwd",String.copyValueOf(pw));
                properties.setProperty("keypwd",String.copyValueOf(pw));

                //if there is an exception it means the password was wrong so we try again
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
