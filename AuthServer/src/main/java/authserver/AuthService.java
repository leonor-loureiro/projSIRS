package authserver;

import authserver.data.User;
import authserver.db.DBConnection;
import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import crypto.Crypto;
import crypto.TokenManager;
import crypto.exception.CryptoException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
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
    private  static final int VALID_PERIOD = 1800000;

    //Singleton instance
    private static AuthService instance = null;

    //Database connection instance
    private DBConnection db;

    //Random generator for token IDs
    private static Random random = new Random();

    private AuthService() {

        if(Application.properties == null){
            try {
                Application.properties = new Properties();
                InputStream input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
                Application.properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        if(!validUsername(username))
            return null;

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
        if(!validUsername(username))
            return null;

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
        if(!validUsername(username))
            return false;

        PublicKey key = null;
        try {
            key = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);
        } catch (CryptoException e) {
            return false;
        }
        return TokenManager.validateJTW(token, "authServer", username, key);
    }

    public String getPublicKey(String username) throws InvalidUserException {
        if(!validUsername(username))
            return null;

        try {
            // Get public key
            String  publicKey = db.getPublicKey(username);
            if(publicKey == null)
                throw new InvalidUserException("Invalid user");
            return publicKey;
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

    private boolean validUsername(String username){
        if (username!= null && username.matches("[a-zA-Z0-9]*")) {
                return true;
        }
        return false;
    }
}
