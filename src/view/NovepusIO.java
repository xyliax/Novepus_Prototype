package view;

import controller.DBController;
import model.Comment;
import model.Message;
import model.Post;
import model.User;

import java.util.ArrayList;
import java.util.Scanner;

public final class NovepusIO {
    private final Scanner scanner;
    private String username;

    public NovepusIO() {
        scanner = new Scanner(System.in);
        novepusPrintln(this + " Initialized");
    }

    public void showMainMenu() {
        System.out.println("""
                _______________________________________________
                        [        Main Menu            ]
                        |    'r'    to     New User   |
                        |    'l'    to      Log In    |
                        |    'w'    to      Forum     |
                        |    'q'    to      Exit      |
                -----------------------------------------------""");
    }

    public void showUserMenu() {
        System.out.printf("""
                        _______________________________________________
                                         User Center  [%s]
                                |    'i'     to    Basic Info |
                                |    'e'     to      Edit     |
                                |    'p'     to   Manage Post |
                                |    'w'     to      Forum    |
                                |    's'     to     Follows   |
                                |    'm'     to     MailBox   | [%d]-inbox
                                |    'q'     to     Log Out   |
                                Online User Number: %d
                        -----------------------------------------------%n""",
                username,
                DBController.getUserInbox(username).size(),
                DBController.getOnlineUserNum());
    }

    public void showPostMenu() {
        System.out.printf("""
                        _______________________________________________
                                       Post Management  [%s]
                                |    'p'     to    New Post   |
                                |    'v'     to  View My Posts|
                                |    'w'     to      Forum    |
                                |    'd'     to   Delete Post |
                                |    'q'     to     Go Back   |
                        -----------------------------------------------%n""",
                username);
    }

    public void showFollowMenu() {
        System.out.printf("""
                        _______________________________________________
                                       Social Option [%s]
                                |    'v'    to    View Follow |
                                |    'f'    to      Follow    |
                                |    'd'    to     Unfollow   |
                                |    'p'    to    Send Message|
                                |    'q'    to      Go Back   |
                        -----------------------------------------------%n""",
                username);
    }

    public void showMailBoxMenu() {
        System.out.printf("""
                        _______________________________________________
                                         Mail Box  [%s]
                                |    'p'    to    New Message |
                                |    'd'    to      Delete    |
                                |    'q'    to     Go Back    |
                        -----------------------------------------------%n""",
                username);
    }

    public void showForumMenu() {
        System.out.printf("""
                        _______________________________________________
                                        World Forum  [%s]
                                |    'v'    to    View Recent |
                                |    'r'    to    Recommends  |
                                |    's'    to      Select    |
                                |    'a'    to     All Users  |
                                |    'p'    to       Post     |
                                |    'q'    to      Go Back   |
                        -----------------------------------------------%n""",
                username);
    }

    public void showUserDetailMenu() {
        System.out.printf("""
                        _______________________________________________
                                       Item to Modify  [%s]
                                |    'p'    to     Password   |
                                |    'e'    to      Email     |
                                |    'i'    to     Interest   |
                                |    'q'    to      Go Back   |
                        -----------------------------------------------%n""",
                username);
    }

    public void showPostDetailMenu() {
        System.out.println("""
                _______________________________________________
                               Action on Post
                        |    'l'    to     Like       |
                        |    'c'    to    Comment     |
                        |    'q'    to    Go Back     |
                -----------------------------------------------""");
    }

    public String readLine() {
        String line;
        do {
            System.out.print("\t" + username + " % ");
            line = scanner.nextLine();
        } while (line.isBlank());
        return line;
    }

    public String readPassword() {
        String password;
        do {
            System.out.print("\t" + username + " % ");
            if (System.console() != null)
                password = String.valueOf(System.console().readPassword());
            else
                password = scanner.nextLine();
        } while (password.isBlank());
        return password;
    }

    public String readOptional() {
        String string;
        System.out.println(username + " % ");
        string = scanner.nextLine();
        return string;
    }

    public String readText() {
        StringBuilder text = new StringBuilder();
        String line;
        novepusPrintln("Reading multiple lines, double 'Enter' to finish");
        do {
            System.out.print(username + "[continue]:");
            line = scanner.nextLine();
            text.append(line).append('\n');
        } while (!line.isEmpty());
        novepusPrintln("Finished!");
        return String.valueOf(text);
    }

    public void novepusPrintln(Object o) {
        System.out.println("\tNovepus >>> " + o);
    }

    public void printUser(User user) {
        System.out.println(user);
    }

    public void printPost(Post post) {
        System.out.println(post);
        System.out.println("Showing Content\n");
        System.out.println(post.content());
        System.out.println("-----------------------------------------------Finish");
        ArrayList<Comment> commentList = new ArrayList<>();
        for (int id : DBController.getPostCommentId(post.postId())) {
            Comment comment = DBController.retrieveCommentById(id);
            if (!comment.deleted())
                commentList.add(comment);
        }
        System.out.printf("Displaying Comments, %d in total%n", commentList.size());
        printCommentList(commentList);
        System.out.println("All Comments have been displayed!");
    }

    public void printComment(Comment comment) {
        System.out.println(comment);
    }

    public void printMessage(Message message) {
        System.out.println(message);
    }

    public void printUserList(ArrayList<User> userList) {
        System.out.println("________NAME_____________________EMAIL______________________");
        for (User user : userList) {
            System.out.printf("| %-15s%-8s||%-28s|%n",
                    user.userName(), user.online() ? "ONLINE" : "OFFLINE",
                    user.userEmail().isEmpty() ? "NOT SET" : user.userEmail());
        }
        System.out.println("------------------------------------------------------------");
    }

    public void printPostList(ArrayList<Post> postList) {
        System.out.println("____________________________________________________________________________________________________");
        for (Post post : postList) {
            System.out.printf("pid=%-6s Title:%-20s Author:%-15s Size:%-5s  Date:%s%n\t\tLikes:%-6s Comments:%-6s%n",
                    post.postId(), post.postTitle(), post.postAuthor(), post.content().length(), post.postDate(),
                    DBController.getPostLikes(post.postId()), DBController.getPostCommentId(post.postId()).size());
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    public void printCommentList(ArrayList<Comment> commentList) {
        System.out.println("_____________________________________________________________________________________");
        for (Comment comment : commentList) {
            System.out.printf("cid=%-6s From %-15s At %s%n\tContent: %s",
                    comment.commentId(), comment.creator(), comment.createDate(), comment.content());
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public void printMessageList(ArrayList<Message> messageList) {
        System.out.println("_____________________________________________________________________________________");
        for (Message message : messageList) {
            System.out.printf("mid=%-6s From %-15s To %-15s At %s%n\tContent: %s",
                    message.messageId(), message.sender(), message.receiver(), message.sentDate(), message.content());
        }
        System.out.println("-------------------------------------------------------------------------------------");
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
