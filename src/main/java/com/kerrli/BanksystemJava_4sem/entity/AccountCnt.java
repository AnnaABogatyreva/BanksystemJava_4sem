package com.kerrli.BanksystemJava_4sem.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(AccountCnt.AccountCntKey.class)
@Table(name = "accountcnt")
public class AccountCnt {
    @Id
    @Column(name = "acc2p")
    private String acc2p;
    @Id
    @Column(name = "currency")
    private String currency;
    @Column(name = "cnt")
    private int cnt;

    public static class AccountCntKey implements Serializable {
        private String acc2p;
        private String currency;

        public AccountCntKey() {}


        public String getAcc2p() {
            return acc2p;
        }

        public String getCurrency() {
            return currency;
        }

        public void setAcc2p(String acc2p) {
            this.acc2p = acc2p;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @Override
        public boolean equals(Object object) {
            AccountCntKey accountCntPrefix = (AccountCntKey) object;
            return this.acc2p.compareTo(accountCntPrefix.acc2p) == 0
                    && this.currency.compareTo(accountCntPrefix.currency) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getAcc2p(), getCurrency());
        }
    }

    public AccountCnt() {}

    public AccountCnt(String acc2p, String currency, int cnt) {
        this.acc2p = acc2p;
        this.currency = currency;
        this.cnt = cnt;
    }

    public int getCnt() {
        return cnt;
    }
}
