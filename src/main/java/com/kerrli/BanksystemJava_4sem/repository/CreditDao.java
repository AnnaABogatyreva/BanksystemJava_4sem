package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditDao {
    Session getSession();

    @Query
    List getCreditTermList();

    @Query
    List getCreditList(int idClient);
}
