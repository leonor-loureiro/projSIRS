package authserver;

import authserver.data.User;
import authserver.db.DBConnection;
import authserver.exception.CryptoException;
import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import authserver.security.Crypto;
import authserver.security.TokenManager;

import java.sql.SQLException;
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
    private  static final int VALID_PERIOD = 120000; //2min

    //Singleton instance
    private static AuthService instance = null;

    //Database connection instance
    private DBConnection db;

    //Random generator for token IDs
    private static Random random = new Random();

    private AuthService(){
        db = new DBConnection("root", "nino_1500");
    }

    public static AuthService getInstance(){
        if(instance == null)
            instance = new AuthService();
        return instance;
    }

    public String register(String username, String password) throws UserAlreadyExistsException {
        String saltedPwd = null;
        try {
            saltedPwd = Crypto.hash(password);
            System.out.println(saltedPwd);
            System.out.println((Crypto.getSalt(saltedPwd)));
        } catch (CryptoException e) {
            throw  new UserAlreadyExistsException("Registration Failed");
        }
        User user = new User(username, saltedPwd);


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
        return TokenManager.validateJTW(token, "authServer", username);
    }

    private String generateToken(String username){
        return TokenManager.createJTW(""+random.nextInt(), "authServer", username, VALID_PERIOD);
    }
}
