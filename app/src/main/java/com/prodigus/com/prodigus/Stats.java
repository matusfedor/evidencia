package com.prodigus.com.prodigus;

/**
 * Created by Matus on 27-April-2017.
 */
public class Stats {
    private String datum;
    private int cnt;

    public Stats(String datum, int cnt) {
        this.datum = datum;
        this.cnt = cnt;
    }
    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public int getCnt() {
        return cnt;
    }

    public void setName(int cnt) {
        this.cnt = cnt;
    }

}
