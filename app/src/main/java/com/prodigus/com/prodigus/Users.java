package com.prodigus.com.prodigus;

/**
 * Created by Matus on 27-April-2017.
 */
public class Users {
    private String nick;
    private String name;

    public Users(String nick, String name) {
        this.nick = nick;
        this.name = name;
    }
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
