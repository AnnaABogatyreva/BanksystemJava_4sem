package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface AccountantDao {
    Session getSession();

    void setCourse(String currency, double buy, double cost, double sell) throws Exception;

    void changeOperDate(Date date) throws Exception;
}
