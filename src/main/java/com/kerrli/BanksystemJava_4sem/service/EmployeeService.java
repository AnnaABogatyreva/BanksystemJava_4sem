package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.repository.EmployeeDaoImpl;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmployeeService {
    private EmployeeDaoImpl employeeDao;

    public EmployeeService() {
        employeeDao = new EmployeeDaoImpl();
    }

    public EmployeeService(EmployeeDaoImpl employeeDao) {
        this.employeeDao = employeeDao;
    }

    public EmployeeDaoImpl getEmployeeDao() {
        return employeeDao;
    }

    public Employee findUser(String login) {
        return employeeDao.findByLogin(login);
    }

    public boolean checkPassword(String login, String password) {
        Employee employee = employeeDao.findByLogin(login);
        return employee.getPassword().compareTo(Employee.hashPassword(password)) == 0;
    }

    public Date getOperDate() {
        return employeeDao.getOperDate();
    }
}
