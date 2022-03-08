package server.service;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class User {
    private int id;
    private String username;
    private String password;

    public User() {
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }


}
