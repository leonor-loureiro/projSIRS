import authserver.AuthService;
import authserver.db.DBConnection;
import authserver.exception.InvalidUserException;
import authserver.exception.UserAlreadyExistsException;
import crypto.Crypto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class AuthServiceTests {

    String username = "bjenbdwirnd82482su";
    String password = "password";
    String publicKey = "publicKey";
    DBConnection db;

    AuthService authService;

    @Before
    public void setUp() throws IOException {

        Crypto.init();

        Properties properties = new Properties();
        InputStream input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
        properties.load(input);

        db = new DBConnection(properties.getProperty("dbuser"),
                properties.getProperty("dbpassword"),
                properties.getProperty("database"));

        authService = AuthService.getInstance();
    }

    /**
     * Successfully registers a user
     */
    @Test
    public void successRegister() throws UserAlreadyExistsException, SQLException {
            authService.register(username, password, publicKey);
            Assert.assertFalse(db.getUser(username) == null);
    }

    /**
     * Tries to register a user with the same username as another that already exists
     */
    @Test(expected = UserAlreadyExistsException.class)
    public void userAlreadyExists() throws UserAlreadyExistsException {
        authService.register(username, password, publicKey);
        authService.register(username, password, publicKey);
    }

    /**
     * Tries to register a user with invalid username format
     */
    @Test
    public void invalidUsernameRegister() throws UserAlreadyExistsException {
        String res = authService.register("jcei!.*", password, publicKey);
        Assert.assertTrue(res == null);
    }

    /**
     * Successful login
     */
    @Test
    public void successLogin() throws UserAlreadyExistsException, InvalidUserException {
        authService.register(username, password, publicKey);
        String res = authService.login(username, password);

        Assert.assertTrue(res != null);
    }

    /**
     * Tries to login a user that doesn't exist
     */
    @Test(expected = InvalidUserException.class)
    public void userDoesNotExist() throws InvalidUserException {
        authService.login(username, password);
    }

    /**
     * Tries to login a user with invalid username format
     */
    @Test
    public void invalidUsernameLogin() throws InvalidUserException, UserAlreadyExistsException {
        authService.register(username, password, publicKey);
        String res = authService.login("ie.*!", password);

        Assert.assertTrue(res == null);
    }

    /**
     * Successfully authenticates a user
     */
    @Test
    public void successAuthenticate() throws UserAlreadyExistsException {
        String token = authService.register(username, password, publicKey);
        Assert.assertTrue(authService.authenticate(username, token));

    }

    /**
     * Tries to authenticate a user with an invalid username format
     */
    @Test
    public void invalidUsernameAuthenticate() throws UserAlreadyExistsException {
        String token = authService.register(username, password, publicKey);
        Assert.assertFalse(authService.authenticate("nue*!ís", token));

    }



    /**
     * Successfully retrieves a user's public key
     */
    @Test
    public void successfulGetPublicKey() throws UserAlreadyExistsException, InvalidUserException {
        authService.register(username, password, publicKey);
        String extractedKey = authService.getPublicKey(username);

        Assert.assertEquals(publicKey, extractedKey);
    }

    /**
     * Tries to retrieve the key of a user that does not exist
     */
    @Test(expected = InvalidUserException.class)
    public void getPublicKeyUserNotExists() throws InvalidUserException {
        authService.getPublicKey(username);
    }

    /**
     * Tries to get the public key for a username that is in an invalid format
     */
    @Test
    public void getPublicKeyInvalidUsername() throws InvalidUserException, UserAlreadyExistsException {
        authService.register(username, password, publicKey);
        Assert.assertTrue(authService.getPublicKey("od*!d") == null);
    }


    @After
    public void cleanUp() throws SQLException {
        db.removeUser(username);
    }
}
