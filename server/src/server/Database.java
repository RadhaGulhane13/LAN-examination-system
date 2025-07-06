package server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Database class manages all database operations for the LAN Examination System.
 * It provides methods for user authentication, question retrieval, and marks storage.
 * 
 * @author RADHA
 */
public class Database {
    
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    
    Connection con;
    ResultSet rs;
    private Properties dbProperties;
    
    /**
     * Creates a new Database instance and establishes connection using configuration.
     * Loads database configuration from database.properties file.
     * 
     * @throws ClassNotFoundException if Oracle JDBC driver is not found
     * @throws SQLException if database connection fails
     */
    public Database() throws ClassNotFoundException, SQLException {
        loadDatabaseConfiguration();
        establishConnection();
    }
    
    /**
     * Loads database configuration from properties file.
     */
    private void loadDatabaseConfiguration() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("database.properties")) {
            if (input != null) {
                dbProperties.load(input);
                LOGGER.info("Database configuration loaded successfully");
            } else {
                LOGGER.warning("Database configuration file not found, using defaults");
                setDefaultProperties();
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading database configuration: " + e.getMessage(), e);
            setDefaultProperties();
        }
    }
    
    /**
     * Sets default database properties if configuration file is not available.
     */
    private void setDefaultProperties() {
        dbProperties.setProperty("db.driver", "oracle.jdbc.driver.OracleDriver");
        dbProperties.setProperty("db.url", "jdbc:oracle:thin:@localhost:1521:xe");
        dbProperties.setProperty("db.username", "RADHA_2");
        dbProperties.setProperty("db.password", "RADHA");
    }
    
    /**
     * Establishes database connection using configured settings.
     * 
     * @throws ClassNotFoundException if Oracle JDBC driver is not found
     * @throws SQLException if database connection fails
     */
    private void establishConnection() throws ClassNotFoundException, SQLException {
        String driver = dbProperties.getProperty("db.driver");
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        
        Class.forName(driver);
        LOGGER.info("Database driver loaded successfully");
        
        con = DriverManager.getConnection(url, username, password);
        LOGGER.info("Database connection established successfully");
    }
    
    /**
     * Fetches all questions and answers from the database.
     * Uses proper resource management with try-with-resources.
     * 
     * @return ArrayList of Question objects
     * @throws SQLException if database query fails
     */
    public ArrayList<Question> fetch() throws SQLException {
        ArrayList<Question> qset = new ArrayList<>();
        String query = "SELECT * FROM Question";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Question q = new Question(
                    rs.getString(2), // question text
                    rs.getString(3), // option 1
                    rs.getString(4), // option 2
                    rs.getString(5), // option 3
                    rs.getString(6), // option 4
                    rs.getString(7)  // correct answer
                );
                qset.add(q);
            }
            
            LOGGER.info("Successfully fetched " + qset.size() + " questions from database");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching questions from database: " + e.getMessage(), e);
            throw e;
        }
        
        return qset;
    }
    
    /**
     * Validates user credentials against the database.
     * Uses prepared statement to prevent SQL injection.
     * 
     * @param username the username to validate
     * @param password the password to validate
     * @return user ID if valid, 0 if invalid
     * @throws SQLException if database query fails
     */
    public int isvaliduser(String username, String password) throws SQLException {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            LOGGER.warning("Invalid username or password provided (null or empty)");
            return 0;
        }
        
        String query = "SELECT ID FROM Student WHERE USERNAME = ? AND PASSWORD = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("ID");
                    LOGGER.info("User authentication successful for username: " + username);
                    return userId;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error validating user credentials: " + e.getMessage(), e);
            throw e;
        }
        
        LOGGER.warning("User authentication failed for username: " + username);
        return 0;
    }
    
    /**
     * Updates the marks of a student in the database.
     * Uses prepared statement for security and proper resource management.
     * 
     * @param marks the marks to set
     * @param ID the student ID
     * @throws SQLException if database update fails
     */
    public void setMarks(int marks, int ID) throws SQLException {
        if (ID <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + ID);
        }
        
        if (marks < 0) {
            throw new IllegalArgumentException("Marks cannot be negative: " + marks);
        }
        
        String sql = "UPDATE STUDENT SET MARKS = ? WHERE ID = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, marks);
            pstmt.setInt(2, ID);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                LOGGER.info("Successfully updated marks (" + marks + ") for student ID: " + ID);
            } else {
                LOGGER.warning("No student found with ID: " + ID + " for marks update");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating marks for student ID " + ID + ": " + e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Closes the database connection and releases resources.
     * Should be called when the Database instance is no longer needed.
     */
    public void close() {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (con != null && !con.isClosed()) {
                con.close();
                LOGGER.info("Database connection closed successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database connection: " + e.getMessage(), e);
        }
    }

}
