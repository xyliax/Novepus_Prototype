package model;

import controller.DBController;

import java.util.ArrayList;

public final record User(int userId, String userName, String userPassword, String userEmail,
                         boolean online, String regDate, String exitDate,
                         ArrayList<Integer> interestIdList,
                         ArrayList<Integer> postIdList,
                         ArrayList<Integer> followingsIdList,
                         ArrayList<Integer> followersIdList) {

    public User(String userName, String userPassword, String userEmail) {
        this(0, userName, userPassword, userEmail, false,
                null, null, null, null, null, null);
    }

    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [    User_Information______%s  uid=%s
                        [        username________| %s
                        [        email___________| %s
                        [        register_date___| %s
                        [        last_online_____| %s
                        [            total_posts_| %d
                        [            followings__| %d
                        [            followers___| %d
                        [            interested__| %s
                        ------------------------------------------------
                        """,
                online ? "ONLINE" : "OFFLINE", userId, userName, userEmail,
                regDate, online ? "NOW" : exitDate,
                postIdList.size(), followingsIdList.size(), followersIdList.size(),
                DBController.getUserInterest(userName));
    }
}
