package controller;

import model.Comment;
import model.Message;
import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBController {
    static OracleConnection conn;
    static FileOutputStream stream;

    static {
        try {
            stream = new FileOutputStream("SQL_log.txt");
            stream.write(("Record All SQL statements at " + new Date() + "\n").getBytes());
        } catch (Exception ignored) {
        }
    }

    private static ResultSet execute(String s)
            throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stream.write((s + "\n").getBytes());
        } catch (IOException ignored) {
        }
        return stmt.executeQuery(s);
    }

    private static String curTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new java.util.Date());
        return String.format(
                "to_date('%s','SYYYY-MM-DD HH24:MI:SS')",
                dateString);
    }

    public static void createUser(User user)
            throws SQLException {
        String s = String.format(
                "INSERT INTO \"user\" VALUES (%s,'%s','%s','%s','%s',%s,%s,%s)",
                "0", user.userName(), user.userPassword(), user.userEmail(), "signature", curTime(), curTime(), 0);
        ResultSet rs = execute(s);
        rs.close();
    }

    public static void setUserPassword(String userName, String newPassword)
            throws SQLException {
        String s = String.format(
                "UPDATE \"user\" SET \"password\"='%s' WHERE \"username\"='%s'",
                newPassword, userName);
        ResultSet rs = execute(s);
        rs.close();
    }

    public static void setUserEmail(String userName, String newEmail)
            throws SQLException {
        String s = String.format(
                "UPDATE \"user\" SET \"email\"='%s' WHERE \"username\"='%s'",
                newEmail, userName);
        ResultSet rs = execute(s);
        rs.close();
    }

    public static User retrieveUserByName(String userName)
            throws SQLException {
        String s1 = String.format(
                "SELECT * FROM \"user\" WHERE \"username\"='%s'",
                userName);
        ResultSet r1 = execute(s1);
        r1.next();
        ArrayList<Integer> interestIdList = new ArrayList<>();
        String s2 = String.format(
                "SELECT * FROM \"interest_user\" WHERE \"user_id\"=%s",
                r1.getInt(1));
        ResultSet r2 = execute(s2);
        while (r2.next())
            interestIdList.add(r2.getInt(2));
        ArrayList<Integer> postIdList = new ArrayList<>();
        String s3 = String.format(
                "SELECT * FROM \"post\" WHERE \"create_user_id\"=%s",
                r1.getInt(1));
        ResultSet r3 = execute(s3);
        while (r3.next())
            postIdList.add(r3.getInt(1));
        ArrayList<Integer> followingsIdList = new ArrayList<>();
        String s4 = String.format(
                "SELECT * FROM \"follow_user\" WHERE \"user_id\"=%s",
                r1.getInt(1));
        ResultSet r4 = execute(s4);
        while (r4.next())
            followingsIdList.add(r4.getInt(2));
        ArrayList<Integer> followerIdList = new ArrayList<>();
        String s5 = String.format(
                "SELECT * FROM \"follow_user\" WHERE \"user_befollowed_id\"=%s",
                r1.getInt(1));
        ResultSet r5 = execute(s5);
        while (r5.next())
            followerIdList.add(r5.getInt(1));
        User user = new User(
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
        return user;
    }

    public static User retrieveUserById(int userId)
            throws SQLException {
        String s1 = String.format(
                "SELECT * FROM \"user\" WHERE \"id\"=%s",
                userId);
        ResultSet r1 = execute(s1);
        r1.next();
        ArrayList<Integer> interestIdList = new ArrayList<>();
        String s2 = String.format(
                "SELECT * FROM \"interest_user\" WHERE \"user_id\"=%s",
                r1.getInt(1));
        ResultSet r2 = execute(s2);
        while (r2.next())
            interestIdList.add(r2.getInt(2));
        ArrayList<Integer> postIdList = new ArrayList<>();
        String s3 = String.format(
                "SELECT * FROM \"post\" WHERE \"create_user_id\"=%s",
                r1.getInt(1));
        ResultSet r3 = execute(s3);
        while (r3.next())
            postIdList.add(r3.getInt(1));
        ArrayList<Integer> followingsIdList = new ArrayList<>();
        String s4 = String.format(
                "SELECT * FROM \"follow_user\" WHERE \"user_id\"=%s",
                r1.getInt(1));
        ResultSet r4 = execute(s4);
        while (r4.next())
            followingsIdList.add(r4.getInt(2));
        ArrayList<Integer> followerIdList = new ArrayList<>();
        String s5 = String.format(
                "SELECT * FROM \"follow_user\" WHERE \"user_befollowed_id\"=%s",
                r1.getInt(1));
        ResultSet r5 = execute(s5);
        while (r5.next())
            followerIdList.add(r5.getInt(1));
        User user = new User(
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
        return user;
    }

    public static void setUserStatus(String userName, boolean online)
            throws SQLException {
        String s = String.format(
                "UPDATE \"user\" SET \"isonline\"=%s WHERE \"username\"='%s'",
                online ? "1" : "0", userName);
        ResultSet rs = execute(s);
        rs.close();
    }

    public static void setPostStatus(int postId, boolean deleted)
            throws SQLException {
        String s = String.format(
                "UPDATE \"post\" SET \"isdelete\"=%s WHERE \"id\"=%s",
                deleted ? "1" : "0", postId);
        ResultSet rs = execute(s);
        rs.close();
    }

    public static ArrayList<String> getUserInterest(String userName) {
        try {
            ArrayList<Integer> interestId = new ArrayList<>();
            String s2 = String.format(
                    "SELECT * FROM \"interest_user\" WHERE \"user_id\"=%s",
                    retrieveUserByName(userName).userId());
            ResultSet r2 = execute(s2);
            while (r2.next())
                interestId.add(r2.getInt(2));
            ArrayList<String> interestNameList = new ArrayList<>();
            for (Integer id : interestId)
                interestNameList.add(getLabelById(id));
            r2.close();
            return interestNameList;
        } catch (SQLException e) {
            System.out.println("ERROR");
        }
        return null;
    }

    public static boolean userExist(String username) throws SQLException {
        String s = String.format(
                "SELECT * FROM \"user\" WHERE \"username\"='%s'",
                username);
        ResultSet r = execute(s);
        boolean b = r.next();
        r.close();
        return b;
    }

    public static void addUserInterest(String userName, String labelName) {
        try {
            String s = String.format(
                    "INSERT INTO \"interest_user\" VALUES(%s,%s)",
                    retrieveUserByName(userName).userId(),
                    addLabel(labelName));
            execute(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void userFollow(String userName, String followedName) {
        try {
            String s = String.format(
                    "INSERT INTO \"follow_user\" VALUES (%s,%s)",
                    retrieveUserByName(userName).userId(), retrieveUserByName(followedName).userId());
            ResultSet rs = execute(s);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void userUnfollow(String userName, String followedName) {
        try {
            String s = String.format(
                    "DELETE FROM \"follow_user\" WHERE \"user_id\"=%s AND \"user_befollowed_id\"=%s)",
                    retrieveUserByName(userName).userId(), retrieveUserByName(followedName).userId());
            ResultSet rs = execute(s);
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createPost(Post post)
            throws SQLException {
        String s = String.format(
                "INSERT INTO \"post\" VALUES (%s,%s,%s,'%s',%s,'%s')",
                "0", retrieveUserByName(post.postAuthor()).userId(),
                curTime(), post.content(), "0", post.postTitle());
        ResultSet rs = execute(s);
        rs.close();
        String q = "SELECT MAX(\"id\") FROM \"post\"";
        ResultSet qs = execute(q);
        qs.next();
        int maxPid = qs.getInt(1);
        for (String label : post.labelNameList()){
            String s1 = String.format(
                    "INSERT INTO \"interest_post\" VALUES(%s,%s)",
                    maxPid, addLabel(label));
            ResultSet r1 = execute(s1);
            r1.close();
        }
    }

    public static Post retrievePostById(int postId)
            throws SQLException {
        String s = String.format(
                "SELECT * FROM \"post\" WHERE \"id\"=%s",
                postId);
        ResultSet r = execute(s);
        ArrayList<String> interestLabelList = new ArrayList<>();
        String s2 = String.format(
                "SELECT * FROM \"interest_post\" WHERE \"post_id\"=%s",
                postId);
        ResultSet r2 = execute(s2);
        while (r2.next())
            interestLabelList.add(getLabelById(r2.getInt(2)));
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

    public static boolean postNotExist(int postId)
            throws SQLException {
        String s = String.format("SELECT * FROM \"post\" WHERE \"id\" = %s", postId);
        ResultSet r = execute(s);
        boolean b = r.next();
        r.close();
        return !b;
    }

    public static ArrayList<Integer> getAllPostId() {
        ArrayList<Integer> idList = new ArrayList<>();
        try {
            String s = "SELECT * FROM \"post\"";
            ResultSet r = execute(s);
            while (r.next())
                idList.add(r.getInt(1));
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }

    public static ArrayList<String> getPostLabel(int postId) {
        try {
            ArrayList<Integer> interestId = new ArrayList<>();
            String s2 = String.format(
                    "SELECT * FROM \"interest_post\" WHERE \"post_id\"=%s",
                    postId);
            ResultSet r2 = execute(s2);
            while (r2.next())
                interestId.add(r2.getInt(2));
            ArrayList<String> interestString = new ArrayList<>();
            for (Integer integer : interestId)
                interestString.add(getLabelById(integer));
            r2.close();
            return interestString;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void userLikePost(String userName, int postId) {
        try {
            String s = String.format(
                    "INSERT INTO \"like_post\" VALUES(%s,%s)",
                    postId, retrieveUserByName(userName).userId());
            execute(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPostLikes(int postId) {
        try {
            String s = String.format(
                    "SELECT COUNT(*) FROM \"like_post\" WHERE \"post_id\"=%s",
                    postId);
            ResultSet r = execute(s);
            r.next();
            int result = r.getInt(1);
            r.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void createComment(Comment c) {
        try {
            String s = String.format(
                    "INSERT INTO \"comment\" VALUES (%s,%s,%s,%s,'%s',%s)",
                    "0", c.postId(), retrieveUserByName(c.creator()).userId(), curTime(), c.content(), "0");
            execute(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getPostCommentId(int postId) {
        ArrayList<Integer> idList = new ArrayList<>();
        try {
            String s = String.format(
                    "SELECT * FROM \"comment\" WHERE \"post_id\"=%s",
                    postId);
            ResultSet r = execute(s);
            while (r.next())
                idList.add(r.getInt(1));
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }

    private static int addLabel(String labelName) {
        int result;
        try {
            String s = String.format(
                    "SELECT * FROM \"interest\" WHERE \"label_name\"='%s'",
                    labelName);
            ResultSet r = execute(s);
            if (r.next()) {
                result = r.getInt(1);
                r.close();
                return result;
            }
            String s2 = String.format(
                    "INSERT INTO \"interest\" VALUES(%s,'%s')",
                    "0", labelName);
            ResultSet r2 = execute(s2);
            ResultSet r3 = execute(s);
            r3.next();
            result = r3.getInt(1);
            r.close();
            r2.close();
            r3.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getLabelById(int labelId) {
        String result;
        try {
            String s = String.format(
                    "SELECT * FROM \"interest\" WHERE \"id\"=%s",
                    labelId);
            ResultSet r = execute(s);
            if (r.next()) {
                result = r.getString(2);
                r.close();
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Comment retrieveCommentById(int commentId) {
        try {
            String s = String.format(
                    "SELECT * FROM \"comment\" WHERE \"id\"=%s",
                    commentId);
            ResultSet r = execute(s);
            if (r.next()) {
                Comment comment = new Comment(
                        r.getInt(1),
                        r.getInt(2),
                        retrieveUserById(r.getInt(3)).userName(),
                        r.getString(5),
                        r.getBoolean(6),
                        r.getString(4)
                );
                r.close();
                return comment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Integer> getUserInbox(String userName) {
        ArrayList<Integer> messageList = new ArrayList<>();
        try {
            String s = String.format(
                    "SELECT * FROM \"message\" WHERE \"to_user_id\"=%s",
                    retrieveUserByName(userName).userId());
            ResultSet r = execute(s);
            while (r.next()){
                int mid = r.getInt(1);
                if(!retrieveMessageById(mid).deleted())
                    messageList.add(mid);
            }
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public static ArrayList<Integer> getUserSent(String userName) {
        ArrayList<Integer> messageList = new ArrayList<>();
        try {
            String s = String.format(
                    "SELECT * FROM \"message\" WHERE \"from_user_id\"=%s",
                    retrieveUserByName(userName).userId());
            ResultSet r = execute(s);
            while (r.next())
                messageList.add(r.getInt(1));
            r.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public static int getOnlineUserNum() {
        int result;
        try {
            String s = "SELECT COUNT(*) FROM \"user\" WHERE \"isonline\"=1";
            ResultSet rs = execute(s);
            rs.next();
            result = rs.getInt(1);
            rs.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void createMessage(Message message)
            throws SQLException {
        String s = String.format(
                "INSERT INTO \"message\" VALUES (%s,%s,%s,%s,'%s',%s)",
                "0", retrieveUserByName(message.sender()).userId(), retrieveUserByName(message.receiver()).userId(),
                curTime(), message.content(), "0");
        execute(s);
    }

    public static boolean messageNotExist(int message_id) {
        boolean result;
        try {
            String s = String.format(
                    "SELECT * FROM \"message\" WHERE \"id\"=%s",
                    message_id);
            ResultSet r = execute(s);
            result = r.next();
            r.close();
            return !result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Message retrieveMessageById(int message_id)
            throws SQLException {
        String s = String.format(
                "SELECT * FROM \"message\" WHERE \"id\"=%s",
                message_id);
        ResultSet rs = execute(s);
        rs.next();
        Message message = new Message(
                rs.getInt(1),
                retrieveUserById(rs.getInt(2)).userName(),
                retrieveUserById(rs.getInt(3)).userName(),
                rs.getString(5),
                rs.getBoolean(6),
                rs.getString(4)
        );
        rs.close();
        return message;
    }

    public static void setMessageStatus(int messageId, boolean deleted) {
        try {
            String s = String.format(
                    "UPDATE \"message\" SET \"isdelete\"=%s WHERE \"id\"=%s",
                    deleted ? "1" : "0", messageId);
            ResultSet rs = execute(s);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Integer> getAllUserId()
            throws SQLException {
        ArrayList<Integer> userIdList = new ArrayList<>();
        String s = "SELECT \"id\" FROM \"user\"";
        ResultSet rs = execute(s);
        while (rs.next())
            userIdList.add(rs.getInt(1));
        rs.close();
        return userIdList;
    }

    public static ArrayList<Integer> getUserInterestPost(String userName) {
        ArrayList<Integer> interestIdList = new ArrayList<>();
        ArrayList<Integer> postIdList = new ArrayList<>();
        try {

            String s2 = String.format(
                    "SELECT * FROM \"interest_user\" WHERE \"user_id\"=%s",
                    retrieveUserByName(userName).userId());
            ResultSet r2 = execute(s2);
            while (r2.next())
                interestIdList.add(r2.getInt(2));
            for (int id : interestIdList) {
                String s3 = "SELECT \"post_id\" FROM \"interest_post\" WHERE \"interest_id\"=" + id;
                ResultSet r = execute(s3);
                while (r.next())
                    postIdList.add(r.getInt(1));
                r.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postIdList;
    }

    public static void main(String[] args) {
        try {
            OracleConnection conn;
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection) DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms", "20075519d", "viukiyec");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}