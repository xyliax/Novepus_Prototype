package model;

public final record Message(int messageId, String sender, String receiver, String content,
                            boolean deleted, String sentDate) {

    public Message(String sender, String receiver, String content) {
        this(0, sender, receiver, content, false, null);
    }

    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [    Message_Information______%s
                        [             from_______| %s
                        [              to________| %s
                        [           sent_date____| %s
                        [             size_______| %s
                        ------------------------------------------------
                        """,
                messageId, sender, receiver, sentDate, content.length());
    }
}
