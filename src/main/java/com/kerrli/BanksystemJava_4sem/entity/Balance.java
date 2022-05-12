package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@IdClass(Balance.BalanceKey.class)
@Table(name = "balance")
public class Balance {
    @Id
    @Column(name = "account")
    private String accountNum;
    @Id
    @Column(name = "dt")
    private Date date;
    @Column(name = "`sum`")
    private double sum;

    public static class BalanceKey implements Serializable {
        private String accountNum;
        private Date date;

        public BalanceKey() {}

        public String getAccountNum() {
            return accountNum;
        }

        public Date getDate() {
            return date;
        }

        @Override
        public boolean equals(Object o) {
            BalanceKey balanceKey = (BalanceKey) o;
            return this.accountNum.compareTo(balanceKey.accountNum) == 0
                    && this.date.compareTo(balanceKey.date) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getAccountNum(), getDate());
        }
    }

    public Balance() {}

    public Balance(String accountNum, Date date, double sum) {
        this.accountNum = accountNum;
        this.date = date;
        this.sum = sum;
    }
}
