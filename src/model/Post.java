package model;

import java.util.Date;
import java.util.ArrayList;

public final record Post (int postId, String postTitle, String postAuthor, String content,
                          boolean visible, String postDate,
                          ArrayList<Integer> labelIdList){

}
