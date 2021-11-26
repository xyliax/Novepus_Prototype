public class tmp {
    @Override
    public String toString() {
        return String.format("""
                        ________________________________________________
                        [User_Information______%s
                        [    username________| %s
                        [    email___________| %s
                        [    register_date___| %s
                        [    last_online_____| %s
                        [        likes_______| %d
                        [        posts_______| %d
                        [        followings__| %d
                        [        followed____| %d
                        ------------------------------------------------
                        """);
    }

}
