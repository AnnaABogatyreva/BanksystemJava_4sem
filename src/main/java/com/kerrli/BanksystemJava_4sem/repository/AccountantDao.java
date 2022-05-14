package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AccountantDao {
    Session getSession();

    @Query
    void setCourse(String currency, double buy, double cost, double sell) throws Exception;

    @Query
    void changeOperDate(Date date, String loginEmployee) throws Exception;
}
