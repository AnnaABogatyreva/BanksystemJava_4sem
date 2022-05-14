package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.Deposite;
import com.kerrli.BanksystemJava_4sem.entity.DepositeTerm;
import com.kerrli.BanksystemJava_4sem.entity.OperDate;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class DepositeDaoImpl implements DepositeDao {
    private final Session session;

    public DepositeDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public DepositeDaoImpl(Session session) {
        this.session = session;
    }

    @Query
    @Override
    public Session getSession() {
        return null;
    }

    @Query
    @Override
    public List getDepositeList() {
        String queryString = "FROM DepositeTerm WHERE type != 'dv'";
        return session.createQuery(queryString, DepositeTerm.class).getResultList();
    }

    @Query
    @Override
    public void createDeposite(String type, String debitAccountNum, double sum, String loginEmployee) throws Exception {
        DepositeTerm depositeTerm = session.get(DepositeTerm.class, type);
        Account debitAccount = session.get(Account.class, debitAccountNum);
        if (depositeTerm.getCurrency().compareTo(debitAccount.getCurrency()) != 0) {
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
            Deposite deposite = new Deposite(debitAccount.getIdClient(), type, tempDate.getOperDate(), null,
                    mainAccount.getAccountNum(), percAccount.getAccountNum(), tempDate.getOperDate(), null);
            session.merge(deposite);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
    }
}
