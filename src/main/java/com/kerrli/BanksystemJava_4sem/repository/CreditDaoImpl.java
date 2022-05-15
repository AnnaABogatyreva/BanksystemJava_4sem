package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class CreditDaoImpl implements CreditDao {
    private final Session session;

    public CreditDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public CreditDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getCreditTermList() {
        return session.createQuery("FROM CreditTerm", CreditTerm.class).getResultList();
    }

    private String getSelectBlockLine(Credit credit) {
        CreditTerm creditTerm = session.get(CreditTerm.class, credit.getType());
        AccountDaoImpl accountDao = new AccountDaoImpl(session);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(credit.getOpenDate());
        calendar.add(Calendar.MONTH, creditTerm.getMonthCnt());
        Date endDate = calendar.getTime();
        Currency currency = session.get(Currency.class, "810");
        String queryString = "SELECT o FROM Operation o WHERE o.creditAccountNum = :account ORDER BY o.idOper";
        Query query = session.createQuery(queryString, Operation.class);
        query.setParameter("account", credit.getMainDutyAccountNum());
        query.setMaxResults(1);
        Operation operation = (Operation) query.getSingleResult();
        return creditTerm.getDescript() + ", " + Lib.formatSum(creditTerm.getRate()) + "% годовых, сумма " +
                Lib.formatSum(operation.getSum()) + " " + currency.getIsoCode() +
                ", окончание " + Lib.formatDate(endDate, "dd.MM.yyyy");
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getCreditList(int idClient) {
        String queryString = "FROM Credit WHERE idClient = :idClient AND closeDate IS NULL";
        Query query = session.createQuery(queryString, Credit.class);
        query.setParameter("idClient", idClient);
        List creditList = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < creditList.size(); i++) {
            Credit credit = (Credit) creditList.get(i);
            CreditExt creditExt = new CreditExt(credit, getSelectBlockLine(credit));
            res.add(creditExt);
        }
        return res;
    }
}
