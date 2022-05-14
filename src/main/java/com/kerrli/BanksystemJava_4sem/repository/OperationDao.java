package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.Client;
import com.kerrli.BanksystemJava_4sem.entity.Operation;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationDao {
    Session getSession();

    @Query
    void transaction(String debitAccountNum, String creditAccountNum,
                     double sum, String loginEmployee) throws Exception;

    @Query
    void convertation(String debitAccountNum, String creditAccountNum,
                      double sum, String loginEmployee) throws Exception;

    @Query
    Account getBankAccount(String accountNum, String mask) throws Exception;

    @Query
    Account getDefaultAccount(Client client, String currency) throws Exception;
}
