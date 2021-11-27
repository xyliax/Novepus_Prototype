package controller;

import controller.data.OracleData;
import model.Post;
import model.User;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDriver;
import view.NovepusIO;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private void mainMenu() {
        String cmd;
        do {
            io.showMainMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "r" -> registerGuide();
                case "l" -> {
                    loginGuide();
                    if (!Objects.equals(getCurrentUser(), GUEST_USER_NAME))
                        userMenu();
                }
                case "w" -> worldForum();
                case "q" -> io.systemPrintln("Quit session");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void userMenu() {
        String cmd;
        do {
            io.showUserMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "i" -> displayUserDetails();
                case "e" -> editUserDetails();
                case "p" -> postGuide();
                case "w" -> worldForum();
                case "s" -> manageFollows();
                case "m" -> mailBox();
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void worldForum() {
        String cmd;
        do {
            io.showForumMenu();
            cmd = io.readLine().strip().toLowerCase();
            switch (cmd) {
                case "v" -> displayAllPosts();
                case "r" -> displayInterestPosts();
                case "s" -> searchGuide();
                case "i" -> userMenu();
                case "p" -> postGuide();
                case "q" -> io.systemPrintln("Going Back");
                default -> io.systemPrintln("Unrecognized Command " + cmd);
            }
        } while (!Objects.equals(cmd, "q"));
    }

    private void mailBox() {

    }

    private void registerGuide() {
        String username;
        String password;
        String confirm;
        do {
            io.systemPrintln("Input your Username ('~' to quit)");
            username = io.readLine();
            if (username.equals("~"))
                return;
            if (/* TODO: 26/11/2021 exist */false) {
                io.systemPrintln(username + " has been taken!");
                username = null;
            }
        } while (username == null);
        do {
            io.systemPrintln("Input your Password");
            password = io.readPassword();
            io.systemPrintln("Confirm your Password (Repeat)");
            confirm = io.readPassword();
            if (!Objects.equals(password, confirm))
                io.systemPrintln("Confirmation Failure!");
        } while (!Objects.equals(password, confirm));
        // TODO: 26/11/2021 finish
    }

    private void loginGuide() {
        String username;
        String password;
        do {
            do {
                io.systemPrintln("Input your Username ('~' to quit)");
                username = io.readLine();
                if (username.equals("~"))
                    return;
                if (/* TODO: 26/11/2021 not exist*/false) {
                    io.systemPrintln(username + " does not exist!");
                    username = null;
                }
            } while (username == null);
            io.systemPrintln("Password");
            password = io.readPassword();
        } while (/* TODO: 26/11/2021 check*/ false);
        setCurrentUser(username);
        io.systemPrintln("Successfully Log In As " + username);
    }

    private void postGuide() {
        String title;
        String content;
        String confirm;
        if (Objects.equals(getCurrentUser(), GUEST_USER_NAME)) {
            io.systemPrintln("You must Log In before posting");
            loginGuide();
            if (Objects.equals(getCurrentUser(), GUEST_USER_NAME))
                return;
        }
        io.systemPrintln("Input the title");
        title = io.readLine();
        io.systemPrintln("You may input the content now");
        content = io.readText();
        io.systemPrintln("'w' to confirm, otherwise quit");
        confirm = io.readLine().strip().toLowerCase();
        if (!confirm.equals("q")) {
            System.out.println("Leaving");
            return;
        }
        // TODO: 26/11/2021 finish
    }

    private void manageFollows() {
        //User user = DBController.retrieveUserByName(currentUser);
    }

    private void displayUserDetails() {

    }

    private void editUserDetails() {

    }

    private void displayAllPosts() {
        ArrayList<Post> allPosts = new ArrayList<>();

    }

    private void displayInterestPosts() {

    }

    private void searchGuide() {
        String title;
        io.systemPrintln("Search by its title");
        title = io.readLine();
        // TODO: 26/11/2021 search and display
    }

    private void postDetailGuide(int postId) {
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
        } catch (SQLException sqlException) {
            connection = null;
            io.systemPrintln("Failure! Cannot connect to Oracle!");
        }
    }

    public NovepusIO getIo() {
        return io;
    }

    public OracleConnection getConnection() {
        return connection;
    }

    public void setConnection(OracleConnection connection) {
        this.connection = connection;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        io.setUsername(currentUser);
    }
}
