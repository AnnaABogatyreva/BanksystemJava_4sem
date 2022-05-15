package com.kerrli.BanksystemJava_4sem.entity;

public class CreditExt extends Credit {
    private String upDescript;

    public CreditExt(Credit credit, String upDescript) {
        super(credit);
        this.upDescript = upDescript;
    }

    public String getUpDescript() {
        return upDescript;
    }

    public void setUpDescript(String upDescript) {
        this.upDescript = upDescript;
    }
}
