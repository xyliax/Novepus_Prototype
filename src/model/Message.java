package model;

public final record Message(int messageId, String sender, String receiver, String content,
                            boolean deleted, String sentDate) {

    public Message(String sender, String receiver, String content) {
        this(0, sender, receiver, content, false, null);
    }
}
