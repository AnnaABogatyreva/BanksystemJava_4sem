package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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

    private void updateBalance() throws Exception {
        String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
        OperDate oldOperDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        queryString = "SELECT a FROM Account a " +
                "WHERE a.accountNum IN " +
                "(SELECT DISTINCT o.debitAccountNum FROM Operation o WHERE o.operDate >= :operDate)" +
                "OR a.accountNum IN " +
                "(SELECT DISTINCT o.creditAccountNum FROM Operation o WHERE o.operDate >= :operDate)";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("operDate", oldOperDate.getOperDate());
        List accountList = query.getResultList();
        for (Object object: accountList) {
            Account account = (Account) object;
            double sum = new AccountDaoImpl(session).checkBalance(account.getAccountNum());
            AccountType accountType = session.get(AccountType.class, account.getAcc2p());
            sum *= accountType.getSign();
            Balance balance = new Balance(account.getAccountNum(), oldOperDate.getOperDate(), sum);
            try {
                session.merge(balance);
            }
            catch (Exception e) {
                throw new Exception("Ошибка сохранения баланса по счету " + account.getAccountNum() + ". " +
                        e.getMessage());
            }
        }
    }

    private void updateDate(Date date) throws Exception {
        String queryString = "UPDATE OperDate o SET o.current = 0 WHERE o.current = 1";
        Query query = session.createQuery(queryString);
        query.executeUpdate();
        OperDate operDate = new OperDate(date, 1);
        try {
            session.merge(operDate);
        }
        catch (Exception e) {
            throw new Exception("Ошибка установки новой даты операционного дня. " + e.getMessage());
        }
    }

    @Override
    public void changeOperDate(Date date) throws Exception {
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
            OperDate oldOperDate = session.createQuery(queryString, OperDate.class).getSingleResult();
            if (!oldOperDate.getOperDate().before(date)) {
                throw new Exception("Некорректная новая дата. ");
            }
            updateBalance();
            updateDate(date);
            // !!! deposits and credits
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
    }
}
