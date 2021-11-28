package model;

import controller.DBController;

import java.util.ArrayList;

public final record Post(int postId, String postTitle, String postAuthor, String content,
                         boolean deleted, String postDate,
                         ArrayList<String> labelNameList) {

    public Post(String postTitle, String postAuthor, String content, ArrayList<String> labelNameList) {
        this(0, postTitle, postAuthor, content, false, null, labelNameList);
    }

    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [    Post_Information______%s
                        [        title___________| %s
                        [        author__________| %s
                        [        post_date_______| %s
                        [            likes_______| %s
                        [            labels______| %s
                        ------------------------------------------------
                        """, postId, postTitle, postAuthor, postDate,
                DBController.getPostLikes(postId),
                DBController.getPostLabel(postId));
    }
}
