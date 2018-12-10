package authserver;

import authserver.data.User;
import authserver.db.DBConnection;
import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import crypto.Crypto;
import crypto.TokenManager;
import crypto.exception.CryptoException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

/**
 * This class implements the functions of the authentication server
 * <li>Register</li>
 * <li>Login</li>
 * <li>Authenticate</li>
 */
public class AuthService {

    private static final String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
    private static final String keystorePwd = "password";
    private static final String keyPwd = "password";
    private static final String myAlias = "server-keypair";

    // Token Validity
    private  static final int VALID_PERIOD = 500000;//120000; //2min

    //Singleton instance
    private static AuthService instance = null;

    //Database connection instance
    private DBConnection db;

    //Random generator for token IDs
    private static Random random = new Random();

    private AuthService(){

        /*Properties properties = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        db = new DBConnection(Application.properties.getProperty("dbuser"),
               Application.properties.getProperty("dbpassword"),
               Application.properties.getProperty("database"));
    }

    public static AuthService getInstance(){
        if(instance == null)
            instance = new AuthService();
        return instance;
    }

    public String register(String username, String password, String Kpub) throws UserAlreadyExistsException {
        String saltedPwd = null;
        try {
            saltedPwd = Crypto.hash(password);

        } catch (CryptoException e) {
            throw  new UserAlreadyExistsException("Registration Failed");
        }
        User user = new User(username, saltedPwd, Kpub);


        try {
            if(db.setUser(user)) {
                System.out.println("Register successful");
                return generateToken(username);
            }


        } catch (SQLException e) {
            throw new UserAlreadyExistsException();
        }


        throw new UserAlreadyExistsException();
    }

    public String login(String username, String password) throws InvalidUserException {


        try {
            //Get user from database
            User user = db.getUser(username);
            System.out.println(user.getUsername() + "->" + user.getSaltedPwd());
            if (user == null)
                throw new InvalidUserException("User does not exist");

            //Compare password with the salted password in db
            if (Crypto.validateHash(password, user.getSaltedPwd())) {
                System.out.println("Login successful");
                return generateToken(username);
            }

            throw new InvalidUserException("Wrong password or username");

        } catch (Exception e){
            throw new InvalidUserException("Login failed.");
        }
    }

    public boolean authenticate(String username, String token){
        PublicKey key = null;
        try {
            key = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);
        } catch (CryptoException e) {
            return false;
        }
        return TokenManager.validateJTW(token, "authServer", username, key);
    }

    public String getPublicKey(String username) throws InvalidUserException {
        try {
            // Get public key
            return db.getPublicKey(username);

            /*
            // Encrypt public key with secret key
            Key Ks = Crypto.generateSecretKey(256, "AES");
            String cipheredKpub = Crypto.encryptAES(Ks, Kpub);

            // Encrypt secret key with authServer Kpriv
            Key Kpriv = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
            String cipheredKs = Crypto.encryptRSA(Ks.getEncoded(), Kpriv);

            return cipheredKpub + "|" + cipheredKs;
            */

        } catch (SQLException e) {
            throw new InvalidUserException("Invalid user");
        }
    }

    private String generateToken(String username){
        // Get the private key for the signature
        Key signingKey = null;
        try {
            signingKey = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
        } catch (CryptoException e) {
            return null;
        }
        String id = "" + random.nextInt(9000000) + 1000000;
        return TokenManager.createJTW(id, "authServer", username, VALID_PERIOD, signingKey);
    }


    /***********************************************************
     * For the client
     */

    public static PublicKey recoverPublicKey(String cipher){
        //Extract ciphered keys
        String cipheredKpub = cipher.substring(0, cipher.indexOf("|"));
        String cipheredKs = cipher.substring(cipher.indexOf("|") + 1);

        try {
            // Get public key from authserver
            PublicKey publicKeyAuth = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);

            // Decipher Ks
            byte[] Ks = Crypto.decryptRSA(Crypto.toByteArray(cipheredKs), publicKeyAuth);

            //Decrypt Kpub
            byte[] publicKeyBytes = Crypto.decryptAES(Ks, cipheredKpub.getBytes());

            // Convert to public key
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));

            return publicKey;

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return null;

    }
}
