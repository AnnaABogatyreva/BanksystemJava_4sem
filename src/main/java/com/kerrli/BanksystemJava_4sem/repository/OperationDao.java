package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Operation;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationDao {
    Session getSession();

    void transaction(String debitAccountNum, String creditAccountNum,
                     double sum, String loginEmployee) throws Exception;
}
