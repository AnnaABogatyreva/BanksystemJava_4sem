package com.kerrli.BanksystemJava_4sem.entity;

public class DepositExt extends Deposit {
    private String upDescript;

    public DepositExt(Deposit deposit, String upDescript) {
        super(deposit);
        this.upDescript = upDescript;
    }

    public String getUpDescript() {
        return upDescript;
    }

    public void setUpDescript(String upDescript) {
        this.upDescript = upDescript;
    }
}
