import authserver.AuthService;
import authserver.data.User;
import authserver.db.DBConnection;
import authserver.exception.UserAlreadyExistsException;
import crypto.Crypto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class AuthServiceTests {

    String username = "bjenbdwirnd82482su";
    String saltedHash = "saltedPassword";
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


    @Test
    public void successRegister() throws UserAlreadyExistsException, SQLException {
            authService.register(username, saltedHash, publicKey);
            Assert.assertFalse(db.getUser(username) == null);
    }

    @Test(expected = UserAlreadyExistsException.class)
    public void userAlreadyExists() throws UserAlreadyExistsException {
        authService.register(username, saltedHash, publicKey);
        authService.register(username, saltedHash, publicKey);
    }



    @After
    public void cleanUp() throws SQLException {
        db.removeUser(username);
    }
}
