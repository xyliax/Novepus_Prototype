package controller;

import controller.data.OracleData;
import model.Comment;
import model.Message;
import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;
import view.NovepusIO;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public final class NovepusController {
    private static final String GUEST_USER_NAME = "_guest_";
    private final NovepusIO io;
    private OracleConnection connection;
    private String currentUser;

    public NovepusController() {
        this.io = new NovepusIO();
        connectToOracle();
        setCurrentUser(GUEST_USER_NAME);
        io.systemPrintln(this + " Initialized");
    }

    private void mainMenu() throws SQLException {
        String cmd;
        do {
            io.showMainMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "r" -> registerGuide();
                case "l" -> {
                    loginGuide();
                    if (!Objects.equals(currentUser, GUEST_USER_NAME))
                        userMenu();
                }
                case "w" -> worldForum();
                case "q" -> io.systemPrintln("Quit session");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void userMenu() throws SQLException {
        String cmd;
        do {
            io.showUserMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "i" -> displayUserDetails();
                case "e" -> editUserDetails();
                case "p" -> postMenu();
                case "w" -> worldForum();
                case "s" -> manageFollows();
                case "m" -> mailBox();
                case "q" -> {
                    io.systemPrintln("Logging out...");
                    DBController.setUserStatus(currentUser, false);
                    setCurrentUser(GUEST_USER_NAME);
                }
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void postMenu() throws SQLException {
        String cmd;
        do {
            io.showPostMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "p" -> postGuide();
                case "v" -> displayMyPosts();
                case "w" -> worldForum();
                case "d" -> deletePost();
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void worldForum() throws SQLException {
        String cmd;
        do {
            io.showForumMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "v" -> displayAllPosts();
                case "r" -> displayInterestPosts();
                case "s" -> selectPost();
                case "i" -> userMenu();
                case "p" -> postGuide();
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void mailBox() throws SQLException {
        String cmd;
        do {
            ArrayList<Message> inbox = new ArrayList<>();
            ArrayList<Message> sent = new ArrayList<>();
            for (int id : DBController.getUserInbox(currentUser)) {
                Message message = DBController.retrieveMessageById(id);
                if (!message.deleted())
                    inbox.add(message);
            }
            for (int id : DBController.getUserSent(currentUser)) {
                Message message = DBController.retrieveMessageById(id);
                if (!message.deleted())
                    sent.add(message);
            }
            io.systemPrintln("Displaying User Inbox");
            io.printMessageList(inbox);
            io.systemPrintln("Display User Inbox Finished!");
            io.systemPrintln("Displaying User Sent");
            io.printMessageList(sent);
            io.systemPrintln("Display User Sent Finished!");
            io.showMailBoxMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "p" -> sendMessage();
                case "d" -> {
                    String mids;
                    int mid = 0;
                    do {
                        io.systemPrintln("Input the 'mid' to delete ('~' to quit)");
                        mids = io.readLine();
                        if (mids.equals("~"))
                            return;
                        try {
                            mid = Integer.parseInt(mids);
                        } catch (NumberFormatException numberFormatException) {
                            io.systemPrintln("Invalid pid value!");
                            mid = 0;
                            continue;
                        }
                        if (!DBController.messageExist(mid) || DBController.retrieveMessageById(mid).deleted()) {
                            io.systemPrintln(String.format("Message (mid=%s) does not exist! Cannot delete!", mid));
                            continue;
                        }
                        if (!DBController.getUserInbox(currentUser).contains(mid) &&
                                !DBController.getUserSent(currentUser).contains(mid))
                            io.systemPrintln(String.format("Message (mid=%s) is not yours! Cannot delete!", mid));
                    } while (!DBController.messageExist(pid) ||
                            (!DBController.getUserInbox(currentUser).contains(mid) &&
                                    !DBController.getUserSent(currentUser).contains(mid)));
                    DBController.setMessageStatus(mid, true);
                    io.systemPrintln(String.format("Successfully delete Message at %s", new Date()));
                }
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!cmd.equals("q"));
    }

    private void registerGuide() throws SQLException {
        String username;
        String password;
        String confirm;
        String email;
        do {
            io.systemPrintln("Input your Username ('~' to quit)");
            username = io.readLine();
            if (username.equals("~"))
                return;
            if (DBController.userExist(username))
                io.systemPrintln(username + " has been taken!");
            if (username.length() > 15)
                io.systemPrintln("Username oversize!");
        } while (DBController.userExist(username) || username.length() > 15);
        do {
            io.systemPrintln("Input your Password");
            password = io.readPassword();
            io.systemPrintln("Confirm your Password (Repeat)");
            confirm = io.readPassword();
            if (!Objects.equals(password, confirm))
                io.systemPrintln("Confirmation Failure!");
            if (password.length() > 15)
                io.systemPrintln("Password oversize!");
        } while (!Objects.equals(password, confirm) || password.length() > 15);
        do {
            io.systemPrintln("Your email (optional)");
            email = io.readOptional();
            if (email.length() > 28)
                io.systemPrintln("Email oversize!");
        } while (email.length() > 28);
        DBController.createUser(new User(username, password, email));
        io.systemPrintln(String.format("New User '%s' finished registration at %s",
                username, new Date()));
    }

    private void loginGuide() throws SQLException {
        String username;
        String password;
        do {
            do {
                io.systemPrintln("Input your Username ('~' to quit)");
                username = io.readLine();
                if (username.equals("~"))
                    return;
                if (!DBController.userExist(username))
                    io.systemPrintln(String.format("User '%s' does not exist!", username));
            } while (!DBController.userExist(username));
            io.systemPrintln("Input Password for " + username);
            password = io.readPassword();
            if (!DBController.retrieveUserByName(username).userPassword().equals(password))
                io.systemPrintln("Incorrect Password!");
        } while (!DBController.retrieveUserByName(username).userPassword().equals(password));
        setCurrentUser(username);
        DBController.setUserStatus(currentUser, true);
        io.systemPrintln("Successfully Log In As " + username);
        io.systemPrintln("Welcome!");
    }

    private void postGuide() throws SQLException {
        String title;
        String content;
        String confirm;
        if (Objects.equals(currentUser, GUEST_USER_NAME)) {
            io.systemPrintln("You must Log In before posting");
            loginGuide();
            if (Objects.equals(currentUser, GUEST_USER_NAME))
                return;
        }
        do {
            io.systemPrintln("Input the title ('~' to quit)");
            title = io.readLine();
            if (title.equals("~"))
                return;
            if (title.length() > 30)
                io.systemPrintln("Title oversize!");
        } while (title.length() > 30);
        io.systemPrintln("You may input the content now");
        content = io.readText();
        io.systemPrintln("'w' to confirm, otherwise quit");
        confirm = io.readLine().strip().toLowerCase();
        if (!confirm.equals("w")) {
            System.out.println("Leaving");
            return;
        }
        DBController.createPost(new Post(title, currentUser, content));
        io.systemPrintln(String.format("User '%s' creates a new Post '%s' at %s",
                currentUser, title, new Date()));
    }

    private void manageFollows() throws SQLException {
        User user = DBController.retrieveUserByName(currentUser);
        io.systemPrintln(String.format("User '%s' follows %s users and has %d followers!",
                user.userName(), user.followingsIdList().size(), user.followersIdList().size()));
        String cmd;
        do {
            io.showFollowMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "v" -> displayFollowDetails();
                case "f" -> addFollowing();
                case "d" -> deleteFollowing();
                case "p" -> sendMessage();
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void displayFollowDetails() throws SQLException {
        User user = DBController.retrieveUserByName(currentUser);
        ArrayList<User> followings = new ArrayList<>();
        ArrayList<User> followers = new ArrayList<>();
        for (int id : user.followingsIdList())
            followings.add(DBController.retrieveUserById(id));
        for (int id : user.followersIdList())
            followers.add(DBController.retrieveUserById(id));
        io.systemPrintln(followings.size() + " followings in total!");
        io.printUserList(followings);
        io.systemPrintln("Display followings finished!");
        io.systemPrintln(followers.size() + " followers in total!");
        io.printUserList(followers);
        io.systemPrintln("Display followers finished!");
    }

    private void addFollowing() throws SQLException {
        String userName;
        do {
            io.systemPrintln("Input the username of the user you want to follow ('~' to quit)");
            userName = io.readLine();
            if (userName.equals("~"))
                return;
            if (!DBController.userExist(userName))
                io.systemPrintln(String.format("User '%s' does not exist!"));
            if (DBController.retrieveUserByName(currentUser).followingsIdList().
                    contains(DBController.retrieveUserByName(userName).userId()))
                io.systemPrintln(String.format("You have already followed '%s'!", userName));
        } while (!DBController.userExist(userName) ||
                DBController.retrieveUserByName(currentUser).followingsIdList().
                        contains(DBController.retrieveUserByName(userName).userId()));
        DBController.userFollow(currentUser, userName);
        io.systemPrintln("Followed");
    }

    private void deleteFollowing() throws SQLException {
        String userName;
        do {
            io.systemPrintln("Input the username of the user you want to unfollow ('~' to quit)");
            userName = io.readLine();
            if (userName.equals("~"))
                return;
            if (!DBController.userExist(userName))
                io.systemPrintln(String.format("User '%s' does not exist!"));
            if (!DBController.retrieveUserByName(currentUser).followingsIdList().
                    contains(DBController.retrieveUserByName(userName).userId()))
                io.systemPrintln(String.format("You have not followed '%s' yet!", userName));
        } while (!DBController.userExist(userName) ||
                !DBController.retrieveUserByName(currentUser).followingsIdList().
                        contains(DBController.retrieveUserByName(userName).userId()));
        DBController.userUnfollow(currentUser, userName);
        io.systemPrintln("Unfollowed");
    }

    private void sendMessage() throws SQLException {
        String receiver;
        String content;
        do {
            io.systemPrintln("Input the username of the receiver ('~' to quit)");
            receiver = io.readLine();
            if (receiver.equals("~"))
                return;
            if (!DBController.userExist(receiver))
                io.systemPrintln(String.format("User '%s' does not exist!"));
        } while (!DBController.userExist(receiver));
        io.systemPrintln("You may input your Message content now");
        content = io.readText();
        DBController.createMessage(new Message(currentUser, receiver, content));
        io.systemPrintln("Sent!");
    }

    private void displayUserDetails() throws SQLException {
        User user = DBController.retrieveUserByName(currentUser);
        io.printUser(user);
    }

    private void editUserDetails() throws SQLException {
        displayUserDetails();
        String cmd;
        do {
            io.showUserDetailMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "p" -> {
                    String oldPassword;
                    String newPassword;
                    String confirm;
                    io.systemPrintln("You have to input your old Password first");
                    oldPassword = io.readPassword();
                    if (!Objects.equals(oldPassword, DBController.retrieveUserByName(currentUser).userPassword())) {
                        io.systemPrintln("Incorrect Password! Going Back");
                        return;
                    }
                    do {
                        io.systemPrintln("Input you new Password now");
                        newPassword = io.readPassword();
                        io.systemPrintln("Confirm your new Password (Repeat)");
                        confirm = io.readPassword();
                        if (newPassword.length() > 15)
                            io.systemPrintln("New Password oversize!");
                        if (!newPassword.equals(confirm))
                            io.systemPrintln("Confirmation Failure!");
                    } while (!newPassword.equals(confirm) | newPassword.length() > 15);
                    DBController.setUserPassword(currentUser, newPassword);
                    io.systemPrintln("Password Reset!");
                    DBController.createMessage(new Message("Admin",currentUser,"Reset Password."));
                }
                case "e" -> {
                    String newEmail;
                    do {
                        io.systemPrintln("Your new email");
                        newEmail = io.readOptional();
                        if (newEmail.length() > 28)
                            io.systemPrintln("Email oversize!");
                    } while (newEmail.length() > 28);
                    DBController.setUserEmail(currentUser, newEmail);
                    io.systemPrintln("Email Reset!");
                    DBController.createMessage(new Message("Admin",currentUser,"Reset Email."));
                }
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void deletePost() throws SQLException {
        displayMyPosts();
        String pids;
        int pid = 0;
        do {
            io.systemPrintln("Input the 'pid' to delete ('~' to quit)");
            pids = io.readLine();
            if (pids.equals("~"))
                return;
            try {
                pid = Integer.parseInt(pids);
            } catch (NumberFormatException numberFormatException) {
                io.systemPrintln("Invalid pid value!");
                pid = 0;
                continue;
            }
            if (!DBController.postExist(pid) || DBController.retrievePostById(pid).deleted()) {
                io.systemPrintln(String.format("Post (pid=%s) does not exist! Cannot delete!", pid));
                continue;
            }
            if (!Objects.equals(DBController.retrievePostById(pid).postAuthor(), currentUser))
                io.systemPrintln(String.format("Post (pid=%s) is not yours! Cannot delete!", pid));
        } while (!DBController.postExist(pid) || DBController.retrievePostById(pid).deleted() ||
                !Objects.equals(DBController.retrievePostById(pid).postAuthor(), currentUser));
        DBController.setPostStatus(pid, true);
        io.systemPrintln(String.format("Successfully delete Post '%s' at %s",
                DBController.retrievePostById(pid).postTitle(), new Date()));
    }

    private void displayMyPosts() throws SQLException {
        User user = DBController.retrieveUserByName(currentUser);
        ArrayList<Post> userPosts = new ArrayList<>();
        for (int id : user.postIdList()) {
            Post post = DBController.retrievePostById(id);
            if (!post.deleted())
                userPosts.add(post);
        }
        io.systemPrintln(userPosts.size() + " Posts in total!");
        io.printPostList(userPosts);
        io.systemPrintln("Display posts finished!");
    }

    private void displayAllPosts() throws SQLException {
        ArrayList<Post> allPosts = new ArrayList<>();
        for (int id : DBController.getAllPostId()) {
            Post post = DBController.retrievePostById(id);
            if (!post.deleted())
                allPosts.add(post);
        }
        io.systemPrintln(String.format("Displaying all Posts, %d in total!", allPosts.size()));
        io.printPostList(allPosts);
        io.systemPrintln("Display posts finished!");
    }

    private void displayInterestPosts() throws SQLException {
        io.systemPrintln("You are interested in " +
                DBController.getUserInterest(currentUser);
        ArrayList<Post> posts = new ArrayList<>();
        for (int id : DBController.getUserInterestPost(currentUser)) {
            Post post = DBController.retrievePostById(id);
            if (!post.deleted())
                posts.add(post);
        }
        io.systemPrintln(String.format("Displaying interesting Posts, %d in total!", posts.size()));
        io.printPostList(posts);
        io.systemPrintln("Display interesting posts finished!");
    }

    private void selectPost() throws SQLException {
        displayAllPosts();
        String pids;
        int pid = 0;
        do {
            io.systemPrintln("Input the 'pid' ('~' to quit)");
            pids = io.readLine();
            if (pids.equals("~"))
                return;
            try {
                pid = Integer.parseInt(pids);
            } catch (NumberFormatException numberFormatException) {
                io.systemPrintln("Invalid pid value!");
                pid = 0;
                continue;
            }
            if (!DBController.postExist(pid) || DBController.retrievePostById(pid).deleted())
                io.systemPrintln(String.format("Post (pid=%s) does not exist! Cannot select!", pid));
        } while (!DBController.postExist(pid) || DBController.retrievePostById(pid).deleted());
        displayPostDetails(pid);
        String cmd;
        do {
            io.showPostDetailMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "l" -> {
                    DBController.userLikePost(currentUser, pid);
                    io.systemPrintln("Liked");
                }
                case "c" -> {
                    String content;
                    io.systemPrintln("You may make comment to this Post");
                    content = io.readText();
                    DBController.createComment(new Comment(pid, currentUser, content));
                    io.systemPrintln(String.format("Successfully comment on Post (pid=%d)", pid));
                }
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void displayPostDetails(int postId) throws SQLException {
        Post post = DBController.retrievePostById(postId);
        io.printPost(post);
    }

    public void run() throws SQLException {
        mainMenu();
    }

    public void connectToOracle() {
        try {
            DriverManager.registerDriver(new OracleDriver());
            connection = (OracleConnection) DriverManager.getConnection(OracleData.URL.getData(),
                    OracleData.USERNAME.getData(), OracleData.PASSWORD.getData());
            DBController.conn = connection;
            io.systemPrintln("Successfully connect to Oracle -> " + connection);
        } catch (SQLException sqlException) {
            connection = null;
            io.systemPrintln("System Failure! Cannot connect to Oracle! Exit");
            System.exit(0);
        }
    }

    public NovepusIO getIo() {
        return io;
    }

    public OracleConnection getConnection() {
        return connection;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        io.setUsername(currentUser);
    }
}
