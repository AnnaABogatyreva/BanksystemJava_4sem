package com.kerrli.BanksystemJava_4sem.entity;

import com.kerrli.BanksystemJava_4sem.util.Lib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "depositeterms")
public class DepositeTerm {
    @Id
    @Column(name = "`type`")
    private String type;
    @Column(name = "monthcnt")
    private int monthCnt;
    @Column(name = "cap")
    private String cap;
    @Column(name = "rate")
    private double rate;
    @Column(name = "descript")
    private String descript;
    @Column(name = "currency")
    private String currency;

    public DepositeTerm() {}

    public DepositeTerm(String type, int monthCnt, String cap, double rate, String descript, String currency) {
        this.type = type;
        this.monthCnt = monthCnt;
        this.cap = cap;
        this.rate = rate;
        this.descript = descript;
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public int getMonthCnt() {
        return monthCnt;
    }

    public String getCap() {
        return cap;
    }

    public double getRate() {
        return rate;
    }

    public String getDescript() {
        return descript;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return descript + " " + Lib.formatSum(rate) + "%";
    }
}
