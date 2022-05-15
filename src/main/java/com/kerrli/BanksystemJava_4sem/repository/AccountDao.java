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

    @Query
    String generateAccountNum(String acc2p, String currencyCode);

    @Query
    Account createAccount(int idClient, String currencyCode, String acc2p, String descript) throws Exception;

    @Query
    double checkBalance(String accountNum);

    @Query
    String getSelectBlockLine(Account accountNum);

    @Query
    Account closeAccount(String accountNum) throws Exception;

    @Query
    List getZeroAccountList(int idClient);

    @Query
    List getAccountList(int idClient);

    @Query
    List getBankAccountList();
}
