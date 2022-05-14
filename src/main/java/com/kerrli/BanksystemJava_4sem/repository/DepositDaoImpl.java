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

    @org.springframework.data.jpa.repository.Query
    @Override
    public void closeDeposit(int depositId, String creditAccountNum, String loginEmployee) throws Exception {
        Deposit deposit = session.get(Deposit.class, depositId);
        double sum = new AccountDaoImpl(session).checkBalance(deposit.getMainAccountNum());
        Date capDate = (deposit.getCapDate() == null) ? deposit.getOpenDate() : deposit.getCapDate();
        String queryString = "FROM OperDate WHERE current = 1";
        OperDate operDate = session.createQuery(queryString, OperDate.class).getSingleResult();
        Account debitBankAccount = new OperationDaoImpl(session).getBankAccount(
                deposit.getMainAccountNum(), "70601%0001");
        double oldSum = new AccountDaoImpl(session).checkBalance(deposit.getPercAccountNum());
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            // "возврат" начисленных процентов после последней капитализации
            if (oldSum > 0) {
                new OperationDaoImpl(session).transaction(debitBankAccount.getAccountNum(), deposit.getPercAccountNum(),
                        oldSum, loginEmployee);
            }
            // после последней капитализации начисляем по ставке "до востребования"
            if (operDate.getOperDate().after(capDate)) {
                DepositTerm dv = session.get(DepositTerm.class, "dv");
                double mainAccountBalance = new AccountDaoImpl(session).checkBalance(deposit.getMainAccountNum());
                double dvSum = Lib.roundSum(mainAccountBalance * Lib.diffDate(capDate, operDate.getOperDate()) *
                        dv.getRate() / 100 / 365);
                new OperationDaoImpl(session).transaction(deposit.getPercAccountNum(),
                        debitBankAccount.getAccountNum(), dvSum, loginEmployee);
                new OperationDaoImpl(session).transaction(deposit.getMainAccountNum(),
                        deposit.getPercAccountNum(), dvSum, loginEmployee);
                sum += dvSum;
            }
            // вывод средств со вклада
            new OperationDaoImpl(session).transaction(creditAccountNum, deposit.getMainAccountNum(),
                    sum, loginEmployee);
            new AccountDaoImpl(session).closeAccount(deposit.getMainAccountNum());
            new AccountDaoImpl(session).closeAccount(deposit.getPercAccountNum());
            deposit.setCloseDate(operDate.getOperDate());
            session.merge(deposit);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
    }

    @org.springframework.data.jpa.repository.Query
    @Override
    public void updateDeposit(int depositId, Date newDate, String loginEmployee) throws Exception {
        Deposit deposit = session.get(Deposit.class, depositId);
        DepositTerm depositTerm = session.get(DepositTerm.class, deposit.getType());
        Account debitBankAccount = new OperationDaoImpl(session).getBankAccount(
                deposit.getMainAccountNum(), "70606%0001");
        // даты капитализации
        List capDateList = new ArrayList();
        if (depositTerm.getCap().compareTo("") == 0) {
            capDateList.add(Lib.addMonths(deposit.getOpenDate(), depositTerm.getMonthCnt()));
        }
        else if (depositTerm.getCap().compareTo("+1 month") == 0) {
            for (int i = 0; i < depositTerm.getMonthCnt(); i++)
                capDateList.add(Lib.addMonths(deposit.getOpenDate(), i + 1));
        }
        else if (depositTerm.getCap().compareTo("+3 month") == 0) {
            for (int i = 0; i < depositTerm.getMonthCnt(); i += 3)
                capDateList.add(Lib.addMonths(deposit.getOpenDate(), i + 3));
        }
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            int i = 0;
            while (i < capDateList.size() &&
                    (((Date) capDateList.get(i)).before(deposit.getUpDate()) ||
                    ((Date) capDateList.get(i)).equals(deposit.getUpDate())))
                i++;
            Date lastCapDate = null;
            while (i < capDateList.size() &&
                    (((Date) capDateList.get(i)).before(newDate) ||
                    ((Date) capDateList.get(i)).equals(newDate))) {
                double mainSum = new AccountDaoImpl(session).checkBalance(deposit.getMainAccountNum());
                int cntDays = Lib.diffDate(deposit.getUpDate(), (Date) capDateList.get(i));
                double sum = Lib.roundSum(mainSum * cntDays * depositTerm.getRate() / 100 / 365);
                new OperationDaoImpl(session).transaction(deposit.getPercAccountNum(),
                        debitBankAccount.getAccountNum(), sum, loginEmployee);
                double percSum = new AccountDaoImpl(session).checkBalance(deposit.getPercAccountNum());
                new OperationDaoImpl(session).transaction(deposit.getMainAccountNum(),
                        deposit.getPercAccountNum(), percSum, loginEmployee);
                deposit.setUpDate((Date) capDateList.get(i));
                lastCapDate = (Date) capDateList.get(i);
                i++;
            }
            // начисление остатка после последней выполненной капитализации
            if (i < capDateList.size()) {
                double mainSum = new AccountDaoImpl(session).checkBalance(deposit.getMainAccountNum());
                int cntDays = Lib.diffDate(deposit.getUpDate(), newDate);
                double sum = Lib.roundSum(mainSum * cntDays * depositTerm.getRate() / 100 / 365);
                if (sum != 0.00) {
                    new OperationDaoImpl(session).transaction(deposit.getPercAccountNum(),
                            debitBankAccount.getAccountNum(), sum, loginEmployee);
                }
                deposit.setUpDate(newDate);
            }
            deposit.setCapDate(lastCapDate);
            session.merge(deposit);
            LibTransaction.commitTransaction(session, transaction);
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw e;
        }
    }
}
