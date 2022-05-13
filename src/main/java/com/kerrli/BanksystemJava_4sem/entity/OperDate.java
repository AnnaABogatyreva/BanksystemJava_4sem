package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "operdays")
public class OperDate {
    @Id
    @Column(name = "operdate")
    private Date operDate;
    @Column(name = "current")
    private int current;

    public OperDate() {}

    public OperDate(Date operDate, int current) {
        this.operDate = operDate;
        this.current = current;
    }

    public Date getOperDate() {
        return operDate;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setOperDate(Date operDate) {
        this.operDate = operDate;
    }
}
