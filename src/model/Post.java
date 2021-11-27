package model;

import controller.DBController;

import java.util.Date;
import java.util.ArrayList;

public final record Post (int postId, String postTitle, String postAuthor, String content,
                          boolean visible, String postDate,
                          ArrayList<Integer> labelIdList){
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
                        """, );
    }
}
