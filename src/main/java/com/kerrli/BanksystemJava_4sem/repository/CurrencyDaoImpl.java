package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Currency;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {
    private Session session;

    public CurrencyDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public List getCurrencyList() {
        Transaction transaction = session.beginTransaction();
        List currencyList = session.createQuery("FROM Currency", Currency.class).getResultList();
        transaction.commit();
        return currencyList;
    }

}
