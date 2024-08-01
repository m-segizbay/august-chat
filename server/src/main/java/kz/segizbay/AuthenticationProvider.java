package kz.segizbay;

public interface AuthenticationProvider {
    String getUserNameByLoginAndPassword(String login, String password);
}
