package application;

import controller.NovepusController;
import view.NovepusIO;

import java.sql.SQLException;


public class NovepusApplication {
    private final NovepusController novepusController;
    private final NovepusIO novepusIO;

    NovepusApplication() {
        novepusController = new NovepusController();
        novepusIO = novepusController.getIo();
        novepusIO.systemPrintln(this + " Finish setting up! ");
    }

    public static void main(String[] args) throws SQLException {
        NovepusApplication session = new NovepusApplication();
        session.launch();
    }

    public void launch() throws SQLException {
        if (novepusController.getConnection() != null)
            novepusController.run();
    }
}
