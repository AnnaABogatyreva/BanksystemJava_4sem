package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositeDao {
    Session getSession();

    @Query
    List getDepositeList();

    @Query
    void createDeposite(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception;
}
