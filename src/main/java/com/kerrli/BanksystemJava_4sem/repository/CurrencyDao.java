package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyDao {
    Session getSession();

    @Query
    List getCurrencyList();

    @Query
    List getForeignCurrencyList();
}
