package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Employee;
import com.kerrli.BanksystemJava_4sem.entity.OperDate;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.management.Query;
import java.util.Date;

@Repository
public class EmployeeDaoImpl implements EmployeeDao {
    private Session session;

    public EmployeeDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public EmployeeDaoImpl(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public Employee findByLogin(String login) {
        Employee employee = session.get(Employee.class, login);
        return employee;
    }

    @Override
    public Date getOperDate() {
        String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
        OperDate operDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        return operDate.getOperDate();
    }
}
