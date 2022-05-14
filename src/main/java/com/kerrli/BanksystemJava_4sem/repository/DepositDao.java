package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DepositDao {
    Session getSession();

    @Query
    List getDepositTermList();

    @Query
    List getDepositList(int idClient);

    @Query
    void createDeposit(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception;

    @Query
    void closeDeposit(int depositId, String creditAccountNum, String loginEmployee) throws Exception;

    @Query
    void updateDeposit(int depositId, Date newDate, String loginEmployee) throws Exception;
}
