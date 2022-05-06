package com.kerrli.BanksystemJava_4sem.dao;

import com.kerrli.BanksystemJava_4sem.entity.Employee;

public interface EmployeeDao {
    public Employee findByLogin(String login);
}
