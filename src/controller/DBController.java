package controller;

import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;

import java.sql.*;
import java.util.ArrayList;

public class DBController {
    static OracleConnection conn;

    private static ResultSet exc(String s) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(s);

    }

    private static Date curTime() {
        return new Date(new java.util.Date().getTime());
    }

    // about user
    public static void createUser(User user) throws SQLException {
        String s = String.format("INSERT INTO \"user\" VALUES (%d,'%s','%s','%s','%s','%s','%s',%d)", 0, user.userName(), user.userPassword(), user.userEmail(), "haha", curTime(), curTime(), 0);
        exc(s);
    }

    public static void setUserPassword(User user, String newPassword) throws SQLException {
        String s2 = String.format("update \"user\" set password=\"%s\" where \"id\"= %s", newPassword, user.userId());
        exc(s2);
    }

    public static User retrieveUserByName(String userName) throws SQLException {
        // get user
        String s1 = String.format("SELECT * FROM \"user\" WHERE \"username\" = '%s'", userName);
        ResultSet r1 = exc(s1);
        r1.next();


        // get interest id
        ArrayList<Integer> interestId = new ArrayList<>();
        String s2 = String.format("SELECT * FROM \"interest_user\" where \"user_id\" = %s",  r1.getInt(1));
        ResultSet r2 = exc(s2);
        while (r2.next()) {
            interestId.add(r2.getInt(2));
        }

        // get post id
        ArrayList<Integer> postId = new ArrayList<>();
        String s3 = String.format("SELECT * FROM \"post\" where \"create_user_id\" = %s", r1.getInt(1));
        ResultSet r3 = exc(s3);
        while (r3.next()) {
            postId.add(r3.getInt(1));
        }

        // get following id
        ArrayList<Integer> followingsIdList = new ArrayList<>();
        String s4 = String.format("SELECT * FROM \"follow_user\" where \"user_id\" = %s", r1.getInt(1));
        ResultSet r4 = exc(s4);
        while (r4.next()) {
            followingsIdList.add(r4.getInt(2));
        }

        // get follower id
        ArrayList<Integer> followerIdList = new ArrayList<>();
        String s5 = String.format("SELECT * FROM \"follow_user\" where \"user_befollowed_id\" = %s", r1.getInt(1));
        ResultSet r5 = exc(s5);
        while (r5.next()) {
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
        String s1 = String.format("SELECT P.id, P.title, U.username, P.content, P.isdelete, P.create_time FROM \"post\" P, \"user\" U WHERE \"P.create_user_id\" = \"U.id\" AND \"P.id\" = %d", postId);
        ResultSet r1 = exc(s1);
        ArrayList<Integer> lableIdList = null;
        String s2 = String.format("SELECT lable_name FROM \"interest\" WHERE \"id\" = %d", postId);
        ResultSet r2 = exc(s2);
        int i = 1;
        while (r2.next()) {
            lableIdList.add(r2.getInt(i));
            i++;
        }
        return new Post(r1.getInt(1), r1.getString(2), r1.getString(3), r1.getString(4), r1.getBoolean(5), r1.getString(6), lableIdList);
    }


    // -------------Need to achieve---------------
    public static void addUserInterest(String username, String labelName) throws SQLException {
        String s = String.format("SELECT id FROM \"user\" WHERE \"username\" = '%s'", username);
        ResultSet r = exc(s);
        String str = String.format("INSERT INTO \"interest\" VALUES (%d,'%s')", r.getInt(1), labelName);
    }

    public static User retrieveUserById(int userId) throws SQLException {
        String s = String.format("SELECT id, username, password, email, isonline, create_time, last_exit_time FROM \"user\" WHERE \"id\" = %s", userId);
        ResultSet r = exc(s);

        ArrayList<Integer> followingsIdList = null;
        ArrayList<Integer> followersIdList = null;
        String s2 = String.format("SELECT user_id FROM \"follow_user\" WHERE \"user_befollowed_id\" = %d", userId);
        ResultSet r2 = exc(s2);
        String s3 = String.format("SELECT user_befollowed_id FROM \"follow_user\" WHERE \"user_id = %d\"", userId);
        ResultSet r3 = exc(s3);

        while (r2.next()) {
            followingsIdList.add(r2.getInt(1));
        }
        while (r3.next()) {
            followersIdList.add(r3.getInt(1));
        }

        // get interest id list
        ArrayList<Integer> interestIdList = null;
        String s4 = String.format("SELECT interest_id FROM \"interest_user\" WHERE \"user_id\" = %d", userId);
        ResultSet r4 = exc(s4);
        while (r4.next()) {
            interestIdList.add(r4.getInt(1));
        }

        // get post id list
        ArrayList<Integer> postIdList = null;
        String s5 = String.format("SELECT id FROM \"post\" WHERE \"create_user_id\" = %d", userId);
        ResultSet r5 = exc(s5);
        while (r5.next()) {
            postIdList.add(r5.getInt(1));
        }

        return new User(r.getInt(1), r.getString(2), r.getString(3), r.getString(4), r.getBoolean(5), r.getString(6), r.getString(7), interestIdList, postIdList, followingsIdList, followersIdList);
    }

    public static ArrayList<Post> searchPostByKey(String keyword) throws SQLException {
        ArrayList<Integer> postIdlist = null;
        String s = "SELECT id FROM \"post\" WHERE \"content\" LIKE \'%" + keyword + "%\'";
        ResultSet r = exc(s);

        ArrayList<Post> postList = null;
        while (r.next()) {
            postList.add(retrievePostById(r.getInt(1)));
        }
        return postList;
    }

    public static ArrayList<String> getUserInterest (int userId){
            ArrayList<String> userInterestList = null;
            try{
                String s = String.format("SELECT I.lable_name FROM \"interest\" I, \"interest_id\" U WHERE \"I.id\" = \"U.interest_id\" AND \"U.user_id\" = %d", userId);
                ResultSet r = exc(s);
                while (r.next()) {
                    userInterestList.add(r.getString(1));
                }
            }catch(SQLException e){
                System.out.println("Select failed.");
            }
            return userInterestList;
    }

    public static ArrayList<String> getPostLabel(int postId) {
        ArrayList<String> postLableList = null;
        try{
            String s = String.format("SELECT I.lable_name FROM \"interest\" I, \"interest_post\" P WHERE \"I.id\" = \"P.interest_id\" AND \"P.post_id\"= %d", postId);
            ResultSet r = exc(s);
            while (r.next()) {
                postLableList.add(r.getString(1));
            }
        }
        catch (SQLException e){
            System.out.println("Select failed.");
        }
        return postLableList;
    }

    public static void createPost(Post post) throws SQLException {
        String s1 = String.format("SELECT id FROM \"user\" WHERE \"username\" = '%s'",post.postAuthor());
        ResultSet r = exc(s1);
        String s = String.format("INSERT INTO \"post\" VALUES (%d, %d, '%s', '%s', %b, '%s')",post.postId(),r.getInt(1),post.postDate(),post.content(),post.visible(),post.postTitle());
    }

    public static boolean userExist(String username) throws SQLException {
        String s = String.format("SELECT * FROM \"user\" WHERE \"username\" = '%s'", username);
        ResultSet r = exc(s);
        return r != null;
    }

    public static boolean postExist(String title) throws SQLException {
        String s = String.format("SELECT * FROM \"post\" WHERE \"title\" = '%s'", title);
        ResultSet r = exc(s);
        return r != null;
    }

    // --------------For test --------------------------

    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519d", "viukiyec");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(curTime());
        ArrayList<Integer> interests = new ArrayList<>();
        interests.add(1);
        interests.add(2);
        ArrayList<Integer> posts = new ArrayList<>();
        interests.add(1);
        interests.add(2);
        ArrayList<Integer> followings = new ArrayList<>();
        interests.add(100);
        interests.add(200);
        ArrayList<Integer> followers = new ArrayList<>();
        interests.add(100);
        interests.add(200);

        try {
            createUser(new User(0, "haha", "eueu", "2008@", false, curTime().toString(), curTime().toString(), interests, posts, followings, followers));
        } catch (SQLException e) {
            System.out.println("wrong!");
        }

    }
}