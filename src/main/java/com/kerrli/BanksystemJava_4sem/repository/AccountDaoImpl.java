package com.kerrli.BanksystemJava_4sem.repository;

import com.kerrli.BanksystemJava_4sem.entity.Account;
import com.kerrli.BanksystemJava_4sem.entity.AccountCnt;
import com.kerrli.BanksystemJava_4sem.entity.AccountType;
import com.kerrli.BanksystemJava_4sem.entity.Currency;
import com.kerrli.BanksystemJava_4sem.util.HibernateSessionFactoryUtil;
import com.kerrli.BanksystemJava_4sem.util.Lib;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class AccountDaoImpl implements AccountDao {
    private Session session;

    public AccountDaoImpl() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public boolean startTransaction() {
        Transaction transaction;
        boolean canStopTransaction;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
            canStopTransaction = true;
        }
        else {
            transaction = session.getTransaction();
            canStopTransaction = false;
        }
        return canStopTransaction;
    }

    @Override
    public void stopTransaction(boolean canStopTransaction) {
        if (canStopTransaction)
            session.getTransaction().commit();
    }

    @Override
    public String generateAccountNum(String acc2p, String currencyCode) {
        boolean transaction = startTransaction();
        int cnt;
        try {
            String queryString = "FROM AccountCnt WHERE acc2p = :acc2p AND currency = :currency";
            Query query = session.createQuery(queryString, AccountCnt.class);
            query.setParameter("acc2p", acc2p);
            query.setParameter("currency", currencyCode);
            AccountCnt accountCnt = (AccountCnt) query.getSingleResult();
            cnt = accountCnt.getCnt();
        }
        catch (Exception e) {
            cnt = 0;
        }
        // Структура банковского счета:
        // 40817 - счет второго порядка
        // 810 - валюта
        // 1 - проверочный код
        // 0000 - отделение банка (0000 - головной офис)
        // 0000001 - порядковый номер счета банка
        cnt++;
        String accountNum = acc2p + currencyCode + "20000" + String.format("%07d", cnt);
        AccountCnt accountCnt = new AccountCnt(acc2p, currencyCode, cnt);
        session.merge(accountCnt);
        stopTransaction(transaction);
        return accountNum;
    }

    @Override
    public Account createAccount(int idClient, String currencyCode, String acc2p, String descript) {
        boolean transaction = startTransaction();
        String accountNum = generateAccountNum(acc2p, currencyCode);
        String queryString = "SELECT COUNT(a) FROM Account a " +
                "WHERE idClient = :idClient AND closed IS NULL AND currency = :currency";
        Query query = session.createQuery(queryString);
        query.setParameter("idClient", idClient);
        query.setParameter("currency", currencyCode);
        Long cnt = (Long) query.list().get(0);
        int def = (cnt > 0) ? 0 : 1;
        Account account = new Account(idClient, accountNum, currencyCode, descript, null, def);
        session.merge(account);
        stopTransaction(transaction);
        return account;
    }

    @Override
    public double checkBalance(String accountNum) {
        String queryString = "SELECT b.sum, b.date FROM Balance b " +
                "WHERE b.accountNum = :accountNum ORDER BY b.date DESC";
        Query query = session.createQuery(queryString);
        query.setParameter("accountNum", accountNum);
        query.setMaxResults(1);
        List list = (List) query.list();
        double sum = 0;
        Date date = Lib.parseDate("1970-01-01");
        try {
            sum = (Double) list.get(0);
            date = (Date) list.get(1);
        }
        catch (Exception e) {}
        System.out.println("sum = " + sum + "\ndate = " + date); /// !!!
        queryString = "SELECT t FROM AccountType t WHERE acc2p = :acc2p";
        query = session.createQuery(queryString);
        query.setParameter("acc2p", accountNum.substring(0, 5));
        AccountType accountType = (AccountType) query.getSingleResult();
        int sign = accountType.getSign();
        sum *= sign;
        queryString = "SELECT -1 * SUM(o.sum) FROM Operation o " +
                "WHERE o.operDate > :operDate AND o.debitAccountNum = :debitAccountNum";
        query = session.createQuery(queryString);
        query.setParameter("operDate", Lib.addTime(date, "23:59:59"));
        query.setParameter("debitAccountNum", accountNum);
        try {
            sum += (Double) query.getSingleResult() * sign;
        }
        catch (Exception e) {}
        queryString = "SELECT 1 * SUM(o.sum) FROM Operation o " +
                "WHERE o.operDate > :operDate AND o.creditAccountNum = :creditAccountNum";
        query = session.createQuery(queryString);
        query.setParameter("operDate", Lib.addTime(date, "23:59:59"));
        query.setParameter("creditAccountNum", accountNum);
        try {
            sum += (Double) query.getSingleResult() * sign;
        }
        catch (Exception e) {}
        return Lib.roundSum(sum);
    }

    @Override
    public String getSelectBlockLine(Account account) {
        Currency currency = session.get(Currency.class, account.getCurrency());
        AccountType accountType = session.get(AccountType.class, account.getAcc2p());
        String res = "Счет №" + account.getAccountNum() + ": " + Lib.formatSum(checkBalance(account.getAccountNum())) +
                " " + currency.getIsoCode() + ", " + account.getDescript() + " (" + accountType.getDispType() + ")";
        return res;
    }

    @Override
    public Account closeAccount(String accountNum) {
        boolean transaction = startTransaction();

        stopTransaction(transaction);
        return null;
    }

    @Override
    public List getZeroAccountList(int idClient) {
        String queryString = "FROM Account a WHERE a.closed IS NULL AND a.idClient = :idClient";
        Query query = session.createQuery(queryString, Account.class);
        query.setParameter("idClient", idClient);
        List list = query.getResultList();
        List res = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Account account = (Account) list.get(i);
            double balance = checkBalance(account.getAccountNum());
            if (Math.abs(balance) < 0.005) {
                account.setDescript(getSelectBlockLine(account));
                res.add(account);
            }

        }
        return res;
    }
}
