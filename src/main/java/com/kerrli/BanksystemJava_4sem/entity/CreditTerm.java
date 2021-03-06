package com.kerrli.BanksystemJava_4sem.entity;

import com.kerrli.BanksystemJava_4sem.util.Lib;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "creditterms")
public class CreditTerm {
    @Id
    @Column(name = "`type`")
    private String type;
    @Column(name = "monthCnt")
    private Integer monthCnt;
    @Column(name = "rate")
    private double rate;
    @Column(name = "ovdrate")
    private double overdueRate;
    @Column(name = "descript")
    private String descript;

    public CreditTerm() {}

    public CreditTerm(String type, Integer monthCnt, double rate, double overdueRate, String descript) {
        this.type = type;
        this.monthCnt = monthCnt;
        this.rate = rate;
        this.overdueRate = overdueRate;
        this.descript = descript;
    }

    public String getType() {
        return type;
    }

    public Integer getMonthCnt() {
        return monthCnt;
    }

    public double getRate() {
        return rate;
    }

    public double getOverdueRate() {
        return overdueRate;
    }

    public String getDescript() {
        return descript;
    }

    @Override
    public String toString() {
        return descript + ", " + Lib.formatSum(rate) + "% годовых";
    }
}
