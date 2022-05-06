package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.dao.EmployeeDaoImpl;
import com.kerrli.BanksystemJava_4sem.entity.Employee;

public class EmployeeService {
    private EmployeeDaoImpl employeeDao = new EmployeeDaoImpl();

    public EmployeeService() {}

    public Employee findUser(String login) {
        return employeeDao.findByLogin(login);
    }
}
