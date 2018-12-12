package authserver.db;

import authserver.data.User;
import java.sql.*;

/**
 * This class implements the operations that interact with the database
 * <li>Get User</li>
 * <li>Set User</li>
 */
public class DBConnection {

    //JDBC driver name
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    // Select user SQL
    private static final String SELECT_USER_SQL = "Select saltedPwd from users where username = ?";
    // Insert user SQL
    private static final String INSERT_USER_SQL = "insert into users (username, saltedPwd, kpub) values ( ? , ? , ?)";
    // Select public key SQL
    private static final String SELECT_KEY_SQL = "Select kpub from users where username = ?";
    // Remove user SQL
    private static final String REMOVE_USER_SQL = "delete from users where username = ?";

    // Database URL
    private final String DB_URL;// = "jdbc:mysql://localhost/sirs";
    //Database credentials
    private final String USER;
    private final String PASS;

    // Database connection
    private Connection conn = null;

    // Statements
    private PreparedStatement selectStm;
    private PreparedStatement insertStm;
    private PreparedStatement selectKeyStm;
    private PreparedStatement removeStm;


    /**
     * Creates a database connection instance
     * @param user database username
     * @param pass database password
     */
    public DBConnection(String user, String pass, String database) {
        USER = user;
        PASS = pass;
        DB_URL = "jdbc:mysql://" + database;

        // Register JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * Extracts the user from the database
     * @param username
     * @return the user
     * @throws SQLException when an sql exception occurs
     * @see User
     */
    public User getUser(String username) throws SQLException {

        //Create connection
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        // Prepare statment
        selectStm = conn.prepareStatement(SELECT_USER_SQL);
        //Set params
        selectStm.setString(1, username);

        //Execute query
        try (ResultSet resultSet = selectStm.executeQuery()) {

            String saltedPwd = null;
            //Extract result
            if (resultSet.next())
                saltedPwd = resultSet.getString("saltedPwd");

            if (saltedPwd == null)
                return null;

            return new User(username, saltedPwd, null);

        } finally {
            //Clean-up
            selectStm.close();
            conn.close();
        }
    }

    /**
     * Extracts the user's public key
     * @param username user's username
     * @return user's public key
     * @throws SQLException
     */
    public String getPublicKey(String username) throws SQLException {
        //Create connection
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        // Prepare statment
        selectKeyStm = conn.prepareStatement(SELECT_KEY_SQL);
        //Set params
        selectKeyStm.setString(1, username);

        //Execute query
        try (ResultSet resultSet = selectKeyStm.executeQuery()) {

            String key = null;
            //Extract result
            if (resultSet.next())
                key = resultSet.getString("kpub");

            if (key == null)
                return null;

            return key;

        } finally {
            //Clean-up
            selectKeyStm.close();
            conn.close();
        }

    }

    /***
     * Add the user to the database
     * @param user the user instance
     * @return true if insert successful; false otherwise
     * @throws SQLException when an sql exception occurs
     * @see User
     */
    public boolean setUser(User user) throws SQLException {

        // Create a connection to the database
        conn = DriverManager.getConnection(DB_URL,USER,PASS);

        //Prepare statement
        insertStm = conn.prepareStatement(INSERT_USER_SQL);

        try {
            // Set params
            insertStm.setString(1, user.getUsername());
            insertStm.setString(2, user.getSaltedPwd());
            insertStm.setString(3, user.getKpub());

            // Execute insert statement
            return insertStm.executeUpdate() == 1;

        } finally {
            // Clean up
            insertStm.close();
            conn.close();
        }
    }

    /**
     * Deletes a user from the database
     * @param username user's username
     * @return true if successful; false otherwise
     * @throws SQLException
     */
    public boolean removeUser(String username) throws SQLException {

        //Create connection
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        // Prepare statement
        removeStm = conn.prepareStatement(REMOVE_USER_SQL);
        //Set params
        removeStm.setString(1, username);

        try {
            // Execute delete statement
            return removeStm.executeUpdate() == 1;

        } finally {
            // Clean up
            removeStm.close();
            conn.close();
        }
    }

}
