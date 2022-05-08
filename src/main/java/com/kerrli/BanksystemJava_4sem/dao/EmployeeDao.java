package com.kerrli.BanksystemJava_4sem.dao;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDao {
    Employee findByLogin(String login);
}
