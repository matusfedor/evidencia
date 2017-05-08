package com.prodigus.com.prodigus;

import java.util.Date;

/**
 * Created by Matus on 05-May-2015.
 */
public class AttributeOrderDate {
    private int id;
    private Date changeDate;

    public AttributeOrderDate(int id, Date change_date) {
        this.id = id;
        this.changeDate = change_date;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return changeDate;
    }

    public void setDate(Date gen) {
        this.changeDate = gen;
    }
}
