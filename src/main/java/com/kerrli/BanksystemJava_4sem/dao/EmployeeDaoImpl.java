package com.kerrli.BanksystemJava_4sem.dao;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;

public class EmployeeDaoImpl implements EmployeeDao {
    @Override
    public Employee findByLogin(String login) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Employee.class, login);
    }
}
