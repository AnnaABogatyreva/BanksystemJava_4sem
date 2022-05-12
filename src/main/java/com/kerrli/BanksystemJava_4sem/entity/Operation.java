package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "operations")
public class Operation {
    @Id
    @Column(name = "idoper")
    private int idOper;
    @Column(name = "db")
    private String debitAccountNum;
    @Column(name = "cr")
    private String creditAccountNum;
    @Column(name = "operdate")
    private Date operDate;
    @Column(name = "`sum`")
    private double sum;
    @Column(name = "employee")
    private int idEmployee;

    public Operation() {}

    public Operation(int idOper, String debitAccountNum, String creditAccountNum,
                     Date operDate, double sum, int idEmployee) {
        this.idOper = idOper;
        this.debitAccountNum = debitAccountNum;
        this.creditAccountNum = creditAccountNum;
        this.operDate = operDate;
        this.sum = sum;
        this.idEmployee = idEmployee;
    }
}
