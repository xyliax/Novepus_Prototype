package controller;

import com.sun.jdi.event.StepEvent;
import model.Comment;
import model.Message;
import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBController {
    static OracleConnection conn;

    private static ResultSet exc(String s) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(s);

    }

    private static String curTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new java.util.Date());
        return String.format("to_date('%s', 'SYYYY-MM-DD HH24:MI:SS')", dateString);
    }

    // about user
    public static void createUser(User user) throws SQLException {
        String s = String.format("INSERT INTO \"user\" VALUES (%s,'%s','%s','%s','%s',%s,%s,%s)", 0, user.userName(), user.userPassword(), user.userEmail(), "haha", curTime(), curTime(), 0);
        exc(s);
    }

    public static void setUserPassword(String userName, String newPassword) throws SQLException {
        String s = String.format("update \"user\" set \"password\" = '%s' where \"username\"= '%s'", newPassword, userName);
        exc(s);
    }

    public static void setUserEmail (String userName, String email) throws SQLException{
        String s = String.format("update \"user\" set \"email\" = '%s' where \"username\"= '%s'", email, userName);
        exc(s);

    }

    public static User retrieveUserByName(String userName) throws SQLException {
        // get user
        String s1 = String.format("SELECT * FROM \"user\" WHERE \"username\" = '%s'", userName);
        ResultSet r1 = exc(s1);
        r1.next();

        // get interest id
        ArrayList<Integer> interestIdList = new ArrayList<>();
        String s2 = String.format("SELECT * FROM \"interest_user\" where \"user_id\" = %s", r1.getInt(1));
        ResultSet r2 = exc(s2);
        while (r2.next()) {
            interestIdList.add(r2.getInt(2));
        }

        // get post id
        ArrayList<Integer> postIdList = new ArrayList<>();
        String s3 = String.format("SELECT * FROM \"post\" where \"create_user_id\" = %s", r1.getInt(1));
        ResultSet r3 = exc(s3);
        while (r3.next()) {
            postIdList.add(r3.getInt(1));
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

        return new User(
                r1.getInt(1),
                r1.getString(2),
                r1.getString(3),
                r1.getString(4),
                r1.getBoolean(8),
                r1.getString(6),
                r1.getString(7),
                interestIdList,
                postIdList,
                followingsIdList,
                followerIdList
        );
    }

    public static User retrieveUserById(int userId) throws SQLException {
        // get user
        String s1 = String.format("SELECT * FROM \"user\" WHERE \"id\" = %s", userId);
        ResultSet r1 = exc(s1);
        r1.next();

        // get interest id
        ArrayList<Integer> interestIdList = new ArrayList<>();
        String s2 = String.format("SELECT * FROM \"interest_user\" where \"user_id\" = %s", r1.getInt(1));
        ResultSet r2 = exc(s2);
        while (r2.next()) {
            interestIdList.add(r2.getInt(2));
        }

        // get post id
        ArrayList<Integer> postIdList = new ArrayList<>();
        String s3 = String.format("SELECT * FROM \"post\" where \"create_user_id\" = %s", r1.getInt(1));
        ResultSet r3 = exc(s3);
        while (r3.next()) {
            postIdList.add(r3.getInt(1));
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

        return new User(
                r1.getInt(1),
                r1.getString(2),
                r1.getString(3),
                r1.getString(4),
                r1.getBoolean(8),
                r1.getString(6),
                r1.getString(7),
                interestIdList,
                postIdList,
                followingsIdList,
                followerIdList
        );
    }

    public static void setUserStatus(String username, boolean status) throws SQLException {
        String s = String.format("update \"user\" set \"isonline\" = '%s' where \"username\"= '%s'", status ? "1" : "0", username);
        exc(s);
    }

    public static void setPostStatus(int postId, boolean status) throws SQLException {
        String s = String.format("update \"post\" set \"isdelete\" = '%s' where \"id\"= %s", status ? "1" : "0", postId);
        exc(s);
    }

    public static ArrayList<String> getUserInterest(String userName) {
        try {
            ArrayList<Integer> interestId = new ArrayList<>();
            String s2 = String.format("SELECT * FROM \"interest_user\" where \"user_id\" = '%s'", retrieveUserByName(userName).userId());
            ResultSet r2 = exc(s2);
            while (r2.next()) {
                interestId.add(r2.getInt(2));
            }
            ArrayList<String> interestString = new ArrayList<>();
            for (Integer integer : interestId) {
                interestString.add(getLabelById(integer));
            }
            return interestString;
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;
    }

    public static boolean userExist(String username) throws SQLException {
        String s = String.format("SELECT * FROM \"user\" WHERE \"username\" = '%s'", username);
        ResultSet r = exc(s);
        return r.next();
    }

    public static void addUserInterest(String username, String labelName) {
        try {
            String s = String.format("\"INSERT INTO \"interest_post\" VALUES(%s,'%s')",
                    retrieveUserByName(username).userId(),
                    addLabel(labelName));
            ResultSet r = exc(s);
        } catch (Exception e) {
            System.out.println("Emm......");
        }
    }

    // about post
    public static void createPost(Post post) throws SQLException {
        String s = String.format("INSERT INTO \"post\" VALUES (%s,%s,%s,'%s',%s,'%s')",
                0, retrieveUserByName(post.postAuthor()).userId(),
                curTime(), post.content(), 0, post.postTitle());
        ResultSet r = exc(s);
    }

    public static Post retrievePostById(int postId) throws SQLException {
        // get post
        String s = String.format("SELECT * FROM \"post\" WHERE \"id\" = %s", postId);
        ResultSet r = exc(s);

        // get interest id
        ArrayList<Integer> interestIdList = new ArrayList<>();
        String s2 = String.format("SELECT * FROM \"interest_post\" where \"post_id\" = %s", postId);
        ResultSet r2 = exc(s2);
        while (r2.next()) {
            interestIdList.add(r2.getInt(2));
        }

        r.next();
        return new Post(
                r.getInt(1),
                r.getString(6),
                retrieveUserById(r.getInt(2)).userName(),
                r.getString(4),
                r.getBoolean(5),
                r.getString(3),
                interestIdList);
    }

    public static boolean postExist(int id) throws SQLException {
        String s = String.format("SELECT * FROM \"post\" WHERE \"id\" = %s", id);
        ResultSet r = exc(s);
        return r.next();
    }

    public static ArrayList<Integer> getAllPostId() {
        ArrayList<Integer> idList = new ArrayList<>();

        try {
            String s = "SELECT * FROM \"post\"";
            ResultSet r = exc(s);
            while (r.next()) {
                idList.add(r.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("error");
        }

        return idList;
    }

    public static ArrayList<String> getPostLabel(int postId) {

        try {
            ArrayList<Integer> interestId = new ArrayList<>();
            String s2 = String.format("SELECT * FROM \"interest_post\" where \"post_id\" = '%s'", postId);
            ResultSet r2 = exc(s2);
            while (r2.next()) {
                interestId.add(r2.getInt(2));
            }
            ArrayList<String> interestString = new ArrayList<>();
            for (Integer integer : interestId) {
                interestString.add(getLabelById(integer));
            }
            return interestString;
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;


    }

    public static void userLikePost(String userName, int postId) {
        try {
            String s = String.format("INSERT INTO \"like_post\" VALUES(%s,%s)", postId, retrieveUserByName(userName).userId());
            exc(s);
        } catch (Exception e) {
            System.out.println("Emm......");
        }

    }

    public static int getPostLikes(int postId) {
        try {
            String s = String.format("SELECT count(*) FROM \"like_post\" WHERE \"post_id\" = %s", postId);
            ResultSet r = exc(s);
            r.next();
            return r.getInt(1);
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return 0;
    }

    // about comment
    public static void createComment(Comment c) {
        try {
            String s = String.format("INSERT INTO \"post\" VALUES (%s,%s,%s,'%s',%s,'%s')",
                    0, c.postId(), retrieveUserByName(c.creator()).userId(), curTime(), c.content(), 0);
            ResultSet r = exc(s);
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
    }

    public static ArrayList<Integer> getPostCommentId(int postId) {
        ArrayList<Integer> idList = new ArrayList<>();
        try {
            String s = String.format("SELECT * FROM \"comment\" WHERE \"post_id\" = %s", postId);
            ResultSet r = exc(s);
            while (r.next()) {
                idList.add(r.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return idList;
    }

    // about others
    private static int addLabel(String labelName) {
        try {
            String s = String.format("SELECT * FROM \"interest\" WHERE \"label_name\" = '%s'", labelName);
            ResultSet r = exc(s);
            if (r.next()) {
                return r.getInt(1);
            }

            String s2 = String.format("INSERT INTO \"interest\" VALUES(%s,'%s')", 0, labelName);
            ResultSet r2 = exc(s2);
            ResultSet r3 = exc(s);
            r.next();
            return r3.getInt(1);

        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return 1;
    }

    public static String getLabelById(int id) {
        try {
            String s = String.format("SELECT * FROM \"interest\" where \"id\" = '%s'", id);
            ResultSet r = exc(s);
            if (r.next()) {
                return r.getString(2);
            }
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;
    }

    public static Comment retrieveCommentById(int comment_id) {
        try {
            String s = String.format("SELECT * FROM \"comment\" where \"id\" = '%s'", comment_id);
            ResultSet r = exc(s);
            if (r.next()) {
                return new Comment(
                        r.getInt(1),
                        r.getInt(2),
                        retrieveUserById(r.getInt(3)).userName(),
                        r.getString(5),
                        r.getBoolean(6),
                        r.getString(4)
                );
            }
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;
    }

    public static ArrayList<Integer> getUserInbox(String userName) {
        ArrayList<Integer> MegList = new ArrayList<>();
        try {
            String s = String.format("SELECT * FROM \"message\" WHERE \"to_user_id\" = %s", retrieveUserByName(userName).userId());
            ResultSet r = exc(s);
            while (r.next()) {
                MegList.add(r.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return MegList;
    }

    public static ArrayList<Integer> getUserSent(String userName) {
        ArrayList<Integer> MegList = new ArrayList<>();
        try {
            String s = String.format("SELECT * FROM \"message\" WHERE \"from_user_id\" = %s", retrieveUserByName(userName).userId());
            ResultSet r = exc(s);
            while (r.next()) {
                MegList.add(r.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return MegList;
    }

    // about message

    public static void createMessage(Message message){

    }

    // -------------Need to achieve---------------

    public static boolean messageExist(int message_id){}

    public static Message retrieveMessageById(int message_id){
        return null;
    }

    public static void setMessageStatus(int message_id,boolean deleted){
    }

    // adm
    public static int getOnlineUserNum(){
        return 1;
    }

    public static void userFollow(String follower,String followed){}

    public static void userUnfollow(String follower,String followed){}

    public static ArrayList<Integer> getUserInterestPost(String userName){
        return null;
    }

    // --------------For test --------------------------
    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519d", "viukiyec");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}