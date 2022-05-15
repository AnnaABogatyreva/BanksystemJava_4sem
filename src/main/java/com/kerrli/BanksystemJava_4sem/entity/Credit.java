package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "credits")
public class Credit {
    @Id
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
    @Column(name = "curacc")
    private String currentAccountNum;
    @Column(name = "odacc")
    private String mainDutyAccountNum;
    @Column(name = "pcacc")
    private String procentAccountNum;
    @Column(name = "prodacc")
    private String overDutyAccountNum;
    @Column(name = "prpcacc")
    private String overProcentAccountNum;
    @Column(name = "update")
    private Date upDate;

    public Credit() {}

    public Credit(int idClient, String type, Date openDate, Date closeDate, String currentAccountNum,
                  String mainDutyAccountNum, String procentAccountNum, String overDutyAccountNum,
                  String overProcentAccountNum, Date upDate) {
        this.id = 0;
        this.idClient = idClient;
        this.type = type;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.currentAccountNum = currentAccountNum;
        this.mainDutyAccountNum = mainDutyAccountNum;
        this.procentAccountNum = procentAccountNum;
        this.overDutyAccountNum = overDutyAccountNum;
        this.overProcentAccountNum = overProcentAccountNum;
        this.upDate = upDate;
    }

    public Credit(Credit credit) {
        this.id = credit.id;
        this.idClient = credit.idClient;
        this.type = credit.type;
        this.openDate = credit.openDate;
        this.closeDate = credit.closeDate;
        this.currentAccountNum = credit.currentAccountNum;
        this.mainDutyAccountNum = credit.mainDutyAccountNum;
        this.procentAccountNum = credit.procentAccountNum;
        this.overDutyAccountNum = credit.overDutyAccountNum;
        this.overProcentAccountNum = credit.overProcentAccountNum;
        this.upDate = credit.upDate;
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

    public String getCurrentAccountNum() {
        return currentAccountNum;
    }

    public String getMainDutyAccountNum() {
        return mainDutyAccountNum;
    }

    public String getProcentAccountNum() {
        return procentAccountNum;
    }

    public String getOverDutyAccountNum() {
        return overDutyAccountNum;
    }

    public String getOverProcentAccountNum() {
        return overProcentAccountNum;
    }

    public Date getUpDate() {
        return upDate;
    }
}
