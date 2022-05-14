package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class DepositDaoImpl implements DepositDao {
    private final Session session;

    public DepositDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public DepositDaoImpl(Session session) {
        this.session = session;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public Session getSession() {
        return null;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getDepositTermList() {
        String queryString = "FROM DepositTerm WHERE type != 'dv'";
        return session.createQuery(queryString, DepositTerm.class).getResultList();
    }

    private String getSelectBlockLine(Deposit deposit) {
        DepositTerm depositTerm = session.get(DepositTerm.class, deposit.getType());
        AccountDaoImpl accountDao = new AccountDaoImpl(session);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deposit.getOpenDate());
        calendar.add(Calendar.MONTH, depositTerm.getMonthCnt());
        Date endDate = calendar.getTime();
        Currency currency = session.get(Currency.class, depositTerm.getCurrency());
        return depositTerm.toString() + ", сумма " +
                Lib.formatSum(accountDao.checkBalance(deposit.getMainAccountNum())) + " " + currency.getIsoCode() +
                ", окончание " + Lib.formatDate(endDate, "dd.MM.yyyy");
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public List getDepositList(int idClient) {
        String queryString = "FROM Deposit WHERE idClient = :idClient AND closeDate IS NULL";
        Query query = session.createQuery(queryString, Deposit.class);
        query.setParameter("idClient", idClient);
        List depositList = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < depositList.size(); i++) {
            Deposit deposit = (Deposit) depositList.get(i);
            DepositExt depositExt = new DepositExt(deposit, getSelectBlockLine(deposit));
            res.add(depositExt);
        }
        return res;
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public void createDeposit(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception {
        DepositTerm depositTerm = session.get(DepositTerm.class, type);
        Account debitAccount = session.get(Account.class, debitAccountNum);
        if (depositTerm.getCurrency().compareTo(debitAccount.getCurrency()) != 0) {
            throw new Exception("Валюты выбранного вклада и счета не совпадают. ");
        }
        boolean transaction = LibTransaction.beginTransaction(session);
        AccountDaoImpl accountDao = new AccountDaoImpl(session);
        Account mainAccount = accountDao.createAccount(debitAccount.getIdClient(), debitAccount.getCurrency(),
                "42301", "Основной счет вклада");
        Account percAccount = accountDao.createAccount(debitAccount.getIdClient(), debitAccount.getCurrency(),
                "47411", "Дополнительный счет вклада");
        OperationDaoImpl operationDao = new OperationDaoImpl(session);
        try {
            operationDao.transaction(mainAccount.getAccountNum(), debitAccountNum, sum, loginEmployee);
            OperDate tempDate = session.createQuery("FROM OperDate WHERE current = 1",
                    OperDate.class).getSingleResult();
            Deposit deposit = new Deposit(debitAccount.getIdClient(), type, tempDate.getOperDate(), null,
                    mainAccount.getAccountNum(), percAccount.getAccountNum(), tempDate.getOperDate(), null);
            session.merge(deposit);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
    }
}
