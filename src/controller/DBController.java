package controller;

import model.*;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;
import java.sql.*;
import java.util.ArrayList;

public class DBController {
    static OracleConnection conn;

    private static ResultSet exc(String s) throws SQLException{
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(s);
        
    }

    private static Date CurTime(){
        return new Date(new java.util.Date().getTime());
    }

    // about user
    public static void createUser(User user) throws SQLException {
        String s = String.format("INSERT INTO USER VALUES 0,%s,%s,%s,%s,null,0", user.userName(), user.userPassword(), user.userEmail(), CurTime());
        exc(s);
    }

    public static void setUserPassword (User user,String newPassword) throws SQLException{
        String s2 = String.format("update \"user\" set password=\"%s\" where id= %s",newPassword ,user.userId());
        exc(s2);
    }

    public static User retrieveUserByName(String userName) throws SQLException {
        // get user
        String s1 = String.format("SELECT * FROM %s where username = %s","\"user\"",userName);
        ResultSet r1 = exc(s1);


        // get interest id
        ArrayList<Integer> interestId = new ArrayList<>();
        String s2 = String.format("SELECT * FROM %s where user_id = %s","\"interest_user\"",r1.getInt(1));
        ResultSet r2 = exc(s2);
        while (r2.next()){
            interestId.add(r2.getInt(2));
        }

        // get post id
        ArrayList<Integer> postId = new ArrayList<>();
        String s3 = String.format("SELECT * FROM %s where create_user_id = %s","\"post\"",r1.getInt(1));
        ResultSet r3 = exc(s3);
        while (r3.next()){
            postId.add(r3.getInt(1));
        }

        // get following id
        ArrayList<Integer> followingsIdList = new ArrayList<>();
        String s4 = String.format("SELECT * FROM %s where user_id = %s","\"follow_user\"",r1.getInt(1));
        ResultSet r4 = exc(s4);
        while (r4.next()){
            followingsIdList.add(r4.getInt(2));
        }

        // get follower id
        ArrayList<Integer> followerIdList = new ArrayList<>();
        String s5 = String.format("SELECT * FROM %s where user_befollowed_id = %s","\"follow_user\"",r1.getInt(1));
        ResultSet r5 = exc(s5);
        while (r5.next()){
            followerIdList.add(r5.getInt(1));
        }

        r1.next();
        return new User(
                r1.getInt(1),
                r1.getString(2),
                r1.getString(3),
                r1.getString(4),
                r1.getBoolean(8),
                r1.getDate(6).toString(),
                r1.getDate(7).toString(),
                interestId,
                postId,
                followingsIdList,
                followerIdList
        );
    }

    // about post
    public static Post retrievePostById(int postId) {
        return null;
    }

    // -------------Need to achieve---------------
    public static void addUserInterest(String username, String labelName) {
    }

    public static User retrieveUserById(int userId) {
        return null;
    }

    public static ArrayList<Post> searchPostByKey(String keyword) {
        return null;
    }

    public static ArrayList<String> getUserInterest(String username) {
        return null;
    }

    // --------------For test --------------------------

    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519D", "viukiyec");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
