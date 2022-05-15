package com.kerrli.BanksystemJava_4sem.repository;

import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CreditDao {
    Session getSession();

    @Query
    List getCreditTermList();

    @Query
    List getCreditList(int idClient);

    void createCredit(String type, double sum, int idClient, String loginEmployee) throws Exception;

    Map<String, Object> getCreditInfo(int id);
}
