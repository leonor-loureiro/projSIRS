package authserver;

import authserver.db.DBConnection;
import crypto.Crypto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

@SpringBootApplication
public class Application {


    private static final String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
    private static final String keystorePwd = "password";
    private static final String keyPwd = "password";
    private static final String myAlias = "server-keypair";
    public static Properties properties;

    public static void main(String[] args) throws KeyStoreException, FileNotFoundException {

        System.out.println("Please introduce the password");
        Scanner scanner =  new Scanner(System.in);
        String password = scanner.next();

        KeyStore ks = KeyStore.getInstance("jks");

        try {
            ks.load(new FileInputStream(keystoreFile),password.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            System.out.println("Wrong password");
        }
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



        //DBConnection db = new DBConnection("root", "nino_1500");

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


        /*try {

            PublicKey publicKey = Crypto.generateRSAKeys().getPublic();
            String publicKeyStr = Crypto.toString(publicKey.getEncoded());
            AuthService authService = AuthService.getInstance();

            Random random = new Random();
            String usermame = "username" + random.nextInt();

            String token = authService.register(usermame, "password", publicKeyStr);
            String cipheredKeys = authService.getPublicKey(usermame);

            PublicKey decipheredKey = Crypto.recoverPublicKey(Crypto.toByteArray(cipheredKeys));

            if(decipheredKey == null)
                System.out.println("decipheredKey == null");
            else if(Arrays.equals(decipheredKey.getEncoded(), publicKey.getEncoded()))
                System.out.println("Equals");
            else
                System.out.println("Not Equals");
/*
            //System.out.println(Crypto.toString(Crypto.generateRSAKeys().getPublic().getEncoded()));


            String kpub = "NHF1UkuPDTFFCmz3wyMEI+9n7uPrrH0IspekJc+joFyknrMAsPfMEX+Zg9mZlTboWA6tVmwIKgyHcGmtqlAgpgDWubrh42QnP+VzXOYRGnCERKQFGE9jvmEFXHXRPEzICTc2TioPdIZWns8hPHUTItjwGNgAC38tW8+Av3ed1o7iFCRoWUtee40md+1OFWM6fZA9AvhHrDZAvQqvAkUIVs1c46Qz08vne/l4FCM90KehmMNsNAM1U046IaQhegQFFGIBrTwcehQeWyDFr4QtXwoU4FjAqKBcfNLKuFBndcUrHUXmMrUtYYiL4X39/Ek2HG7/dBsWDf0KpR6LJNmrqTcdbXdlv7BuNfngL39MKrXwCRFMS92ZjfjIi2V5BLKn8xZTMu8wt3RULzIAnIxPv3z71Dc3X1E9LeGbzIHiKpY=|KCQlTERm9mmG6hK+ABoF3ywURrFHQlgURjz8hl9qlLh/jg6Y7GT+3JiA6Qv+G2k36kablQNG+lQr/HmrXt5fM2EJB056l+w10qBd0KRcmI3szRawErf+t5X27cFJppO3OWvSUGO/hsOykQiP6s21CXVXQTzy4/Nk9728WUD7MTxaNm5aZR+VZhSL3xpQVySloyLL/LHxeZl09mSfccC2AzYUNbEsFMYVfogPlTniNFroVOfl1A4hUorcaqOOB7dSmoG674RcaR+UPFT/mn8QheERxSR20xCrbNO+fXpjtU0s76yj1FJCCvzCs94jIe7B8P7R2ugQQcIRKLrpxjjAAw==";
            String cipheredKpub = Crypto.getSalt(kpub);
            String cipheredKs = kpub.substring(kpub.indexOf("|") + 1);

            // Get public key from authserver
            PublicKey Kpub = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);

            //Decrypt Ks
            byte[] Ks = Crypto.decryptRSA(cipheredKs, Kpub);

            //Decrypt Kpub
            byte[] KpubBytes = Crypto.decryptAES(Ks, cipheredKpub);

            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(KpubBytes));
            System.out.println(publicKey.toString());

            //System.out.println("cipheredKpub=  " + cipheredKpub);
            //System.out.println("cipheredKs=  " + cipheredKs);

            String data = "Ola Manel";
            SecretKey ks = Crypto.generateSecretKey();
            String mac = Crypto.computeMAC(ks, data.getBytes());
            System.out.println(mac);
            if(Crypto.validateMAC(ks,data.getBytes(), mac))
                System.out.println("Equals");
            else
                System.out.println("Not equals");

            Key Kpriv = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
            PublicKey Kpub = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);

            System.out.println("Kpriv=" + Kpriv.toString());
            System.out.println("Kpub=" + Kpub.getEncoded().length);


            KeyPair kp = Crypto.generateRSAKeys(2048);

            String encryptedData = Crypto.encryptRSA(kp.getPublic().getEncoded(), Kpriv);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            String datab64 = Crypto.toString(secretKey.getEncoded());

            String signature = Crypto.sign(datab64, (PrivateKey) Kpriv);
            if(Crypto.verifySignature(signature, datab64, Kpub))
                System.out.println("Valid");
            else
                System.out.println("Invalid");

            System.out.println(signature);

            String encryptedData = Crypto.encryptRSA(datab64, (PrivateKey) Kpriv);
            String decryptedData = Crypto.decryptRSA(encryptedData, Kpub);

            if (decryptedData.equals(datab64))
                System.out.println("Equals");
            else
                System.out.println("Not equals");

            byte[] cipher = Crypto.encryptAES(secretKey, Kpub.getEncoded());
            String plainText = Crypto.toString(Crypto.decryptAES(secretKey, cipher));

            String KpubStr =(Crypto.toString(Kpub.getEncoded()));
            System.out.println(plainText);

            if(plainText.equals(KpubStr))
                System.out.println("Equals");
            else
                System.out.println("Not Equals");



        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        /*AuthService authService = AuthService.getInstance();
        try {
            System.out.println(authService.getPublicKey("test1"));
        } catch (InvalidUserException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }*/

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