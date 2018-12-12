import authserver.data.User;
import authserver.db.DBConnection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class DBOperationsTest {

    User user;
    DBConnection db;

    @Before
    public void setUp() throws IOException {
        Properties properties = new Properties();
        InputStream input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
        properties.load(input);

        db = new DBConnection(properties.getProperty("dbuser"),
                properties.getProperty("dbpassword"),
                properties.getProperty("database"));

        user = new User("username", "saltedPassword", "publicKey");
    }

    /**
     * User is successfully insert in the database
     * @throws SQLException
     */
    @Test
    public void successNewUser() throws SQLException {
        db.setUser(user);
        User dbUser = db.getUser(user.getUsername());

        Assert.assertEquals(user.getUsername(), dbUser.getUsername());
        Assert.assertEquals(user.getSaltedPwd(), dbUser.getSaltedPwd());
    }

    /**
     * Tries to add a user with a username that already exits in the database
     * @throws SQLException
     */
    @Test (expected = SQLException.class)
    public void userAlreadyExists() throws SQLException {
        db.setUser(user);
        db.setUser(user);
    }

    /**
     * Successfully retrieves a user's public key
     * @throws SQLException
     */
    @Test
    public void successGetPublicKey() throws SQLException {
        db.setUser(user);
        String publicKey = db.getPublicKey(user.getUsername());

        Assert.assertEquals(user.getKpub(), publicKey);
    }

    /**
     * Tries to retrieve a user from the database that does not exist
     * @throws SQLException
     */
    @Test
    public void noSuchUser() throws SQLException {
        User dbUser = db.getUser(user.getUsername());

        Assert.assertTrue(dbUser == null);
    }

    /**
     * Tries to get the public key from a user that does not exist
     * @throws SQLException
     */
    @Test
    public void noSuchUserInGetKPub() throws SQLException {
        String publicKey = db.getPublicKey(user.getUsername());

        Assert.assertTrue(publicKey == null);
    }



    @After
    public void cleanUp() throws SQLException {
        db.removeUser(user.getUsername());
    }
}
