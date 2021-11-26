package model;

import java.util.Date;

public final record Comment(int commentId, String creator, String content,
                            boolean visible, Date createDate) {
}
