package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "capterms")
public class CapTerm {
    @Id
    @Column(name = "cap")
    private String cap;
    @Column(name = "descript")
    private String descript;

    public CapTerm() {}

    public CapTerm(String cap, String descript) {
        this.cap = cap;
        this.descript = descript;
    }

    public String getCap() {
        return cap;
    }

    public String getDescript() {
        return descript;
    }
}
