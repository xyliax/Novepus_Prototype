package controller;

import java.io.*;
import java.io.Console;
import java.sql.*;
import java.util.ArrayList;

import model.*;
import oracle.jdbc.driver.*;
import oracle.sql.*;

public class DBController {
    static OracleConnection conn ;
    public static void createUser(User user) throws SQLException{
        Date date = new Date(new java.util.Date().getTime());

        Statement stmt = conn.createStatement();
        ResultSet rset = stmt.executeQuery(
                String.format("INSERT INTO USER VALUES %s,%s,%s,%s,%s","null",user.userName(),user.userPassword(),user.userEmail())
        );

    }


    // ---------------------
    public static void addUserInterest(String username,String labelName){

    }

    public static User retrieveUserById(int userId){
        return null;
    }

    public static User retrieveUserByName(String userName){
        return null;
    }

    public static Post retrievePostById(int postId){
        return null;
    }

    public static ArrayList<Post> searchPostByKey(String keyword){
        return null;
    }



    public static void main(String[] args){
        try {
            DriverManager.registerDriver(new OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms","20075519D","viukiyec");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
