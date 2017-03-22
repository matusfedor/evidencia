package com.prodigus.com.prodigus;

public class DataModel {
    private String name;
    private int anInt;
    private double aDouble;
    private String OtherData;

    public DataModel(String name, int anInt, double aDouble, String otherData) {
        this.name = name;
        this.anInt = anInt;
        this.aDouble = aDouble;
        OtherData = otherData;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public String getOtherData() {
        return OtherData;
    }

    public void setOtherData(String otherData) {
        OtherData = otherData;
    }
}