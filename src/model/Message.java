package model;

import java.util.Date;

public final record Message(int messageId, String sender, String receiver, String content,
                            boolean visible, Date sentDate) {
}
