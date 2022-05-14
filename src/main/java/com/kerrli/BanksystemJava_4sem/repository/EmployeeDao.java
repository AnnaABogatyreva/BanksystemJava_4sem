package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EmployeeDao {
    Session getSession();

    @Query
    Employee findByLogin(String login);

    @Query
    Date getOperDate();
}
