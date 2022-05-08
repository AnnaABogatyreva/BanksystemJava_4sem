package com.kerrli.BanksystemJava_4sem.service;

import com.kerrli.BanksystemJava_4sem.dao.EmployeeDaoImpl;
import com.kerrli.BanksystemJava_4sem.entity.Employee;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private EmployeeDaoImpl employeeDao;

    public EmployeeService() {
        employeeDao = new EmployeeDaoImpl();
    }

    public Employee findUser(String login) {
        return employeeDao.findByLogin(login);
    }

    public boolean checkPassword(String login, String password) {
        Employee employee = employeeDao.findByLogin(login);
        return employee.getPassword().compareTo(Employee.hashPassword(password)) == 0;
    }
}
