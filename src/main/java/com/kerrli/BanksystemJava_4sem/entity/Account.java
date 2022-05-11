package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "idclient")
    private int idClient;
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
}
