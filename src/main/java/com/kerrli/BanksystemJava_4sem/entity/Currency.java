package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency {
    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "isocode")
    private String isocode;

    public Currency() {}

    public Currency(String code, String name, String isocode) {
        this.code = code;
        this.name = name;
        this.isocode = isocode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getIsocode() {
        return isocode;
    }
}
