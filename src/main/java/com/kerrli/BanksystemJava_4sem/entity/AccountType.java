package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounttype")
public class AccountType {
    @Id
    @Column(name = "acc2p")
    private String acc2p;
    @Column(name = "`type`")
    private String type;
    @Column(name = "descr")
    private String descript;

    public AccountType() {}

    public int getSign() {
        int sign = 0;
        if (type.compareTo("active") == 0)
            sign = 1;
        else if (type.compareTo("passive") == 0)
            sign = -1;
        return sign;
    }

    public String getDispType() {
        String type = "";
        if (this.type.compareTo("active") == 0)
            type = "А";
        else if (this.type.compareTo("passive") == 0)
            type = "П";
        return type;
    }
}
