package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emproles")
public class Emprole {
    @Id
    @Column(name = "role")
    private String role;
    @Column(name = "descript")
    private String descript;

    public Emprole() {}

    public Emprole(String role, String descript) {
        this.role = role;
        this.descript = descript;
    }
}
