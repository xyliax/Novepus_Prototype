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
        ResultSet r = stmt.executeQuery(s);
        return r;
    }

    private static String curTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new java.util.Date());
        return String.format("to_date('%s', 'SYYYY-MM-DD HH24:MI:SS')", dateString);
    }

    // about user
    public static void createUser(User user) throws SQLException {
        String s = String.format("INSERT INTO \"user\" VALUES (%s,'%s','%s','%s','%s',%s,%s,%s)", 0, user.userName(), user.userPassword(), user.userEmail(), "haha", curTime(), curTime(), 0);
        ResultSet rs = exc(s);
        rs.close();
    }

    public static void setUserPassword(String userName, String newPassword) throws SQLException {
        String s = String.format("update \"user\" set \"password\" = '%s' where \"username\"= '%s'", newPassword, userName);
        ResultSet rs = exc(s);
        rs.close();
    }

    public static void setUserEmail (String userName, String email) throws SQLException{
        String s = String.format("update \"user\" set \"email\" = '%s' where \"username\"= '%s'", email, userName);
        ResultSet rs = exc(s);
        rs.close();
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

        User u = new User(
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
        r1.close();
        r2.close();
        r3.close();
        r4.close();
        r5.close();
        return u;
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

        User u = new User(
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
        r1.close();
        r2.close();
        r3.close();
        r4.close();
        r5.close();
        return u;
    }

    public static void setUserStatus(String username, boolean status) throws SQLException {
        String s = String.format("update \"user\" set \"isonline\" = %s where \"username\"= '%s'", status ? "1" : "0", username);
        ResultSet rs = exc(s);
        rs.close();
    }

    public static void setPostStatus(int postId, boolean status) throws SQLException {
        String s = String.format("update \"post\" set \"isdelete\" = %s where \"id\"= %s", status ? "1" : "0", postId);
        ResultSet rs = exc(s);
        rs.close();
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
            r2.close();
            return interestString;
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;
    }

    public static boolean userExist(String username) throws SQLException {
        String s = String.format("SELECT * FROM \"user\" WHERE \"username\" = '%s'", username);
        ResultSet r = exc(s);
        boolean b = r.next();
        r.close();
        return b;
    }

    public static void addUserInterest(String username, String labelName) {
        try {
            String s = String.format("\"INSERT INTO \"interest_post\" VALUES(%s,'%s')",
                    retrieveUserByName(username).userId(),
                    addLabel(labelName));
            exc(s);
        } catch (Exception e) {
            System.out.println("Emm......");
        }
    }

    public static void userFollow(String follower,String followed){
        try {
            String s = String.format("INSERT INTO \"follow_user\" VALUES (%s,%s)",retrieveUserByName(follower).userId(),retrieveUserByName(followed).userId());
            ResultSet rs = exc(s);
            rs.close();
        }catch (Exception e){
            System.out.println("Emm......");
        }
    }

    public static void userUnfollow(String follower,String followed){
        try {
            String s = String.format("DELETE FROM \"follow_user\" WHERE \"user_id\" = %s AND \"user_befollowed_id\" = %s)",retrieveUserByName(follower).userId(),retrieveUserByName(followed).userId());
            ResultSet rs = exc(s);
            rs.close();
        }catch (Exception e){
            System.out.println("Emm......");
        }
    }

    // about post
    public static void createPost(Post post) throws SQLException {
        String s = String.format("INSERT INTO \"post\" VALUES (%s,%s,%s,'%s',%s,'%s')",
                0, retrieveUserByName(post.postAuthor()).userId(),
                curTime(), post.content(), 0, post.postTitle());

        for (String label:post.labelNameList()){
            addLabel(label);
        }

        ResultSet rs = exc(s);
        rs.close();
    }

    public static Post retrievePostById(int postId) throws SQLException {
        // get post
        String s = String.format("SELECT * FROM \"post\" WHERE \"id\" = %s", postId);
        ResultSet r = exc(s);


        // get interest id
        ArrayList<String> interestLabelList = new ArrayList<>();
        String s2 = String.format("SELECT * FROM \"interest_post\" where \"post_id\" = %s", postId);
        ResultSet r2 = exc(s2);
        while (r2.next()) {
            interestLabelList.add(getLabelById(r2.getInt(2)));
        }
        r.next();
        Post post = new Post(
                r.getInt(1),
                r.getString(6),
                retrieveUserById(r.getInt(2)).userName(),
                r.getString(4),
                r.getBoolean(5),
                r.getString(3),
                interestLabelList);
        r.close();
        r2.close();
        return post;

    }

    public static boolean postExist(int id) throws SQLException {
        String s = String.format("SELECT * FROM \"post\" WHERE \"id\" = %s", id);
        ResultSet r = exc(s);
        boolean b = r.next();
        r.close();
        return b;
    }

    public static ArrayList<Integer> getAllPostId() {
        ArrayList<Integer> idList = new ArrayList<>();

        try {
            String s = "SELECT * FROM \"post\"";
            ResultSet r = exc(s);
            while (r.next()) {
                idList.add(r.getInt(1));
            }
            r.close();
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
            r2.close();
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
            int result = r.getInt(1);
            r.close();
            return result;
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
            exc(s);
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
            r.close();
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
                int result = r.getInt(1);
                r.close();
                return result;
            }

            String s2 = String.format("INSERT INTO \"interest\" VALUES(%s,'%s')", 0, labelName);
            ResultSet r2 = exc(s2);
            ResultSet r3 = exc(s);
            int result = r3.getInt(1);
            r.close();
            r2.close();
            r3.close();
            return result;

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
                String result =  r.getString(2);
                r.close();
                return result;
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
                Comment c = new Comment(
                        r.getInt(1),
                        r.getInt(2),
                        retrieveUserById(r.getInt(3)).userName(),
                        r.getString(5),
                        r.getBoolean(6),
                        r.getString(4)
                );
                r.close();
                return c;
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
            r.close();
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
            r.close();
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return MegList;
    }

    public static int getOnlineUserNum() {
        try {
            String s = "SELECT count(*) FROM \"user\" WHERE isonline = 0";
            ResultSet rs = exc(s);
            int outcome = rs.getInt(1);
            rs.close();
            return outcome;
        }catch (SQLException e){
            System.out.println("ERROR");
        }
        return 0;
    }

    // about message
    public static void createMessage (Message message) throws SQLException{
        String s = String.format("INSERT INTO \"message\" VALUES (%s,%s,%s,'%s',%s,'%s')",
                0,
                retrieveUserByName(message.sender()).userId(),
                retrieveUserByName(message.receiver()).userId(),
                curTime(),
                message.content(),
                0);
        exc(s);
    }

    public static boolean messageExist(int message_id){
        try {
            String s = String.format("SELECT * FROM \"message\" WHERE \"id\" = %s", message_id);
            ResultSet r = exc(s);
            boolean b = r.next();
            r.close();
            return b;
        }catch (SQLException e){
            System.out.println("ERROR");
        }
        return false;
    }

    public static Message retrieveMessageById (int message_id) throws SQLException{
        // get post
        String s = String.format("SELECT * FROM \"message\" WHERE \"id\" = %s", message_id);
        ResultSet r = exc(s);

        Message m = new Message(
                r.getInt(1),
                retrieveUserById(r.getInt(2)).userName(),
                retrieveUserById(r.getInt(3)).userName(),
                r.getString(5),
                r.getBoolean(6),
                r.getString(4)
        );
        r.close();
        return m;
    }

    public static void setMessageStatus (int message_id,boolean deleted){
        try {
            String s = String.format("update \"message\" set \"isdelete\" = %s where \"id\"= '%s';", deleted ? "1" : "0", message_id);
            ResultSet rs = exc(s);
            rs.close();
        }catch (SQLException e){
            System.out.println("ERROR");
        }

    }

    public static ArrayList<Integer> getAllUserId() throws SQLException{
        ArrayList<Integer> userIdList = new ArrayList<>();
        String s= "SELECT \"id\" FROM \"user\"";
        ResultSet r = exc(s);
        while (r.next()){
            userIdList.add(r.getInt(1));
        }
        r.close();
        return userIdList;
    }

    // -------------Need to achieve---------------

    // adm
    public static ArrayList<Integer> getUserInterestPost(String userName){
        try {
            ArrayList<Integer> interestId = new ArrayList<>();

            String s2 = String.format("SELECT * FROM \"interest_user\" where \"user_id\" = '%s'", retrieveUserByName(userName).userId());
            ResultSet r2 = exc(s2);
            while (r2.next()) {
                interestId.add(r2.getInt(2));}

            for (int i:interestId) {
                String s3 = "SELECT \"post_id\" FROM \"interest_post\" WHERE \"interest_id\" = "+i;
                ResultSet r = exc(s3);
                while (r.next()){
                    interestId.add(r.getInt(1));
                }
                r.close();
            }

            return interestId;


        }catch (SQLException e){
            System.out.println("ERROR");
        }

        return null;
    }

    // --------------For test --------------------------
    public static void main(String[] args) {
        try {
            OracleConnection conn;
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519d", "viukiyec");

            // -------------------------
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}