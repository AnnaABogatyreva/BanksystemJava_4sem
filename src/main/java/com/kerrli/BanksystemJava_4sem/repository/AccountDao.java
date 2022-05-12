package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDao {
    Session getSession();

    boolean beginTransaction();

    void commitTransaction(boolean canStopTransaction);

    void rollbackTransaction(boolean canStopTransaction);

    String generateAccountNum(String acc2p, String currencyCode);

    Account createAccount(int idClient, String currencyCode, String acc2p, String descript);

    double checkBalance(String accountNum);

    String getSelectBlockLine(Account accountNum);

    Account closeAccount(String accountNum) throws Exception;

    List getZeroAccountList(int idClient);

    List getAccountList(int idClient);
}
