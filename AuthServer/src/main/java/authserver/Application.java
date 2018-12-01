package authserver;

import authserver.db.DBConnection;
import authserver.data.User;
import authserver.exception.CryptoException;
import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import authserver.security.Crypto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;

@SpringBootApplication
public class Application {


    private static final String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
    private static final String keystorePwd = "password";
    private static final String keyPwd = "password";
    private static final String myAlias = "server-keypair";

    public static void main(String[] args) {

        Crypto.init();

        DBConnection db = new DBConnection("root", "nino_1500");

        /* String key = null;
        try {
            key = db.getPublicKey("test1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(key);*/
        /*try {
            System.out.println(AuthService.getInstance().login("try2", "password"));
        } catch (InvalidUserException e) {
            e.printStackTrace();
        }*/


        try {
            Key Kpriv = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
            PublicKey Kpub = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);

            System.out.println("Kpriv=" + Kpriv.toString());
            System.out.println("Kpub=" + Kpub.toString());

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            String datab64 = Crypto.toString(secretKey.getEncoded());

            /*String signature = Crypto.sign(datab64, (PrivateKey) Kpriv);
            if(Crypto.verifySignature(signature, datab64, Kpub))
                System.out.println("Valid");
            else
                System.out.println("Invalid");

            System.out.println(signature);*/

            String encryptedData = Crypto.encryptRSA(datab64, (PrivateKey) Kpriv);
            String decryptedData = Crypto.decryptRSA(encryptedData, Kpub);

            if (decryptedData.equals(datab64))
                System.out.println("Equals");
            else
                System.out.println("Not equals");


        } catch (CryptoException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        /*String saltedPass = "";
        try {
            String pwd = "password0000";
            saltedPass = Crypto.hash(pwd);
            System.out.println(saltedPass);


            DBConnection db = new DBConnection("root", "nino_1500");


            db.openConnection();

            User user = new User("sebas", saltedPass);
            System.out.println("Set user : " + db.setUser(user));

            user = db.getUser("sebas");
            System.out.println("username = " + user.getUsername() + "; password = " + user.getSaltedPwd());
            System.out.println("Password is valid: " + Crypto.validateHash(pwd, user.getSaltedPwd()));

            db.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }*/

        SpringApplication.run(Application.class, args);

    }
}