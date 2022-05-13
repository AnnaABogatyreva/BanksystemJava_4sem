package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountantDao {
    Session getSession();

    void setCourse(String currency, double buy, double cost, double sell) throws Exception;
}
