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
        String s = String.format("INSERT INTO \"USER\" VALUES (0,%s,%s,%s,%s,null,0)", user.userName(), user.userPassword(), user.userEmail(), CurTime());
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
    public static Post retrievePostById(int postId) throws SQLException {
        //(int postId, String postTitle, String postAuthor, String content,
        //                          boolean visible, String postDate,
        //                          ArrayList<Integer> labelIdList)
        String s1 = String.format("SELECT P.ID, P.TITLE, U.USERNAME, P.CONTENT, P.ISDELETE, P.CREATE_TIME FROM POST P, USER U WHERE P.CREATE_USER_ID = U.ID AND P.ID = %d",postId);
        ResultSet r1 = exc(s1);
        ArrayList<Integer> lableIdList = null;
        String s2 = String.format("SELECT LABAL_NAME FROM \"INTEREST\" WHERE ID = %d",postId);
        ResultSet r2 = exc(s2);
        int i = 1;
        while(r2.next()){
            lableIdList.add(r2.getInt(i));
            i++;
        }
        return new Post(r1.getInt(1),r1.getString(2),r1.getString(3),r1.getString(4),r1.getBoolean(5),r1.getString(6),lableIdList);
    }


    // -------------Need to achieve---------------
    public static void addUserInterest(String username, String labelName) throws SQLException{
        String s = String.format("SELECT ID FROM \"USER\" WHERE USERNAME = %s",username);
        ResultSet r = exc(s);
        String str = String.format("INSERT INTO \"INTEREST\" VALUES (%d,%s)",r.getInt(1),labelName);
    }

    public static User retrieveUserById(int userId) throws SQLException {
/*
(int userId, String userName, String userPassword, String userEmail,
                         boolean online, String regDate, String exitDate,
                         ArrayList<Integer> interestIdList,
                         ArrayList<Integer> postIdList,
                         ArrayList<Integer> followingsIdList,
                         ArrayList<Integer> followersIdList)
 */
        String s = String.format("SELECT ID, USERNAME, PASSWORD, EMAIL, ISONLINE, CREATE_TIME, LAST_EXIT_TIME FROM \"USER\" WHERE USERID = %s",userId);
        ResultSet r = exc(s);

        ArrayList<Integer> followingsIdList = null;
        ArrayList<Integer> followersIdList = null;
        String s2 = String.format("SELECT USER_ID FROM FOLLOW_USER WHERE USER_BEFOLLOWED_ID = %d",userId);
        ResultSet r2 = exc(s2);
        String s3 = String.format("SELECT USER_BEFOLLOWED_ID FROM FOLLOW_USER WHERE USER_ID  = %d",userId);
        ResultSet r3 = exc(s3);

        while(r2.next()){
            followingsIdList.add(r2.getInt(1));
        }
        while(r3.next()){
            followersIdList.add(r3.getInt(1));
        }

        // get interest id list
        ArrayList<Integer> interestIdList = null;
        String s4 = String.format("SELECT INTEREST_ID FROM INTEREST_USER WHERE USER_ID = %d",userId);
        ResultSet r4 = exc(s4);
        while(r4.next()){
            interestIdList.add(r4.getInt(1));
        }

        // get post id list
        ArrayList<Integer> postIdList = null;
        String s5 = String.format("SELECT ID FROM POST WHERE CREATE_USER_ID = %d",userId);
        ResultSet r5 = exc(s5);
        while(r5.next()){
            postIdList.add(r5.getInt(1));
        }

        return new User(r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getBoolean(5),r.getString(6),r.getString(7),interestIdList,postIdList,followingsIdList,followersIdList);
    }

    public static ArrayList<Post> searchPostByKey(String keyword) throws SQLException {
        ArrayList<Integer> postIdlist = null;
        String s = "SELECT ID FROM POST WHERE CONTENT LIKE \'%"+keyword+"%\'";
        ResultSet r = exc(s);

        ArrayList<Post> postList = null;
        while(r.next()){
            postList.add(retrievePostById(r.getInt(1)));
        }
        return postList;
    }

    public static ArrayList<String> getUserInterest(int userId) {
        //String s = String.format("SELECT LABLE_NAME FROM INTEREST, INTEREST_USER WHERE ID = INTEREST_ID AND USER_ID = %d",userId);
        // TODO: 27/11/2021
        return null;
    }
    
    public static ArrayList<String> getPostLabel(int postId){
        // TODO: 27/11/2021  
        return null;
    }
    
    public static boolean userExist(String username) throws SQLException {
        String s = String.format("SELECT * FROM USER WHERE USERNAME = %s",username);
        ResultSet r = exc(s);
        return r != null;
        // TODO: 27/11/2021  
    }
    
    public static boolean postExist(String title){
        return true;
        // TODO: 27/11/2021  
    }

    public static void createPost(Post post){

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
