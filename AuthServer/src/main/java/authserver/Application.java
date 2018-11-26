package authserver;

import authserver.db.DBConnection;
import authserver.data.User;
import authserver.exception.CryptoException;
import authserver.security.Crypto;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.Key;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
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
        // Set java security policy
        Security.setProperty("crypto.policy", "unlimited");

        // Add security provider
        if(Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());



        try {
            Key Kpriv = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
            PublicKey Kpub = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);

            System.out.println("Kpriv=" + Kpriv.toString());
            System.out.println("Kpub=" + Kpub.toString());

        } catch (CryptoException e) {
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
