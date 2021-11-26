package controller.data;

public enum OracleData {
    URL("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms"),
    USERNAME("20075519d"),
    PASSWORD("viukiyec");
    private final String data;

    OracleData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}