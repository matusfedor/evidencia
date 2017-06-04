package com.prodigus.com.prodigus;

/**
 * Created by Matus on 27-April-2017.
 */
public class StatisticInsert {

    //String stat_date, int cnt, String user, int attribute, String type
    private String stat_date;
    private int countOf;
    private String user;
    private int attribute;
    private String type;

    public StatisticInsert(String nick, int countOf, String user, int attribute, String type) {
        this.stat_date = nick;
        this.countOf = countOf;
        this.user = user;
        this.attribute = attribute;
        this.type = type;
    }
    public String getDate() {
        return stat_date;
    }

    public int getCountOf() { return countOf;}

    public String getUser() {
        return user;
    }

    public int getAttribute() {
        return attribute;
    }

    public String getType() {
        return type;
    }

}
