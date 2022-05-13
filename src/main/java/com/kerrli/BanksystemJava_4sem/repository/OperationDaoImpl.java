package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.*;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import com.kerrli.BanksystemJava_4sem.util.LibTransaction;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Repository
public class OperationDaoImpl implements OperationDao {
    private Session session;

    public OperationDaoImpl() {
        this.session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public OperationDaoImpl(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void transaction(String debitAccountNum, String creditAccountNum,
                            double sum, String loginEmployee) throws Exception {
        sum = Lib.roundSum(sum);
        Account debitAccount = session.get(Account.class, debitAccountNum);
        Account creditAccount = session.get(Account.class, creditAccountNum);
        AccountType debitAccountType = session.get(AccountType.class, debitAccount.getAcc2p());
        AccountType creditAccountType = session.get(AccountType.class, creditAccount.getAcc2p());
        double debitBalance = new AccountDaoImpl(session).checkBalance(debitAccountNum);
        double creditBalance = new AccountDaoImpl(session).checkBalance(creditAccountNum);
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            if (debitAccountNum.compareTo(creditAccountNum) == 0)
                throw new Exception("Выберите разные счета. ");
            if (debitAccount == null || debitAccount.getClosed() != null)
                throw new Exception("Счет дебета " + debitAccountNum + " не существует или закрыт. ");
            if (creditAccount == null || creditAccount.getClosed() != null)
                throw new Exception("Счет кредита " + creditAccountNum + " не существует или закрыт. ");
            if (debitAccount.getCurrency().compareTo(creditAccount.getCurrency()) != 0)
                throw new Exception("Валюты счетов не совпадают: " +
                        debitAccount.getCurrency() + " != " + creditAccount.getCurrency() + ". ");
            if (debitAccountType.getSign() > 0 && debitBalance < sum)
                throw new Exception("Недостаточно средств на счете " + debitAccountNum + ". ");
            if (creditAccountType.getSign() < 0 && creditBalance < sum)
                throw new Exception("Недостаточно средств на счете " + creditAccountNum + ". ");
            if (sum < 0.00)
                throw new Exception("Сумма проводки должна быть больше нуля: (" +
                        debitAccountNum + ", " + creditAccountNum + "): " + Lib.formatSum(sum) + ". ");
            if (sum > 0.00) {
                String queryString = "SELECT o FROM OperDate o WHERE o.current = 1";
                OperDate operDate = session.createQuery(queryString, OperDate.class).getSingleResult();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
                Date date = Lib.addTime(operDate.getOperDate(), calendar.getTime());
                Operation operation = new Operation(debitAccountNum, creditAccountNum, date, sum, loginEmployee);
                session.merge(operation);
            }
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
        LibTransaction.commitTransaction(session, transaction);
    }

    @Override
    public void convertation(String debitAccountNum, String creditAccountNum,
                             double sum, String loginEmployee) throws Exception {
        sum = Lib.roundSum(sum);
        Account debitAccount = session.get(Account.class, debitAccountNum);
        Account creditAccount = session.get(Account.class, creditAccountNum);
        boolean transaction = LibTransaction.beginTransaction(session);
        try {
            double addSum = sum;
            if (debitAccount.getCurrency().compareTo("810") == 0) {
                Account debitBankAccount = getBankAccount(debitAccountNum, "70601810%0001");
                transaction(debitBankAccount.getAccountNum(), debitAccountNum, addSum, loginEmployee);
            } else {
                String queryString = "SELECT c FROM Converter c WHERE c.current = 1 AND c.currency = :currency";
                Query query = session.createQuery(queryString, Converter.class);
                query.setParameter("currency", debitAccount.getCurrency());
                Converter converter = (Converter) query.getSingleResult();
                Account debitBankAccount = getBankAccount(debitAccountNum,
                        "70601" + debitAccount.getCurrency() + "%0001");
                addSum = Lib.roundSum(addSum * converter.getBuy());
                transaction(debitBankAccount.getAccountNum(), debitAccountNum, sum, loginEmployee);
            }
            if (creditAccount.getCurrency().compareTo("810") == 0) {
                Account creditBankAccount = getBankAccount(creditAccountNum, "70601810%0001");
                transaction(creditAccountNum, creditBankAccount.getAccountNum(), addSum, loginEmployee);
            } else {
                String queryString = "SELECT c FROM Converter c WHERE c.current = 1 AND c.currency = :currency";
                Query query = session.createQuery(queryString, Converter.class);
                query.setParameter("currency", creditAccount.getCurrency());
                Converter converter = (Converter) query.getSingleResult();
                Account creditBankAccount = getBankAccount(creditAccountNum,
                        "70601" + creditAccount.getCurrency() + "%0001");
                addSum = Lib.roundSum(addSum / converter.getSell());
                transaction(creditAccountNum, creditBankAccount.getAccountNum(), addSum, loginEmployee);
            }
        }
        catch (Exception e) {
            LibTransaction.rollbackTransaction(session, transaction);
            throw new Exception(e.getMessage());
        }
        LibTransaction.commitTransaction(session, transaction);
    }

    @Override
    public Account getBankAccount(String accountNum, String mask) throws Exception {
        Account account = session.get(Account.class, accountNum);
        String queryString = "SELECT a FROM Account a " +
                "WHERE a.idClient = 1 AND a.accountNum LIKE :mask AND a.closed IS NULL AND a.currency = :currency";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("mask", mask);
        query.setParameter("currency", account.getCurrency());
        query.setMaxResults(1);
        Account bankAccount = (Account) query.getSingleResult();
        return bankAccount;
    }

    @Override
    public Account getDefaultAccount(Client client, String currency) throws Exception {
        String queryString = "SELECT a FROM Account a " +
                "WHERE a.idClient = :idClient AND a.currency = :currency AND a.closed IS NULL AND a.def = 1";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("idClient", client.getId());
        query.setParameter("currency", currency);
        query.setMaxResults(1);
        Account account = null;
        try {
            account = (Account) query.getSingleResult();
        }
        catch (NoResultException e) {
            queryString = "SELECT a FROM Account a WHERE a.idClient = :idClient AND a.closed IS NULL AND a.def = 1";
            query = session.createQuery(queryString, Account.class);
            query.setParameter("idClient", client.getId());
            query.setMaxResults(1);
            try {
                account = (Account) query.getSingleResult();
            }
            catch (Exception ex) {
                throw new Exception("У клиента нет подходящего счета для принятия перевода. " + ex.getMessage());
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return account;
    }


}
