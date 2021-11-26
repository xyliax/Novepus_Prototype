package controller;

import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;

import java.sql.*;
import java.util.ArrayList;

public class DBController {
    static OracleConnection conn;

    public static void createUser(User user) throws SQLException {
        Date date = new Date(new java.util.Date().getTime());
        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                String.format("INSERT INTO USER VALUES null,%s,%s,%s,%s,null,0", user.userName(), user.userPassword(), user.userEmail(), date)
        );

    }


    // ---------------------
    public static void addUserInterest(String username, String labelName) {

    }

    public static User retrieveUserById(int userId) {
        return null;
    }

    public static User retrieveUserByName(String userName) {
        return null;
    }

    public static Post retrievePostById(int postId) {
        return null;
    }

    public static ArrayList<Post> searchPostByKey(String keyword) {
        return null;
    }

    public static ArrayList<String> getUserInterest(String username) {
        return null;
    }


    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519D", "viukiyec");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
