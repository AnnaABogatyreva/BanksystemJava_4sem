package com.kerrli.BanksystemJava_4sem.entity;

import com.kerrli.BanksystemJava_4sem.util.Lib;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@IdClass(CreditGraph.CreditGraphKey.class)
@Table(name = "creditgraph")
public class CreditGraph {
    @Id
    @Column(name = "id")
    private int id;
    @Id
    @Column(name = "n")
    private int num;
    @Id
    @Column(name = "dateplat")
    private Date payDate;
    @Column(name = "sumod")
    private double mainDutySum;
    @Column(name = "sumpc")
    private double percentSum;
    @Column(name = "processed")
    private int processed;

    public static class CreditGraphKey implements Serializable {
        private int id;
        private int num;
        private Date payDate;

        public CreditGraphKey() {}

        public int getId() {
            return id;
        }

        public int getNum() {
            return num;
        }

        public Date getPayDate() {
            return payDate;
        }

        @Override
        public boolean equals(Object o) {
            CreditGraphKey key = (CreditGraphKey) o;
            return this.id == key.id && this.num == key.num && this.payDate.equals(key.payDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getNum(), getPayDate());
        }
    }

    public CreditGraph() {}

    public CreditGraph(int id, int num, Date payDate, double mainDutySum, double percentSum, int processed) {
        this.id = id;
        this.num = num;
        this.payDate = payDate;
        this.mainDutySum = mainDutySum;
        this.percentSum = percentSum;
        this.processed = processed;
    }

    public int getId() {
        return id;
    }

    public int getNum() {
        return num;
    }

    public Date getPayDate() {
        return payDate;
    }

    public String getPayDateStr() {
        return Lib.formatDate(payDate, "dd.MM.yyyy");
    }

    public double getMainDutySum() {
        return mainDutySum;
    }

    public String getMainDutySumStr() {
        return Lib.formatSum(mainDutySum);
    }

    public double getPercentSum() {
        return percentSum;
    }

    public String getPercentSumStr() {
        return Lib.formatSum(percentSum);
    }

    public String getTotalSumStr() {
        return Lib.formatSum(mainDutySum + percentSum);
    }

    public int getProcessed() {
        return processed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public void setMainDutySum(double mainDutySum) {
        this.mainDutySum = mainDutySum;
    }

    public void setPercentSum(double percentSum) {
        this.percentSum = percentSum;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
}
