package kz.segizbay;

import java.security.AuthProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryAuthProvider implements AuthenticationProvider {
    private class User {
        private String login;
        private String password;
        private String username;

        public User(String login, String password, String username) {
            this.login = login;
            this.password = password;
            this.username = username;
        }
    }

    private List<User> users;

    public InMemoryAuthProvider() {
        this.users = new ArrayList<>(Arrays.asList(
                new User("alex@gmail.com", "111", "Alex"),
                new User("jhon@gmail.com", "222", "Jhon"),
                new User("mike@gmail.com", "333", "Mike")
        ));
    }

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.login) && password.equals(user.password)) {
                return user.username;
            }
        }
        return null;
    }


}
