package com.kerrli.BanksystemJava_4sem.entity;

import com.kerrli.BanksystemJava_4sem.util.LibAccount;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account")
public class Account {
    @Column(name = "idclient")
    private int idClient;
    @Id
    @Column(name = "accountnum")
    private String accountNum;
    @Column(name = "currency")
    private String currency;
    @Column(name = "descript")
    private String descript;
    @Column(name = "closed")
    private Date closed;
    @Column(name = "`default`")
    private int def;

    public Account() {}

    public Account(int idclient, String accountNum, String currency, String descript, Date closed, int def) {
        this.idClient = idclient;
        this.accountNum = accountNum;
        this.currency = currency;
        this.descript = descript;
        this.closed = closed;
        this.def = def;
    }

    public Account(Account account) {
        this.idClient = account.getIdClient();
        this.accountNum = account.getAccountNum();
        this.currency = account.getCurrency();
        this.descript = account.getDescript();
        this.closed = account.getClosed();
        this.def = account.getDef();
    }

    public int getIdClient() {
        return idClient;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public String getAcc2p() {
        return accountNum.substring(0, 5);
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescript() {
        return descript;
    }

    public Date getClosed() {
        return closed;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
}
