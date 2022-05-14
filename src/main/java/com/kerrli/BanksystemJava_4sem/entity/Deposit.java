package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "deposits")
public class Deposit {
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

    public Deposit() {}

    public Deposit(int idClient, String type, Date openDate, Date closeDate,
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

    public Deposit(Deposit deposit) {
        this.id = deposit.id;
        this.idClient = deposit.idClient;
        this.type = deposit.type;
        this.openDate = deposit.openDate;
        this.closeDate = deposit.closeDate;
        this.mainAccountNum = deposit.mainAccountNum;
        this.percAccountNum = deposit.percAccountNum;
        this.upDate = deposit.upDate;
        this.capDate = deposit.capDate;
    }

    public int getId() {
        return id;
    }

    public int getIdClient() {
        return idClient;
    }

    public String getType() {
        return type;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public String getMainAccountNum() {
        return mainAccountNum;
    }

    public String getPercAccountNum() {
        return percAccountNum;
    }

    public Date getUpDate() {
        return upDate;
    }

    public Date getCapDate() {
        return capDate;
    }

}
