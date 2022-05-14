package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "deposits")
public class Deposite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "idclient")
    private int idClient;
    @Column(name = "`type`")
    private String type;
    @Column(name = "opendate")
    private Date openDate;
    @Column(name = "closedate")
    private Date closeDate;
    @Column(name = "mainacc")
    private String mainAccountNum;
    @Column(name = "percacc")
    private String percAccountNum;
    @Column(name = "`update`")
    private Date upDate;
    @Column(name = "capdate")
    private Date capDate;

    public Deposite() {}

    public Deposite(int idClient, String type, Date openDate, Date closeDate,
                    String mainAccountNum, String percAccountNum, Date upDate, Date capDate) {
        this.idClient = idClient;
        this.type = type;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.mainAccountNum = mainAccountNum;
        this.percAccountNum = percAccountNum;
        this.upDate = upDate;
        this.capDate = capDate;
    }
}
