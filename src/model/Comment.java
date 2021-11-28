package model;

public final record Comment(int commentId, int postId, String creator, String content,
                            boolean deleted, String createDate) {

    public Comment(int postId, String creator, String content) {
        this(0, postId, creator, content, false, null);
    }
}
