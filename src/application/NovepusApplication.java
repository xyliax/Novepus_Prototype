package application;

import controller.NovepusController;
import view.NovepusIO;

import java.sql.SQLException;


public final class NovepusApplication {
    private final NovepusController novepusController;
    private final NovepusIO novepusIO;

    public NovepusApplication() {
        novepusController = new NovepusController();
        novepusIO = novepusController.getIo();
        novepusIO.novepusPrintln(this + " Finish setting up! ");
    }

    public static void main(String[] args) {
        NovepusApplication session = new NovepusApplication();
        try {
            session.launch();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            session.novepusIO.novepusPrintln("Connection Broken. Restart Needed.");
            session.novepusIO.novepusPrintln("Connection Broken. Restart Needed.");
            try {
                session.novepusController.getConnection().close();
            } catch (SQLException ignored) {
            }
            session.novepusIO.novepusPrintln("Connection Closed.");
        }
    }

    public void launch() throws SQLException {
        if (novepusController.getConnection() != null)
            novepusController.run();
    }
}
