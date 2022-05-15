package com.kerrli.BanksystemJava_4sem.entity;

import java.util.Date;

public class CreditGraphExt extends CreditGraph {
    private Date prevPayDate;

    public CreditGraphExt(CreditGraph creditGraph, Date prevPayDate) {
        super(creditGraph);
        this.prevPayDate = prevPayDate;
    }

    public Date getPrevPayDate() {
        return prevPayDate;
    }

    public void setPrevPayDate(Date prevPayDate) {
        this.prevPayDate = prevPayDate;
    }
}
