package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Converter;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class AccountantDaoImpl implements AccountantDao {
    private Session session;

    public AccountantDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public AccountantDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setCourse(String currency, double buy, double cost, double sell) throws Exception {
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            String queryString = "UPDATE Converter c SET c.current = 0 WHERE c.currency = :currency AND c.current = 1";
            Query query = session.createQuery(queryString);
            query.setParameter("currency", currency);
            query.executeUpdate();
            Date date = Lib.getTempDate(session);
            Converter converter = new Converter(currency, buy, cost, sell, date, 1);
            session.merge(converter);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
    }
}
