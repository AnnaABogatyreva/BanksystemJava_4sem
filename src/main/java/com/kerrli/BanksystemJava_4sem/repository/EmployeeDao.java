package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EmployeeDao {

    Session getSession();
    
    Employee findByLogin(String login);

    Date getOperDate();
}
