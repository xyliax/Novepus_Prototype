package model;

import java.util.ArrayList;
import java.util.Date;

public final record User(int userId, String userName, String userPassword, String userEmail,
                         boolean online, Date regDate, Date exitDate,
                         ArrayList<Integer> interestIdList,
                         ArrayList<Integer> postIdList,
                         ArrayList<Integer> followingsIdList,
                         ArrayList<Integer> followersIdList) {

}
