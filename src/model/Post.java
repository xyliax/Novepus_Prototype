package model;

import controller.DBController;

import java.util.ArrayList;

public final record Post(int postId, String postTitle, String postAuthor, String content,
                         boolean visible, String postDate,
                         ArrayList<Integer> labelIdList) {

    public Post(String postTitle, String postAuthor, String content) {
        this(0, postTitle, postAuthor, content, true, null, null);
    }

    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [    Post_Information______%s
                        [        title___________| %s
                        [        author__________| %s
                        [        post_date_______| %s
                        [            labels______| %s
                        ------------------------------------------------
                        """, postId, postTitle, postAuthor, postDate,
                DBController.getPostLabel(postId));
    }
}
