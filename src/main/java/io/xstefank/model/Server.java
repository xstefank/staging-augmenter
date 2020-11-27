package io.xstefank.model;

public class Server {

    public String id;
    public String username;
    public String password;

    @Override
    public String toString() {
        return "Server{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
