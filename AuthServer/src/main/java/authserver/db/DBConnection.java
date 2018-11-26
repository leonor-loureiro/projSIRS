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
    // Database URL
    private static final String DB_URL = "jdbc:mysql://localhost/sirs";
    // Select user SQL
    private static final String SELECT_USER_SQL = "Select saltedPwd from users where username = ?";
    // Insert user SQL
    private static final String INSERT_USER_SQL = "insert into users (username, saltedPwd) values ( ? , ? )";

    //Database credentials
    private final String USER;
    private final String PASS;

    // Database connection
    private Connection conn = null;

    // Statements
    private PreparedStatement selectStm;
    private PreparedStatement insertStm;


    /**
     * Creates a database connection instance
     * @param user database username
     * @param pass database password
     */
    public DBConnection(String user, String pass) {
        USER = user;
        PASS = pass;

        // Register JDBC driver
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Opens a connection to the database
     */
    public void openConnection() throws SQLException {

        // Open a connection
        conn = DriverManager.getConnection(DB_URL,USER,PASS);

        // Prepare statements
        selectStm = conn.prepareStatement(SELECT_USER_SQL);
        insertStm = conn.prepareStatement(INSERT_USER_SQL);

    }

    /**
     * Closes the connection to the database
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        // Close statements
        if(selectStm != null)
            selectStm.close();

        if(insertStm != null)
            insertStm.close();

        //Close connection
        if(conn != null)
            conn.close();
    }

    /**
     * Extracts the user from the database
     * @param username
     * @return the user
     * @throws SQLException when an sql exception occurs
     * @see User
     */
    public User getUser(String username) throws SQLException {

        if(selectStm == null || selectStm.isClosed())
            return null;

        //Set params
        selectStm.setString(1, username);

        try (ResultSet resultSet = selectStm.executeQuery()) {
            // Set params

            //Execute query

            String saltedPwd = null;
            //Extract result
            if (resultSet.next())
                saltedPwd = resultSet.getString("saltedPwd");

            if (saltedPwd == null)
                return null;

            return new User(username, saltedPwd);

        } finally {
            //Clean-up
            selectStm.clearParameters();
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

        if(insertStm == null || insertStm.isClosed())
            return false;

        try {
            // Set params
            insertStm.setString(1, user.getUsername());
            insertStm.setString(2, user.getSaltedPwd());

            // Execute insert statement
            return insertStm.executeUpdate() == 1;

        } finally {
            // Clean up
            insertStm.clearParameters();
        }
    }

}
