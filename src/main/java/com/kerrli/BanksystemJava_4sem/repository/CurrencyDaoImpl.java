package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Currency;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {
    private final Session session;

    public CurrencyDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public CurrencyDaoImpl(Session session) {
        this.session = session;
    }

    @Query
    @Override
    public Session getSession() {
        return session;
    }

    @Query
    @Override
    public List getCurrencyList() {
        return session.createQuery("FROM Currency", Currency.class).getResultList();
    }

    @Query
    @Override
    public List getForeignCurrencyList() {
        return session.createQuery("FROM Currency c WHERE c.code != '810'",
                Currency.class).getResultList();
    }

}
