package model;

import controller.DBController;

import java.util.ArrayList;
import java.util.Date;

public final record User(int userId, String userName, String userPassword, String userEmail,
                         boolean online, String regDate, String exitDate,
                         ArrayList<Integer> interestIdList,
                         ArrayList<Integer> postIdList,
                         ArrayList<Integer> followingsIdList,
                         ArrayList<Integer> followersIdList) {
    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [    User_Information______%s
                        [        username________| %s
                        [        email___________| %s
                        [        register_date___| %s
                        [        last_online_____| %s
                        [            posts_______| %d
                        [            followings__| %d
                        [            followers___| %d
                        [            interested__| %s
                        ------------------------------------------------
                        """, online ? "ONLINE" : "OFFLINE", userName, userEmail,
                regDate, online ? "NOW" : exitDate,
                postIdList.size(), followingsIdList.size(), followersIdList.size(),
                DBController.getUserInterest(userId));
    }
}
