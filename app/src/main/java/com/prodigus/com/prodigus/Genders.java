package com.prodigus.com.prodigus;

/**
 * Created by Matus on 05-May-2015.
 */
public class Genders {
    private int id;
    private String gen;

    public Genders(int id, String gen) {
        this.id = id;
        this.gen = gen;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    @Override
    public String toString() {
        return gen;
    }

}
