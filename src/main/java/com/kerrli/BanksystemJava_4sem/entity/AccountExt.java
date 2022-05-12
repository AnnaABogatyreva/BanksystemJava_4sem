package com.kerrli.BanksystemJava_4sem.entity;

public class AccountExt extends Account {
    private String upDescript;

    public AccountExt(Account account, String upDescript) {
        super(account);
        this.upDescript = upDescript;
    }

    public String getUpDescript() {
        return upDescript;
    }

    public void setUpDescript(String upDescript) {
        this.upDescript = upDescript;
    }
}
